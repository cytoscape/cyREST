package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.rest.internal.resource.NetworkResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NetworkNameResourceTest extends BasicResourceTest {

	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	protected Application configure() {
		return new ResourceConfig(NetworkResource.class);
	}

	@Test
	public void testGetNetworkNames() throws Exception {
		String result = target("/v1/networks.names").request().get(String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		assertTrue(root.isArray());
		assertEquals(2, root.size());
		
		
		final JsonNode firstEntry = root.get(0);
		assertTrue(firstEntry.isObject());
		
		assertNotNull(firstEntry.get("name").asText());
		assertNotNull(firstEntry.get("SUID").asLong());
	}
	
	@Test
	public void testGetNetworkNamesColumnNullProducesError() throws Exception {
		Response response = target("/v1/networks.names").queryParam("query", "dummy").request().get();
		assertNotNull(response);
		assertEquals(500, response.getStatus());
	
		String result = response.readEntity(String.class);
		assertNotNull(result);
		System.out.println(result);
		final JsonNode root = mapper.readTree(result);
		assertEquals(2, root.size());
		assertTrue(root.has("data"));
		assertTrue(root.has("errors"));
		assertEquals(1,root.get("errors").size());
		final JsonNode errorNode = root.get("errors").get(0);
		assertEquals("urn:cytoscape:ci:cyrest-core:v1:networks:errors:7", errorNode.get("type").asText());
	}
	
	@Test
	public void testGetNetworkNamesQueryNullProducesError() throws Exception {
		Response response = target("/v1/networks.names").queryParam("column", "dummy").request().get();
		assertNotNull(response);
		assertEquals(500, response.getStatus());
	
		String result = response.readEntity(String.class);
		assertNotNull(result);
		System.out.println(result);
		final JsonNode root = mapper.readTree(result);
		assertEquals(2, root.size());
		assertTrue(root.has("data"));
		assertTrue(root.has("errors"));
		assertEquals(1,root.get("errors").size());
		final JsonNode errorNode = root.get("errors").get(0);
		assertEquals("urn:cytoscape:ci:cyrest-core:v1:networks:errors:7", errorNode.get("type").asText());
	}
}
