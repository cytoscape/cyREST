package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.resource.CyRESTSwagger;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

public class CyRESTSwaggerTest extends JerseyTest 
{
	@Override
	protected Application configure() {
		return new ResourceConfig(CyRESTSwagger.class);
	}

	@Test
	public void test() {
		Response result = target("/v1/swagger.json").request().get();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		
		System.out.println("CyREST Swagger exists at /v1/swagger.json");
		
		//TODO Verify that the Swagger json or yaml documents the same resources as the server.
	}
}
