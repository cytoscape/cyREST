package org.cytoscape.rest.internal.resource;

import java.util.HashMap;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cytoscape.ci.CISwaggerConstants;
import org.cytoscape.rest.internal.task.AutomationAppTracker;
import org.cytoscape.rest.internal.task.ResourceManager;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Inject;

import io.swagger.annotations.ExternalDocs;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.config.ReaderListener;
import io.swagger.models.HttpMethod;
import io.swagger.models.Operation;

import io.swagger.models.Response;

import io.swagger.models.Swagger;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.util.Json;

@Path("/v1/swagger.json")
@Singleton
public class CyRESTSwagger extends AbstractResource
{
	@Inject
	@NotNull
	AutomationAppTracker appTracker;
	
	private static final String RESOURCE_URN = "swagger";

	@Override
	public String getResourceURI() {
		return RESOURCE_URN;
	}
	
	private final static Logger logger = LoggerFactory.getLogger(CollectionResource.class);
	
	@Override
	public Logger getResourceLogger() {
		return logger;
	}
	
	private String swaggerDefinition;

	final Set<Class<?>> classes = new HashSet<Class<?>>();

	public void addResource(Class<?> clazz)
	{
		classes.add(clazz);
		updateSwagger();
	}

	public void removeResource(Class<?> clazz)
	{
		classes.remove(clazz);
		updateSwagger();
	}

	public CyRESTSwagger(){
		updateSwagger();
	}

	protected void updateSwagger()
	{
		swaggerDefinition = null;
	}

	public boolean isSwaggerDefinitionNull()
	{
		return (swaggerDefinition == null);
	}

	protected void buildSwagger()
	{
		final Set<Class<?>> classes = new HashSet<Class<?>>(this.classes);
		BeanConfig beanConfig = new BeanConfig(){
			public Set<Class<?>> classes()
			{
				//Set<Class<?>> classes = new HashSet<Class<?>>();
				//classes.addAll();
				classes.add(CyRESTSwaggerConfig.class);
				return classes;
			}
		};

		//FIXME This needs to get set from the ResourceManager
		beanConfig.setHost(ResourceManager.HOST + ":" + cyRESTPort);
		beanConfig.setScan(true);
		beanConfig.setPrettyPrint(true);

		Swagger swagger = beanConfig.getSwagger();

		String automationAppReport = appTracker.getMarkdownReport(); 
		swagger.getInfo().setDescription(SWAGGER_INFO_DESCRIPTION + automationAppReport);
		
		wrapCIResponses(swagger);
		addCommandLinks(swagger);

		// serialization of the Swagger definition
		try 
		{
			Json.mapper().enable(SerializationFeature.INDENT_OUTPUT);
			this.swaggerDefinition = Json.mapper().writeValueAsString(swagger);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void wrapCIResponses(Swagger swagger) {
		Map<String, io.swagger.models.Path> paths = swagger.getPaths();
		if (paths != null)
			for (Map.Entry<String, io.swagger.models.Path> pathEntry : paths.entrySet()) {
				try {
				Map<HttpMethod, Operation> operationMap = pathEntry.getValue().getOperationMap();
				for (Map.Entry<HttpMethod, Operation> operationEntry : operationMap.entrySet()) {

					Object ciExtension = operationEntry.getValue().getVendorExtensions().get(CISwaggerConstants.X_CI_EXTENSION);

					if (ciExtension != null && ciExtension instanceof Map) {
						Map<?,?> map = (Map<?, ?>) ciExtension;
						if (CISwaggerConstants.TRUE.equals(map.get(CISwaggerConstants.CI_EXTENSION_CI_WRAPPING))) {
						
							for (Map.Entry<String, Response> responseEntry : operationEntry.getValue().getResponses().entrySet()) {
								
								//System.out.println("Wrapping " + responseEntry.getKey() + " response for path " + pathEntry.getKey() + " data model:" + responseEntry.getValue().getDescription());

								Map<String, Property> propertyMap = new HashMap<String, Property>();
								propertyMap.put("data", responseEntry.getValue().getSchema());

								RefProperty errorProperty = new RefProperty("#/definitions/CIError");

								propertyMap.put("errors", new ArrayProperty(errorProperty));
								ObjectProperty ciProperty = new ObjectProperty(propertyMap);

								responseEntry.getValue().setSchema(ciProperty);
							}
						}
					}
				}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
	}
					

	public static final String COMMAND_LINK_PREFIX = "\n\nFor a list of all available commands and their documentation, see the [CyREST Command API](";
	
	public static final String COMMAND_LINK_POSTFIX = ")";
	
	private String getCommandLink() {
		String url;
		try {
			url = "http://localhost:"+cyRESTPort+"/v1/swaggerUI/swagger-ui/index.html"
					+ "?url=" + URLEncoder.encode("http://" + ResourceManager.HOST + ":" + this.cyRESTPort + "/v1/commands/swagger.json", "UTF-8");
		
			//TODO this should be done with a string formatting utility.
		return COMMAND_LINK_PREFIX +url + COMMAND_LINK_POSTFIX;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "\n\nUnable to make a hyperlink to the CyREST Command API";
		}
	}
	
	private void addCommandLinks(Swagger swagger) {
		Map<String, io.swagger.models.Path> paths = swagger.getPaths();
		
		if (paths != null) {
			for (Map.Entry<String, io.swagger.models.Path> pathEntry : paths.entrySet()) {
				
				Map<HttpMethod, Operation> operationMap = pathEntry.getValue().getOperationMap();
				for (Map.Entry<HttpMethod, Operation> operationEntry : operationMap.entrySet()) {

					if (operationEntry.getValue().getTags() != null && operationEntry.getValue().getTags().contains(CyRESTSwagger.CyRESTSwaggerConfig.COMMANDS_TAG))
					{
						String description = operationEntry.getValue().getDescription();
						if (description == null) {
							description = "";
						}
						description += getCommandLink();
						operationEntry.getValue().setDescription(description);
					}

					//Should be want to scan descriptions, parameter details, etc. and automatically generate links,
					//this is how it could happen. Note that 
					//operationEntry.getValue().setDescription("Test afterScan replacement.");
				}
			}

		}

	}

	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public String get()
	{
		if (swaggerDefinition == null)
		{
			buildSwagger();
		}
		return swaggerDefinition;
	}

	private static final String SWAGGER_INFO_DESCRIPTION =  "A RESTful service for accessing Cytoscape 3.\n\n";
	
	
	@SwaggerDefinition(
			info = @Info(
					description = "A RESTful service for accessing Cytoscape 3.",
					version = "V2.0.0",
					title = "CyREST API"
					//termsOfService = "http://theweatherapi.io/terms.html",
					// contact = @Contact(
					//   name = "Rain Moore", 
					//    email = "rain.moore@theweatherapi.io", 
					//    url = "http://theweatherapi.io"
					// ),
					// license = @License(
					//    name = "Apache 2.0", 
					//    url = "http://www.apache.org/licenses/LICENSE-2.0"
					// )
					),
			//Be wary of this host parameter if you are using BeanConfig; use one or the other, as they will
			//cause conflicts.
			//host = "localhost:1234",
			basePath = "",
			consumes = {"application/json", "application/xml"},
			produces = {"application/json", "application/xml"},
			schemes = {SwaggerDefinition.Scheme.HTTP},
			tags = 
		{
				@Tag(name = CyRESTSwaggerConfig.COLLECTIONS_TAG),
				@Tag(name = CyRESTSwaggerConfig.COMMANDS_TAG),
				@Tag(name = CyRESTSwaggerConfig.CYTOSCAPE_SYSTEM_TAG),
				@Tag(name = CyRESTSwaggerConfig.GROUPS_TAG),
				@Tag(name = CyRESTSwaggerConfig.LAYOUTS_TAG),
				@Tag(name = CyRESTSwaggerConfig.NETWORKS_TAG),
				@Tag(name = CyRESTSwaggerConfig.NETWORK_VIEWS_TAG),
				@Tag(name = CyRESTSwaggerConfig.PROPERTIES_TAG),
				@Tag(name = CyRESTSwaggerConfig.REST_SERVICE_TAG),
				@Tag(name = CyRESTSwaggerConfig.SESSION_TAG),
				@Tag(name = CyRESTSwaggerConfig.TABLES_TAG),	
				@Tag(name = CyRESTSwaggerConfig.USER_INTERFACE_TAG),			
				@Tag(name = CyRESTSwaggerConfig.VISUAL_PROPERTIES_TAG),
				@Tag(name = CyRESTSwaggerConfig.VISUAL_STYLES_TAG)
		}, 
		externalDocs = @ExternalDocs(value = "Cytoscape", url = "http://cytoscape.org/")
			)
	public static class CyRESTSwaggerConfig implements ReaderListener
	{

		public static final String SESSION_TAG = "Session";
		public static final String APPS_TAG = "Apps";
		public static final String USER_INTERFACE_TAG = "User Interface";
		public static final String NETWORKS_TAG = "Networks";
		public static final String TABLES_TAG = "Tables";
		public static final String COMMANDS_TAG = "Commands";
		public static final String REST_SERVICE_TAG = "REST Service";
		public static final String LAYOUTS_TAG = "Layouts";
		public static final String NETWORK_VIEWS_TAG = "Network Views";
		public static final String PROPERTIES_TAG = "Properties";
		public static final String VISUAL_PROPERTIES_TAG = "Visual Properties";
		public static final String VISUAL_STYLES_TAG = "Visual Styles";
		public static final String GROUPS_TAG = "Groups";
		public static final String COLLECTIONS_TAG = "Collections";
		public static final String CYTOSCAPE_SYSTEM_TAG = "Cytoscape System";

		@Override
		public void beforeScan(Reader arg0, Swagger arg1) 
		{

		}

		public void afterScan(Reader reader, Swagger swagger)
		{
			
		}
	}

	/*
	 * This may need to be changed should we switch from Swagger UI 2.x to 3.x. The 3.x id tags are in the following 
	 * format: operations,get-/v1/networks/{networkId}/views,Network Views
	 */

	public final static String NETWORK_GET_LINK = "[/v1/networks](#!/Networks/getNetworksAsSUID)";
	public final static String NETWORK_VIEWS_LINK = "[/v1/networks/{networkId}/views](#!/Network32Views/getAllNetworkViews)";
}
