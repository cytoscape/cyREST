package org.cytoscape.rest.service;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.resource.AlgorithmicResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
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
	public void testFailsYFiles() throws Exception {
		Response result = target("/v1/apply/layouts/com.yworks.yfiles.layout.DummyYFilesLayout").request().get();
		assertEquals(404, result.getStatus());
		result = target("/v1/apply/layouts/yfiles.DummyYFilesLayout").request().get();
		assertEquals(404, result.getStatus());
	}
	
	@Test
	public void testFailsYFilesNetworkId() throws Exception {
		Long suid = view.getModel().getSUID();
		Response result = target("/v1/apply/layouts/com.yworks.yfiles.layout.DummyYFilesLayout/" + suid).request().get();
		assertEquals(404, result.getStatus());
		result = target("yfiles.DummyYFilesLayout/" + suid).request().get();
		assertEquals(404, result.getStatus());
	}
	
	@Test
	public void testFailsYFilesParametersGet() throws Exception {
		Response result = target("/v1/apply/layouts/com.yworks.yfiles.layout.DummyYFilesLayout/parameters").request().get();
		assertEquals(404, result.getStatus());
		result = target("/v1/apply/layouts/yfiles.DummyYFilesLayout/parameters").request().get();
		assertEquals(404, result.getStatus());
	}
	
	@Test
	public void testFailsYFilesParametersPut() throws Exception {
		Response result = target("/v1/apply/layouts/com.yworks.yfiles.layout.DummyYFilesLayout/parameters").request().put(Entity.entity("", MediaType.APPLICATION_JSON_TYPE));
		System.out.println(result.getEntity());
		assertEquals(404, result.getStatus());
		result = target("/v1/apply/layouts/yfiles.DummyYFilesLayout/parameters").request().put(Entity.entity("", MediaType.APPLICATION_JSON_TYPE));
		assertEquals(404, result.getStatus());
	}
	
	
	@Test
	public void testLayoutParametersPutInvalidJson() throws Exception {
		Response result = target("/v1/apply/layouts/grid/parameters").request().put(Entity.entity("", MediaType.APPLICATION_JSON_TYPE));
		String body = result.readEntity(String.class);
		JsonNode root = mapper.readTree(body);
		assertNotNull(root);
		root.get("data");
		JsonNode errorsNode = root.get("errors");
		assertEquals(1, errorsNode.size());
		JsonNode error = errorsNode.get(0);
		assertEquals("urn:cytoscape:ci:cyrest-core:v1:apply:errors:6", error.get("type").asText());
		assertEquals(500, result.getStatus());
	}
	
	@Test
	public void testLayoutParametersPut() throws Exception {
		Response result = target("/v1/apply/layouts/grid/parameters").request().put(Entity.entity("[{\"name\": \"size\", \"value\": \"1\"}]", MediaType.APPLICATION_JSON_TYPE));
		System.out.println(result.getEntity());
		assertEquals(200, result.getStatus());
	}
	
	@Test
	public void testLayoutParametersPutInvalidType() throws Exception {
		Response result = target("/v1/apply/layouts/grid/parameters").request().put(Entity.entity("[{\"name\": \"invalid_name\", \"value\": \"1\"}]", MediaType.APPLICATION_JSON_TYPE));
		String body = result.readEntity(String.class);
		JsonNode root = mapper.readTree(body);
		assertNotNull(root);
		root.get("data");
		JsonNode errorsNode = root.get("errors");
		assertEquals(1, errorsNode.size());
		JsonNode error = errorsNode.get(0);
		assertEquals("urn:cytoscape:ci:cyrest-core:v1:apply:errors:6", error.get("type").asText());
		assertEquals(500, result.getStatus());
	}
	
	@Test
	public void testFailsYFilesColumnTypes() throws Exception {
		Response result = target("/v1/apply/layouts/com.yworks.yfiles.layout.DummyYFilesLayout/columntypes").request().get();
		assertEquals(404, result.getStatus());
		System.out.println("Result = " + result);
		String body = result.readEntity(String.class);
		JsonNode root = mapper.readTree(body);
		assertNotNull(root);
		root.get("data");
		JsonNode errorsNode = root.get("errors");
		assertEquals(1, errorsNode.size());
		JsonNode error = errorsNode.get(0);
		assertEquals("urn:cytoscape:ci:cyrest-core:v1:apply:errors:7", error.get("type").asText());
		
		
		result = target("/v1/apply/layouts/yfiles.DummyYFilesLayout/columntypes").request().get();
		System.out.println(result);
		assertEquals(404, result.getStatus());
		
		body = result.readEntity(String.class);
		root = mapper.readTree(body);
		assertNotNull(root);
		root.get("data");
		errorsNode = root.get("errors");
		assertEquals(1, errorsNode.size());
		error = errorsNode.get(0);
		assertEquals("urn:cytoscape:ci:cyrest-core:v1:apply:errors:7", error.get("type").asText());
		
	}
	
	@Test
	public void testGetFit() throws Exception {
		
		Long suid = view.getModel().getSUID();
		final Response result = target("/v1/apply/fit/" + suid).request().get();
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		
		System.out.println("res: " + result.toString());
		
		final String body = result.readEntity(String.class);
		System.out.println(body);
		final JsonNode root = mapper.readTree(body);
		assertEquals("Fit content success.", root.get("message").asText());
	}

	@Test
	public void testEdgeBundling() throws Exception {
		
		Long suid = view.getModel().getSUID();
		final Response result = target("/v1/apply/edgebundling/" + suid).request().get();
		assertNotNull(result);
		System.out.println("res: " + result.toString());
		final String body = result.readEntity(String.class);
		System.out.println(body);
		
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		
		
		final JsonNode root = mapper.readTree(body);
		assertEquals("Edge bundling success.", root.get("message").asText());
	}
	
	@Test
	public void testClearEdgeBends() throws Exception {
		
		Long suid = view.getModel().getSUID();
		final Response result = target("/v1/apply/clearalledgebends/" + suid).request().get();
		assertNotNull(result);
		System.out.println("res: " + result.toString());
		final String body = result.readEntity(String.class);
		System.out.println(body);
		
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		
		
		final JsonNode root = mapper.readTree(body);
		assertEquals("Clear all edge bends success.", root.get("message").asText());
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
	public void testApplyLayout() throws JsonProcessingException, IOException {
		Long suid = view.getModel().getSUID();
		Response result = target("/v1/apply/layouts/grid/" + suid)
				.queryParam("column", "test").request().get();
		System.out.println("res: " + result.toString());
		final String body = result.readEntity(String.class);
		System.out.println(body);
		// TODO: Add more realistic test
		assertEquals(200, result.getStatus());
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.isObject());
		assertEquals("Layout finished.", root.get("message").asText());
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
		assertEquals("Visual Style applied.",root.get("message").asText());
	}
}