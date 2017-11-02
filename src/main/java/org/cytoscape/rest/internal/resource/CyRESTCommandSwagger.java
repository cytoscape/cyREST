package org.cytoscape.rest.internal.resource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cytoscape.ci.model.CIError;
import org.cytoscape.ci.model.CIResponse;
import org.cytoscape.command.AvailableCommands;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.SavePolicy;
import org.cytoscape.rest.internal.commands.resources.CommandResource;
import org.cytoscape.rest.internal.task.ResourceManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.util.ListMultipleSelection;
import org.cytoscape.work.util.ListSingleSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Inject;

import io.swagger.annotations.ExternalDocs;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.config.ReaderListener;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.Operation;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.StringProperty;
import io.swagger.converter.ModelConverters;
import io.swagger.util.Json;


@Path("/v1/commands/swagger.json")
@Singleton
public class CyRESTCommandSwagger extends AbstractResource
{
	private static final Logger logger = LoggerFactory.getLogger(CyRESTCommandSwagger.class);
	
	
	@Inject
	@NotNull
	private AvailableCommands available;

	private String swaggerDefinition;
	
	public CyRESTCommandSwagger()
	{
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

	private Model ciResponseModel;
	
	protected void buildSwagger()
	{
		BeanConfig commandBeanConfig = new BeanConfig(){
			public Set<Class<?>> classes()
			{
				Set<Class<?>> classes = new HashSet<Class<?>>();
				classes.add(CommandResource.class);
				classes.add(CyRESTCommandSwaggerConfig.class);
				return classes;
			}
		};

		commandBeanConfig.setHost(ResourceManager.HOST + ":" +  cyRESTPort);
		commandBeanConfig.setScan(true);
		commandBeanConfig.setPrettyPrint(true);

		Swagger swagger = commandBeanConfig.getSwagger();
		
		Map<String, Model> ciResponseModels = ModelConverters.getInstance().read(CIResponse.class);
		ciResponseModel = ciResponseModels.get("CIResponse");
		
		Map<String, Model> ciErrorModels = ModelConverters.getInstance().read(CIError.class);
		Model ciErrorModel = ciErrorModels.get("CIError");
		swagger.addDefinition("CIError", ciErrorModel);
		
		addCommandPaths(swagger);
		// serialization of the Swagger definition
		try 
		{
			Json.mapper().enable(SerializationFeature.INDENT_OUTPUT);
			this.swaggerDefinition = Json.mapper().writeValueAsString(swagger);
		
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Scans the AvailableCommands service, translates available commands and parameters into their Swagger annotation 
	 * equivalents, and adds them to the passed Swagger instance.
	 * 
	 * @param swagger
	 */
	private void addCommandPaths(Swagger swagger)
	{
		//Set<String> classes = new HashSet<String>();

		if (available != null)	{

			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {

						boolean resetNetwork = setCurrentNetwork();
						boolean resetView = setCurrentNetworkView();
						try {
							for (String namespace : available.getNamespaces()) {
								for (String command : available.getCommands(namespace)) {
									io.swagger.models.Path testPath = new io.swagger.models.Path();
									Operation operation = new Operation();
									operation.addTag(namespace);
									operation.setOperationId(namespace + "/" + command);
									operation.setSummary(available.getDescription(namespace, command));

									operation.setDescription(available.getLongDescription(namespace, command));

									Response response = new Response();
									response.setDescription("successful operation");

									boolean isJSONCapable = setSuccessfulResponse(namespace, command, available, response);

									operation.addResponse("200", response);
									if (isJSONCapable) {
										setPostParameters(namespace, command, available, operation);
										operation.addProduces(MediaType.APPLICATION_JSON);
										testPath.setPost(operation);
									} else {
										setGetParameters(namespace, command, available, operation);
										operation.addProduces(MediaType.TEXT_PLAIN);
										testPath.setGet(operation);
									}
									swagger.path("/v1/commands/" + namespace + "/" + command, testPath);
								
								}
							}
						}
						finally
						{
							resetCurrentNetworkView(resetView);
							resetCurrentNetwork(resetNetwork);
						}
					}
				});
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 

		}
		else
		{
			System.out.println("CyRESTCommandSwagger availableCommands is null");
		}
	}

	/**
	 * 
	 * @return true if this command returns JSON.
	 */
	private boolean setSuccessfulResponse(String namespace, String command, AvailableCommands available, Response response) {

		boolean isJSONCapable = available.getSupportsJSON(namespace, command);
		
		if (isJSONCapable) {

			String jsonExample = available.getExampleJSON(namespace, command);
			
			ObjectProperty objectProperty = new ObjectProperty();
		
			ObjectMapper objectMapper = new ObjectMapper();
			objectProperty.setProperties(ciResponseModel.getProperties());
			JsonNode jsonNode;
			try {
				jsonNode = objectMapper.readValue(CommandResource.getJSONResponse(Arrays.asList(jsonExample), new ArrayList<CIError>(), null), JsonNode.class);
				if (!containsNull(jsonNode)) {
					objectProperty.setExample(jsonNode); 
				} else {
					reportJSONExampleError(new Exception("Swagger Definition Contained a null value: " + objectMapper.writeValueAsString(jsonNode)), namespace, command, "Invalid for Swagger (JSON contained null)", objectMapper, objectProperty);
				}
			} catch (JsonParseException e) {
				reportJSONExampleError(e, namespace, command, "JsonParseException", objectMapper, objectProperty);
			} catch (JsonMappingException e) {			
				reportJSONExampleError(e, namespace, command, "JsonMappingException", objectMapper, objectProperty);
			} catch (IOException e) {
				reportJSONExampleError(e, namespace, command, "IOException", objectMapper, objectProperty);
			}
			response.setSchema(objectProperty);
		}
		else {
			StringProperty stringProperty =  new StringProperty();
			stringProperty.setName("noName");
			response.setSchema(stringProperty); 
		}
		return isJSONCapable;
	}

	private void reportJSONExampleError(Throwable e, String namespace, String command, String string, ObjectMapper objectMapper, ObjectProperty objectProperty) {
		logger.error("Error creating json example for " + namespace + " " + command + " : " + string, e);
		JsonNode jsonNode;
		try {
			jsonNode = objectMapper.readValue("\"ERROR. Example could not be included: " + string + "\"", JsonNode.class);
			objectProperty.setExample(jsonNode); 
		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	 
	}
	
	private void setGetParameters(String namespace, String command, AvailableCommands available, Operation operation) {
		for (String argumentName : available.getArguments(namespace, command))
		{
			QueryParameter parameter = new QueryParameter();
			parameter.setName(argumentName);

			String javaType = available.getArgType(namespace, command, argumentName).toString();

			String longDescription = available.getArgLongDescription(namespace, command, argumentName);
			if (longDescription == null || longDescription.length() == 0) {
				String description = available.getArgDescription(namespace, command, argumentName);
				if (description!= null && description.length() > 0) { 
					longDescription = description;
				} else {
					longDescription = "";
				}
			}

			if (longDescription.length() > 0) {
				longDescription += "\n\n";
			}

			longDescription += "Java Type:\n\n ``` " + javaType + " ```";

			parameter.setDescription(longDescription);

			String example = available.getArgExampleStringValue(namespace, command, argumentName);
			if (example != null && example.length() > 0) {
				parameter.setExample(example);
			}

			Class<?> typeClass = available.getArgType(namespace, command, argumentName);
			setParameterTypeAndFormatFromClass(parameter,typeClass);

			boolean required = available.getArgRequired(namespace, command, argumentName);
			parameter.setRequired(required);

			operation.addParameter(parameter);
		}
	}

	private void setPostParameters(String namespace, String command, AvailableCommands available, Operation operation) {
		BodyParameter parameter = new BodyParameter();
		parameter.setName("body");

		CommandModel model = new CommandModel(namespace, command, available);
		//parameter.setRequired(!model.properties.isEmpty());
		parameter.setSchema(model);
		operation.addParameter(parameter);
	}

	CyNetwork emptyNetwork;
	CyNetworkView emptyView;

	// This is a wrapper around appMgr.getCurrentNetwork() to make sure
	// we *always* return a network.  If getCurrentNetwork is null, we return
	// the emptyNetwork.
	private CyNetwork getNetwork() {
		if (applicationManager.getCurrentNetwork() != null)
			return applicationManager.getCurrentNetwork();

		if (emptyNetwork == null) {
			emptyNetwork = (networkFactory.createNetwork(SavePolicy.DO_NOT_SAVE));
			//Note: DO NOT CHANGE THE NETWORK NAME FROM "cy:command_documentation_generation", it is necessary for a workaround.
			emptyNetwork.getRow(emptyNetwork).set(CyNetwork.NAME, "cy:command_documentation_generation");
		}
		return emptyNetwork;
	}

	private boolean setCurrentNetwork() {
		if (applicationManager.getCurrentNetwork() == null) {

			getNetwork();
			networkManager.addNetwork(emptyNetwork, true);
			return true;
		}
		return false;
	}

	private void resetCurrentNetwork(boolean reset) {
		if (!reset) return;
		applicationManager.setCurrentNetwork(null);
		networkManager.destroyNetwork(emptyNetwork);
		emptyNetwork = null;
	}

	private CyNetworkView getNetworkView() {
		if (applicationManager.getCurrentNetworkView() != null)
			return applicationManager.getCurrentNetworkView();

		if (emptyView == null) {
			emptyView = (networkViewFactory.createNetworkView(getNetwork()));
		}
		return emptyView;
	}

	private boolean setCurrentNetworkView() {
		if (applicationManager.getCurrentNetworkView() == null) {
			getNetworkView();
			networkViewManager.addNetworkView(emptyView, true);
			return true;
		}
		return false;
	}

	public static boolean containsNull(JsonNode input) {
		if (input.isNull()) {
			return true;
		} else {
			for (JsonNode node :input) {
				if (containsNull(node)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void resetCurrentNetworkView(boolean reset) {
		if (!reset) return;
		applicationManager.setCurrentNetworkView(null);
		networkViewManager.destroyNetworkView(emptyView);
		emptyView = null;
	}

	private final class CommandModel extends ModelImpl {
		
		public CommandModel(String namespace, String command, AvailableCommands available) {
			super();
			for (String argument : available.getArguments(namespace, command)) {
				StringProperty property = new StringProperty();
				
				property.setName(argument);
				String description = available.getArgLongDescription(namespace, command, argument);
				
				String defaultString = available.getArgExampleStringValue(namespace, command, argument);
				if (defaultString != null && defaultString.length() > 0) {
					property.setExample(defaultString);
				}
				
				try {
					Class<?> type = available.getArgType(namespace, command, argument);
		
					//FIXME This calls a deprecated method because any other way of getting the value of a tunable is 
					// extremely complicated.
					Object value = available.getArgValue(namespace, command, argument);
						
					if (type.equals(ListSingleSelection.class) && value != null) {
						List<String> values = new ArrayList<String>();
						for (Object entry : ((ListSingleSelection)value).getPossibleValues()) {
							values.add(entry.toString());
						}
						/*
						if (values.contains(defaultString)) {
							Collections.swap(values, 0, values.indexOf(defaultString));
						}*/
						property.setEnum(values);
					} else if (type.equals(ListMultipleSelection.class) && value != null) {
						StringJoiner joiner = new StringJoiner(", ", " = [", "]");
						for (Object entry : ((ListMultipleSelection)value).getPossibleValues()) {
							joiner.add("'" + entry.toString() + "'");
						}
						description += joiner.toString();
					} else if (type.equals(CyTable.class) ){
						description += " [NodeTable -> Node:NetworkName , EdgeTable -> Edge:NetworkName , NetworkTable -> Network:NetworkName , " +
								"UnassignedTable -> TableFileName]";
					}
					
				} catch (Exception e) {
					logger.error("Error handling command enum", e);
				}
			
				property.setDescription(description);
				
				boolean required = available.getArgRequired(namespace, command, argument);
				
				this.addProperty(argument, property);
				if (required) {
					this.addRequired(argument);
				} 
			}
		}

		@Override
		public String getDescription() {

			return "A list of command arguments";
		}

		@Override
		public String getTitle() {

			return "Command Arguments";
		}
	}

	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public String get() throws JsonProcessingException 
	{
		updateSwagger();
		if (swaggerDefinition == null)
		{
			buildSwagger();
		}
		return swaggerDefinition;
	}

	@SwaggerDefinition(
			info = @Info(
					description = "An API to offer access to Cytoscape command line commands through a REST-like service."
							+ "\n\nIn Cytoscape 3.6, this section is upgraded with support for POST access to Cytoscape commands, offering enhanced documentation as well as JSON output for some commands. The JSON currently provided by commands in this release may be expanded or revised in future releases."
							+ "\n\nAll commands are still available via GET requests, the syntax of which is described in the [Command resources](#!/Commands)",
					version = "V2.0.0",
					title = "CyREST Command API"
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

		}, 
		externalDocs = @ExternalDocs(value = "Cytoscape", url = "http://cytoscape.org/")
	)
	
	public static class CyRESTCommandSwaggerConfig implements ReaderListener
	{
		@Override
		public void beforeScan(Reader arg0, Swagger swagger) 
		{

		}

		public void afterScan(Reader reader, Swagger swagger)
		{

		}
	}

	/**
	 * Sets the Type and Format for the passed QueryParameter based on the passed Class
	 * produced by AvailableCommands
	 * 
	 * Note that this takes into account some basic primitives, plus String, but defaults
	 * to a String type for any other class. This was done because more complex arguments
	 * are generally translated from String using StringTunableHandler.
	 * 
	 * @see org.cytoscape.command.AvailableCommands
	 * @see org.cytoscape.command.StringTunableHandler
	 * 
	 * @param parameter
	 * @param clazz
	 */
	public static void setParameterTypeAndFormatFromClass(QueryParameter parameter, Class<?> clazz)
	{
		//FIXME This shouldn't be producing hard-coded strings here, but instead should 
		//access some Swagger or JSON constant.
		//These were extracted from the Swagger Specification (http://swagger.io/specification/)
		if (int.class.equals(clazz))
		{
			parameter.setType("integer");
			parameter.setFormat("int32");
		}
		else if (long.class.equals(clazz))
		{
			parameter.setType("integer");
			parameter.setFormat("int64");
		}
		else if (float.class.equals(clazz))
		{
			parameter.setType("number");
			parameter.setFormat("int32");
		}
		else if (double.class.equals(clazz))
		{
			parameter.setType("number");
			parameter.setFormat("int64");
		}
		else if (String.class.equals(clazz))
		{
			parameter.setType("string");
		}
		else if (byte.class.equals(clazz))
		{
			parameter.setType("string");
			parameter.setFormat("byte");
		}
		else if (boolean.class.equals(clazz))
		{
			parameter.setType("boolean");
		}
		else
		{	
			parameter.setType("string");
		}
	}
}
