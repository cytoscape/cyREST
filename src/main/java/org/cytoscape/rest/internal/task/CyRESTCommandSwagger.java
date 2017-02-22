package org.cytoscape.rest.internal.task;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cytoscape.rest.internal.commands.resources.CommandResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Swagger;
import io.swagger.util.Json;

@Path("/v1/commands/swagger.json")
@Singleton
public class CyRESTCommandSwagger 
{
	private final String swaggerDefinition;
	
	public CyRESTCommandSwagger(){
		BeanConfig commandBeanConfig = new BeanConfig(){
			public Set<Class<?>> classes()
			{
				Set<Class<?>> classes = new HashSet<Class<?>>();
				classes.add(CommandResource.class);
				classes.add(CyRESTCommandSwaggerConfig.class);
				return classes;
			}
		};
	
		//FIXME This needs to get set from the ResourceManager
		commandBeanConfig.setHost("0.0.0.0" + ":" + 1234);
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
		return swaggerDefinition;
	}
}
