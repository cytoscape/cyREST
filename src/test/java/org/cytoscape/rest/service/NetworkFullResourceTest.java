package org.cytoscape.rest.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.rest.internal.resource.NetworkFullResource;
import org.cytoscape.work.TaskIterator;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NetworkFullResourceTest extends BasicResourceTest {
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	protected Application configure() {
		return new ResourceConfig(NetworkFullResource.class);
	}
	
	@Test
	public void testGetNetworksJSON() throws Exception {
		Response response = target("/v1/networks.json").request().get();
		System.out.println(response.readEntity(String.class));
	}
	
	@Test
	public void testGetNetworkNamesColumnNullProducesError() throws Exception {
		Response response = target("/v1/networks.json").queryParam("query", "dummy").request().get();
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
		assertEquals("urn:cytoscape:ci:cyrest-core:v1:networks:errors:2", errorNode.get("type").asText());
	}
	
	@Test
	public void testGetNetworkNamesQueryNullProducesError() throws Exception {
		Response response = target("/v1/networks.json").queryParam("column", "dummy").request().get();
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
		assertEquals("urn:cytoscape:ci:cyrest-core:v1:networks:errors:2", errorNode.get("type").asText());
	}

}
