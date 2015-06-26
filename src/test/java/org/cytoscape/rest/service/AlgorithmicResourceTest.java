package org.cytoscape.rest.service;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.resource.AlgorithmicResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AlgorithmicResourceTest extends BasicResourceTest {

	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	protected Application configure() {
		return new ResourceConfig(AlgorithmicResource.class);
	}

	@Test
	public void testGetLayouts() throws Exception {
		final Response result = target("/v1/apply/layouts").request().get();
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		System.out.println("res: " + result.toString());
		final String body = result.readEntity(String.class);
		System.out.println(body);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.isArray());
		assertEquals(root.size(), 1);
		assertEquals("grid", root.get(0).asText());
	}


	@Test
	public void testGetLayout() throws Exception {
		final Response result = target("/v1/apply/layouts/grid").request().get();
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		
		System.out.println("res: " + result.toString());
		
		final String body = result.readEntity(String.class);
		System.out.println(body);
		final JsonNode root = mapper.readTree(body);
		assertEquals("grid", root.get("name").asText());
		assertTrue(root.get("parameters").isArray());
	}


	@Test
	public void testGetLayoutParameters() throws Exception {
		final Response result = target("/v1/apply/layouts/grid/parameters").request().get();
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		
		System.out.println("res: " + result.toString());
		
		final String body = result.readEntity(String.class);
		System.out.println(body);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.isArray());
	}
	

	@Test
	public void testGetLayoutColumnType() throws Exception {
		final Response result = target("/v1/apply/layouts/grid/columntypes").request().get();
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		
		System.out.println("res: " + result.toString());
		
		final String body = result.readEntity(String.class);
		System.out.println(body);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.isObject());
	}


	@Test
	public void testGetStyles() throws Exception {
		final Response result = target("/v1/apply/styles").request().get();
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		System.out.println("res: " + result.toString());
		final String body = result.readEntity(String.class);
		System.out.println(body);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.isArray());
		assertEquals(2, root.size());
		Set<String> vsNames = new HashSet<>();
		for(JsonNode n: root) {
			vsNames.add(n.asText());
		}
		
		assertTrue(vsNames.contains("vs1"));
		assertTrue(vsNames.contains("mock1"));
	}
	
	@Test
	public void testApplyLayout() {
		Long suid = view.getModel().getSUID();
		Response result = target("/v1/apply/layouts/grid/" + suid)
				.queryParam("column", "test").request().get();
		System.out.println("res: " + result.toString());
		
		// TODO: Add more realistic test
		assertTrue(result.getStatus() == 500);
	}


	@Test
	public void testApplyStyle() throws Exception {
		Long suid = view.getModel().getSUID();
		Response result = target("/v1/apply/styles/vs1/" + suid).request().get();
		System.out.println("res: " + result.toString());
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		final String body = result.readEntity(String.class);
		System.out.println(body);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.isObject());
		assertNotNull(root.get("message").asText());
	}
}