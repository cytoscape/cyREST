package org.cytoscape.rest.internal.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cytoscape.ci.model.CIError;
import org.cytoscape.command.AvailableCommands;
import org.cytoscape.rest.internal.commands.resources.CommandResource;
import org.cytoscape.rest.internal.task.ResourceManager;
import org.cytoscape.work.ResultDescriptor;
import org.cytoscape.work.json.JSONResult;

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
import io.swagger.models.ArrayModel;
import io.swagger.models.ComposedModel;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import io.swagger.util.Json;

@Path("/v1/commands/swagger.json")
@Singleton
public class CyRESTCommandSwagger extends AbstractResource
{
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

			for (String namespace : available.getNamespaces())
			{
				for (String command : available.getCommands(namespace))
				{
					io.swagger.models.Path testPath = new io.swagger.models.Path();
					Operation operation = new Operation();
					operation.addTag(namespace);
					operation.setOperationId(namespace + "/" + command);
					operation.setSummary(available.getDescription(namespace, command));

					operation.setDescription(available.getLongDescription(namespace, command));

					//Later, this could be very useful for extracting JSON.
					//operation.addProduces(MediaType.APPLICATION_JSON);


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

		boolean isJSONCapable = false;
		List<ResultDescriptor> resultDescriptors = available.getResultDescriptors(namespace, command);
		List<String> jsonResultExamples = new ArrayList<String>();
		for (ResultDescriptor resultDescriptor : resultDescriptors) {
			List<Class<?>> resultClasses = resultDescriptor.getResultTypes();
			if (resultClasses!=null) {
				for (Class<?> resultClass : resultClasses) {
					if (JSONResult.class.isAssignableFrom(resultClass)){
						isJSONCapable = true;
						JSONResult jsonResult = resultDescriptor.getResultExample(JSONResult.class);
						jsonResultExamples.add(jsonResult.getJSON());
					}
				}
			} else {
				System.out.println("Null result type iterator for " + namespace + " " + command);
			}
		}
		if (isJSONCapable) {

			ObjectProperty objectProperty =  new ObjectProperty();
			objectProperty.setName("newName");

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode;
			try {
				jsonNode = objectMapper.readValue(CommandResource.getJSONResponse(jsonResultExamples, new ArrayList<CIError>(), null), JsonNode.class);
				objectProperty.setExample(jsonNode);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {			
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
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

			String example = available.getArgDefaultStringValue(namespace, command, argumentName);
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
		parameter.setRequired(!model.properties.isEmpty());
		parameter.setSchema(model);
		operation.addParameter(parameter);
	}
	
	private final class CommandModel implements Model {
		
		final String namespace; 
		final String command;
		AvailableCommands available;
		
		private final Map<String, Property> properties;
		
		public CommandModel(String namespace, String command, AvailableCommands available) {
			this.namespace = namespace;
			this.command = command;
			this.available = available;
			this.properties = new HashMap<String, Property>();
			for (String argument : available.getArguments(namespace, command)) {
				Property property = new StringProperty();
				property.setName(argument);
				property.setDescription(available.getArgLongDescription(namespace, command, argument));
				String defaultString = available.getArgDefaultStringValue(namespace, command, argument);
				if (defaultString != null && defaultString.length() > 0) {
					property.setDefault(defaultString);
				}
				//property.setExample();
				properties.put(argument, property);
			}
		}

		@Override
		public String getDescription() {
			
			return "A set of command arguments for ";
		}

		@Override
		public Object getExample() {
			return null;
		}

		@Override
		public io.swagger.models.ExternalDocs getExternalDocs() {
			return null;
		}

		@Override
		public Map<String, Property> getProperties() {
			
			return properties;
		}

		@Override
		public String getReference() {
			return null;
		}

		@Override
		public String getTitle() {
			
			return "Command Arguments";
		}

		@Override
		public Map<String, Object> getVendorExtensions() {
			
			return null;
		}

		@Override
		public void setDescription(String arg0) {
			
		}

		@Override
		public void setExample(Object arg0) {
			
		}

		@Override
		public void setProperties(Map<String, Property> arg0) {
			
		}

		@Override
		public void setReference(String arg0) {
			
		}

		@Override
		public void setTitle(String arg0) {
			
		}
		
		public  Object clone(){
			return new CommandModel(namespace, command, available) ;	 
		 }
	}
	
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public String get() throws JsonProcessingException 
	{
		if (swaggerDefinition == null)
		{
			buildSwagger();
		}
		return swaggerDefinition;
	}

	@SwaggerDefinition(
			info = @Info(
					description = "An API to offer access to Cytoscape command line commands through a REST-like service.",
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
