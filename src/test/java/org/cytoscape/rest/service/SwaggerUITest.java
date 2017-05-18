package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.resource.SwaggerUIResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

public class SwaggerUITest extends BasicResourceTest
{
	@Override
	protected Application configure() {
		return new ResourceConfig(SwaggerUIResource.class);
	}

	@Test
	public void getsContent() throws Exception {
		Response response = target("/v1/swaggerUI/dummyResourcePath").request().get();
	
		assertEquals(200, response.getStatus());
		
		InputStream result = (InputStream) response.getEntity();
		assertNotNull(result);
		assertEquals(9, result.available());
		
		byte[] resultBytes= new byte[9];
		result.read(resultBytes);
		assertEquals("test data", new String(resultBytes));
	}
	
}
