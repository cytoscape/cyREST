package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

public class SwaggerTest extends JerseyTest 
{
	@Override
	protected Application configure() 
	{
		ResourceConfig rc = new ResourceConfig();
		rc.register(io.swagger.jaxrs.listing.ApiListingResource.class);
		rc.register(io.swagger.jaxrs.listing.SwaggerSerializers.class);
		return rc;
	}

	@Test
	public void test() {
		Response result = target("/swagger.json").request().get();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		
		System.out.println("Swagger exists at /swagger.json");
		
		//TODO Verify that the Swagger json or yaml documents the same resources as the server.
	}
}
