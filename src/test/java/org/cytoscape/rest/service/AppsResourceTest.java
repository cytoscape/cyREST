package org.cytoscape.rest.service;

import static org.junit.Assert.*;

import java.io.IOException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.resource.AppsResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.osgi.framework.Bundle;

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
	
		final JsonNode data = mapper.readTree(body);
		
		JsonNode activeNode = null;
		JsonNode uninstalledNode = null;
		
		JsonNode node0 = data.get(0);
		if (node0.get("bundleName").asText().equals("dummy active automation bundle")) {
			activeNode = node0;
		} else if (node0.get("bundleName").asText().equals("dummy uninstalled automation bundle"))
		{
			uninstalledNode = node0;
		}
		JsonNode node1 = data.get(1);
		
		if (node1.get("bundleName").asText().equals("dummy active automation bundle")) {
			activeNode = node1;
		} else if (node1.get("bundleName").asText().equals("dummy uninstalled automation bundle"))
		{
			uninstalledNode = node1;
		}
		
		assertEquals("dummy active automation bundle", activeNode.get("bundleName").asText());
		assertEquals("org.dummyorg.dummyactiveautomationapp", activeNode.get("bundleSymbolicName").asText());
		assertEquals("6.0.0", activeNode.get("bundleVersion").asText());
		assertEquals(Bundle.ACTIVE, activeNode.get("bundleState").asInt());
		
		assertEquals("dummy uninstalled automation bundle", uninstalledNode.get("bundleName").asText());
		assertEquals("org.dummyorg.dummyuninstalledautomationapp", uninstalledNode.get("bundleSymbolicName").asText());
		assertEquals("6.0.0", uninstalledNode.get("bundleVersion").asText());
		assertEquals(Bundle.UNINSTALLED, uninstalledNode.get("bundleState").asInt());
	}
	
	@Test
	public void appsFilterTest() throws JsonProcessingException, IOException {
		final Response response = target("/v1/apps/").queryParam("bundleState", Bundle.ACTIVE).request().get();
		assertEquals(200, response.getStatus());
		final String body = response.readEntity(String.class);
		System.out.println(body);
	
		final JsonNode data = mapper.readTree(body);
		
		assertTrue(data.isArray());
		assertEquals(1, data.size());
		JsonNode node0 = data.get(0);
		assertEquals("dummy active automation bundle", node0.get("bundleName").asText());
		assertEquals("org.dummyorg.dummyactiveautomationapp", node0.get("bundleSymbolicName").asText());
		assertEquals("6.0.0", node0.get("bundleVersion").asText());
		assertEquals(Bundle.ACTIVE, node0.get("bundleState").asInt());
	
	}
	
	@Test
	public void appsCountFilterTest() throws JsonProcessingException, IOException {
		final Response response = target("/v1/apps/count").queryParam("bundleState", Bundle.ACTIVE).request().get();
		assertEquals(200, response.getStatus());
		final String body = response.readEntity(String.class);
		System.out.println(body);
	
		final JsonNode root = mapper.readTree(body);
		final JsonNode data = root.get("data");
		final JsonNode count = data.get("count");
		
		assertEquals(1, count.asInt()); 
		
		final JsonNode errors = root.get("errors");
		assertTrue(errors.isArray());
		assertEquals(0, errors.size());
	}
}
