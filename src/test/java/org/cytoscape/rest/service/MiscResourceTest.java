package org.cytoscape.rest.service;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.resource.MiscResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;

public class MiscResourceTest extends BasicResourceTest {

	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	protected Application configure() {
		return new ResourceConfig(MiscResource.class);
	}

	
	@Test
	public void gcTest() {
		final Response result = target("/v1/gc").request().get();
		assertEquals(204, result.getStatus());
	}
	
	@Test
	public void cytoscapeVersionTest() {
		final Response result = target("/v1/version").request().get();
		assertEquals(200, result.getStatus());
	}
	
	@Test 
	public void statusTest() throws JsonProcessingException, IOException {
		when(this.allAppsStartedListener.getAllAppsStarted()).thenReturn(false);
		final Response response = target("/v1/").request().get();
		assertEquals(200, response.getStatus());
		final JsonNode root = mapper.readTree(response.readEntity(String.class));
		assertTrue(root.has("allAppsStarted"));
		assertEquals(false, root.get("allAppsStarted").asBoolean());
	
	}
	
	@Test 
	public void statusAllAppsLoadedTest() throws JsonProcessingException, IOException {
		when(this.allAppsStartedListener.getAllAppsStarted()).thenReturn(true);
		final Response response = target("/v1/").request().get();
		assertEquals(200, response.getStatus());
		final JsonNode root = mapper.readTree(response.readEntity(String.class));
		assertTrue(root.has("allAppsStarted"));
		assertEquals(true, root.get("allAppsStarted").asBoolean());
	
	}
}
