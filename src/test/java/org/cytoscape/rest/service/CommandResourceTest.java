package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.commands.resources.CommandResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CommandResourceTest extends BasicResourceTest {

	@Override
	protected Application configure() {
		return new ResourceConfig(CommandResource.class);
	}

	private ObjectMapper mapper = new ObjectMapper();


	@Test
	public void testCommand() throws Exception {
		
		Response response = target("/v1/commands/dummyNamespace/dummyCommand").queryParam("dummyArgument", "fleen").request().get();
		assertNotNull(response);
		
		final String body = response.readEntity(String.class);
		System.out.println(body);
		
		assertEquals(200, response.getStatus());
	}
}
