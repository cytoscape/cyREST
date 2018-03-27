package org.cytoscape.rest.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.never;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.rest.internal.resource.CollectionResource;
import org.cytoscape.rest.internal.resource.PropertiesResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

public class PropertiesResourceTest extends BasicResourceTest {

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	protected Application configure() {
		return new ResourceConfig(PropertiesResource.class);
	}

	@Test
	public void testGetPropertyNamespaces() throws Exception {
		Response result = target("/v1/properties").request().get();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		final String body = result.readEntity(String.class);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.has("data"));
		final JsonNode data = root.get("data");
		assertTrue(data.isArray());
		assertEquals(1, data.size());
		assertEquals("dummy.properties", data.get(0).asText());	
	}
	
	@Test
	public void testGetPropertyNames() throws Exception {
		Response result = target("/v1/properties/dummy.properties").request().get();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		final String body = result.readEntity(String.class);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.has("data"));
		final JsonNode data = root.get("data");
		assertTrue(data.isArray());
		assertEquals(1, data.size());
		assertEquals("dummy.property", data.get(0).asText());	
	}
	
	@Test
	public void testGetPropertyNames404() throws Exception {
		Response result = target("/v1/properties/dummy.nonexistant.properties").request().get();
		assertNotNull(result);
		assertEquals(404, result.getStatus());
		final String body = result.readEntity(String.class);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.has("data"));
		final JsonNode data = root.get("data");
		
	}
	
	@Test
	public void testGetProperty() throws Exception {
		Response result = target("/v1/properties/dummy.properties/dummy.property").request().get();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		final String body = result.readEntity(String.class);
		System.out.println(body);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.has("data"));
		final JsonNode data = root.get("data");
		assertTrue(data.has("value"));
		assertEquals("dummyPropertyValue", data.get("value").asText());	
	}
	
	@Test
	public void testPutProperty() throws Exception {
		final String newVal = "{"
				+ "\"value\": \"new.value\" }";
		
		Entity<String> entity = Entity.entity(newVal, MediaType.APPLICATION_JSON_TYPE);
		Response result = target("/v1/properties/dummy.properties/dummy.property").request().put(entity);
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		
		verify(properties).setProperty("dummy.property", "new.value");
	}
	
	@Test
	public void testPutProperty404A() throws Exception {
		final String newVal = "{"
				+ "\"value\": \"new.value\" }";
		
		Entity<String> entity = Entity.entity(newVal, MediaType.APPLICATION_JSON_TYPE);
		Response result = target("/v1/properties/dummy.nonexistant.properties/dummy.property").request().put(entity);
		assertNotNull(result);
		assertEquals(404, result.getStatus());
		
		verifyZeroInteractions(properties);
	}
	
	@Test
	public void testPutProperty404B() throws Exception {
		final String newVal = "{"
				+ "\"value\": \"new.value\" }";
		
		Entity<String> entity = Entity.entity(newVal, MediaType.APPLICATION_JSON_TYPE);
		Response result = target("/v1/properties/dummy.properties/dummy.nonexistant.property").request().put(entity);
		assertNotNull(result);
		assertEquals(404, result.getStatus());
		verify(properties).containsKey("dummy.nonexistant.property");
		verify(properties,never()).setProperty("new.property", "new.value");;
	}
	
	@Test
	public void testPostProperty404() throws Exception {
		final String newVal = "{"
				+  "\"key\": \"new.property\","
				+ "\"value\": \"new.value\" }";
		
		Entity<String> entity = Entity.entity(newVal, MediaType.APPLICATION_JSON_TYPE);
		Response result = target("/v1/properties/dummy.nonexistant.properties/").request().post(entity);
		assertNotNull(result);
		assertEquals(404, result.getStatus());
		
		verifyZeroInteractions(properties);
	}
	
	@Test
	public void testPostProperty() throws Exception {
		final String newVal = "{"
				+  "\"key\": \"new.property\","
				+ "\"value\": \"new.value\" }";
		
		Entity<String> entity = Entity.entity(newVal, MediaType.APPLICATION_JSON_TYPE);
		Response result = target("/v1/properties/dummy.properties/").request().post(entity);
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		
		verify(properties).setProperty("new.property", "new.value");
	}
	
	
	@Test
	public void testDeleteProperty() throws Exception {
		Response result = target("/v1/properties/dummy.properties/dummy.property").request().delete();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		
		verify(properties).remove("dummy.property");
	}
	
	@Test
	public void testDeleteProperty404A() throws Exception {
		Response result = target("/v1/properties/dummy.nonexistant.properties/dummy.property").request().delete();
		assertNotNull(result);
		assertEquals(404, result.getStatus());
		
		verify(properties, never()).remove("dummy.property");
	}
	
}
