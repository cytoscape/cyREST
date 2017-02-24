package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.resource.CyRESTCommandSwagger;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CyRESTCommandSwaggerTest extends SwaggerResourceTest
{
	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	protected Application configure() {
		return new ResourceConfig(CyRESTCommandSwagger.class);
	}

	@Test
	public void hasValidSwaggerConfig() throws Exception {
		Response response = target("/v1/commands/swagger.json").request().get();
		String result = response.readEntity(String.class);
		assertNotNull(result);
		
		System.out.println("CyREST Command Swagger exists at /v1/commands/swagger.json");
		System.out.println(result);
		
		final JsonNode root = mapper.readTree(result);
	
		swaggerConfigTest(root);
	}
	
	@Test
	public void isDefinitionNullOnInit()
	{
		CyRESTCommandSwagger cyRESTCommandSwagger = new CyRESTCommandSwagger();
		assertTrue(cyRESTCommandSwagger.isSwaggerDefinitionNull());
	}
	
	
}
