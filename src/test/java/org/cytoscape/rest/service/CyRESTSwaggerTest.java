package org.cytoscape.rest.service;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.resource.CyRESTSwagger;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CyRESTSwaggerTest extends SwaggerResourceTest 
{
	private ObjectMapper mapper = new ObjectMapper();
	@Override
	protected Application configure() {
		return new ResourceConfig(CyRESTSwagger.class);
	}

	@Test
	public void hasValidSwaggerConfig() throws JsonProcessingException, IOException {
		Response response = target("/v1/swagger.json").request().get();
		String result = response.readEntity(String.class);
		assertNotNull(result);
		
		System.out.println("CyREST Swagger exists at /v1/swagger.json");
		System.out.println(result);
		final JsonNode root = mapper.readTree(result);
		
		swaggerConfigTest(root);
	}
}
