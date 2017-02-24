package org.cytoscape.rest.internal.resource;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cytoscape.command.AvailableCommands;
import org.cytoscape.rest.internal.commands.resources.CommandResource;
import org.cytoscape.rest.internal.task.ResourceManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Inject;

import io.swagger.annotations.ExternalDocs;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.config.ReaderListener;
import io.swagger.models.Operation;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.QueryParameter;
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

		// serialization of the Swagger definition
		try 
		{
			Json.mapper().enable(SerializationFeature.INDENT_OUTPUT);
			this.swaggerDefinition = Json.mapper().writeValueAsString(swagger);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
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
					description = "A bridge to offer access to Cytoscape command line commands through a REST-like service.",
					version = "V2.0.0",
					title = "CyREST Command Bridge API"
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
			basePath = "/",
			consumes = {"application/json", "application/xml"},
			produces = {"application/json", "application/xml"},
			schemes = {SwaggerDefinition.Scheme.HTTP},
			tags = 
		{
				
		}, 
		externalDocs = @ExternalDocs(value = "Cytoscape", url = "http://cytoscape.org/")
		)
	public class CyRESTCommandSwaggerConfig implements ReaderListener
	{

		@Override
		public void beforeScan(Reader arg0, Swagger swagger) 
		{
			//Here we hide the single path used for all commands and replace it with ALL available commands.
			swagger.getPaths().remove("/v1/commands/{namespace}/{command}");
		}

		public void afterScan(Reader reader, Swagger swagger)
		{
			Set<String> classes = new HashSet<String>();
			
			for (String namespace : available.getNamespaces())
			{
				for (String command : available.getCommands(namespace))
				{
					io.swagger.models.Path testPath = new io.swagger.models.Path();
					Operation operation = new Operation();
					operation.addTag(namespace);
					operation.setOperationId(namespace + "/" + command);
					operation.setSummary(available.getDescription(namespace, command));
				
					for (String argumentName : available.getArguments(namespace, command))
					{
						
						QueryParameter parameter = new QueryParameter();
						parameter.setName(argumentName);
						
						String type = available.getArgTypeString(namespace, command, argumentName);
						classes.add(available.getArgType(namespace, command, argumentName).toString());
						
						String description = available.getArgDescription(namespace, command, argumentName);
						//FIXME Since some argument type strings contain HTML special characters, we're performing
						//a replacement here. This could probably be done a lot more comprehensively.
						parameter.setDescription(type.replaceAll("<", "&lt;").replaceAll(">","&gt;")+ ": " + description);
						
						Class<?> typeClass = available.getArgType(namespace, command, argumentName);
						setTypeAndFormatFromClass(parameter,typeClass);
						
						boolean required = available.getArgRequired(namespace, command, argumentName);
						parameter.setRequired(required);
					
						operation.addParameter(parameter);
					}
					
					//Later, this could be very useful for extracting JSON.
					//operation.addProduces(MediaType.APPLICATION_JSON);

					operation.addProduces(MediaType.TEXT_PLAIN);
					Response response = new Response();
					response.setDescription("successful operation");
					StringProperty stringProperty =  new StringProperty();
					stringProperty.setName("testName");
					response.setSchema(stringProperty);

					operation.addResponse("200", response);

					testPath.setGet(operation);
					swagger.path("/v1/commands/" + namespace + "/" + command, testPath);
				}
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
		public void setTypeAndFormatFromClass(QueryParameter parameter, Class<?> clazz)
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
}
