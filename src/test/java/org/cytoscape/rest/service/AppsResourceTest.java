package org.cytoscape.rest.service;

import static org.junit.Assert.*;

import java.io.IOException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.resource.AppsResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AppsResourceTest extends BasicResourceTest {

	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	protected Application configure() {
		return new ResourceConfig(AppsResource.class);
	}

	@Test
	public void appsTest() throws JsonProcessingException, IOException {
		final Response response = target("/v1/apps/").request().get();
		assertEquals(200, response.getStatus());
		final String body = response.readEntity(String.class);
		System.out.println(body);
	
		final JsonNode root = mapper.readTree(body);
		
		assertTrue(root.isArray());
		assertEquals(1, root.size());
		JsonNode firstNode = root.get(0);
		assertEquals("dummy automation bundle", firstNode.get("bundleName").asText());
		assertEquals("org.dummyorg.dummyautomationapp", firstNode.get("bundleSymbolicName").asText());
		assertEquals("6.0.0", firstNode.get("bundleVersion").asText());
		assertEquals(1, firstNode.get("bundleState").asInt());
		
	}
	
}
