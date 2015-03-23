package org.cytoscape.rest.service;

import static org.junit.Assert.*;

import java.io.File;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.resource.SessionResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SessionResourceTest extends BasicResourceTest {

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	protected Application configure() {
		return new ResourceConfig(SessionResource.class);
	}

	@Test
	public void testGetSessionName() throws Exception {
		String result = target("/v1/session/name").request().get(String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		assertTrue(root.isObject());
		assertNotNull(root.get("name"));
		assertEquals("testSession", root.get("name").asText());
	}


	@Test
	public void testLoadSession() throws Exception {
		File dummySession = new File("test1.cys");
		String result = target("/v1/session").queryParam("file", dummySession.getName()).request().get(String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		assertTrue(root.isObject());
		assertNotNull(root.get("file"));
		assertEquals(dummySession.getAbsolutePath(), root.get("file").asText());
	}
	
	@Test
	public void testDeleteSession() throws Exception {
		Response result = target("/v1/session").request().delete();
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		System.out.println("res: " + result.toString());
		MediaType type = result.getMediaType();
		System.out.println("media type: " + type.toString());
		assertEquals(MediaType.APPLICATION_JSON, type.toString());
		String val = result.readEntity(String.class);
		System.out.println("value: " + val);
		final JsonNode root = mapper.readTree(val);
		assertTrue(root.isObject());
		assertNotNull(root.get("message"));
		assertEquals("New session created.", root.get("message").asText());
	}
	
	
	@Test
	public void testSaveSession() throws Exception {
		final Entity<String> entity = Entity.entity("", MediaType.APPLICATION_JSON_TYPE);
		File dummySession = new File("test1.cys");
		System.out.println("original file: " + dummySession.getAbsolutePath());
		
		Response result = target("/v1/session").queryParam("file", dummySession.getName()).request().post(entity);
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		System.out.println("res: " + result.toString());
		MediaType type = result.getMediaType();
		System.out.println("media type: " + type.toString());
		assertEquals(MediaType.APPLICATION_JSON, type.toString());
		String val = result.readEntity(String.class);
		System.out.println("value: " + val);
		final JsonNode root = mapper.readTree(val);
		assertTrue(root.isObject());
		assertNotNull(root.get("file"));
		assertEquals(dummySession.getAbsolutePath(), root.get("file").asText());
	}
}
