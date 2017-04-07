package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.resource.UIResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UiResourceTest extends BasicResourceTest {

	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	protected Application configure() {
		return new ResourceConfig(UIResource.class);
	}

	@Test
	public void testGetUI() throws Exception {
		final Response result = target("/v1/ui").request().get();
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		System.out.println("res: " + result.toString());
		final String body = result.readEntity(String.class);
		System.out.println(body);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.isObject());
		assertTrue(root.get("isDesktopAvailable").asBoolean());
	}

	
	@Test
	public void testGetPanels() throws Exception {
		final Response result = target("/v1/ui/panels").request().get();
		assertNotNull(result);
		assertTrue(result.getStatus() == 500);
		// TODO: prepare mock
	}
	
	
	@Test
	public void testPanelStatus() throws Exception {
		Response result = target("/v1/ui/panels/WEST").request().get();
		assertNotNull(result);
		System.out.println(result);
		assertTrue(result.getStatus() == 500);
		
		result = target("/v1/ui/panels/FOO").request().get();
		assertNotNull(result);
		System.out.println(result);
		assertTrue(result.getStatus() == 500);
		// TODO: prepare mock
	}


	@Test
	public void testUpdateLod() throws Exception {
		Entity<String> entity = Entity.entity("", MediaType.APPLICATION_JSON_TYPE);
		final Response result = target("/v1/ui/lod").request().put(entity);
		assertNotNull(result);
		final String body = result.readEntity(String.class);
		System.out.println(body);
		assertTrue(result.getStatus() == 200);
		JsonNode root = mapper.readTree(body);
		assertEquals("Toggled Graphics level of details.", root.get("message").asText());
		// TODO: prepare mock
	}
}
