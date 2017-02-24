package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.resource.CyRESTCommandSwagger;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CyRESTCommandSwaggerTest extends JerseyTest 
{
	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	protected Application configure() {
		return new ResourceConfig(CyRESTCommandSwagger.class);
	}

	@Test
	public void test() throws Exception {
		String result = target("/v1/commands/swagger.json").request().get(String.class);
		assertNotNull(result);
		
		
		System.out.println("CyREST Command Swagger exists at /v1/commands/swagger.json");
		
		final JsonNode root = mapper.readTree(result);
		//TODO Verify that the Swagger json or yaml documents the same resources as the server.
	}
	
	@Test
	public void isDefinitionNullOnInit()
	{
		CyRESTCommandSwagger cyRESTCommandSwagger = new CyRESTCommandSwagger();
		assertTrue(cyRESTCommandSwagger.isSwaggerDefinitionNull());
	}
	
	
}
