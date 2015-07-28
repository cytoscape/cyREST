package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.resource.GroupResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GroupTest extends BasicResourceTest {

	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	protected Application configure() {
		return new ResourceConfig(GroupResource.class);
	}
	
	private void createGroup() {
		
	}

	@Test
	public void testGetGroups() throws Exception {
		final Long suid = network.getSUID();
		final Response result = target("/v1/networks/" + suid + "/groups").request().get();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		System.out.println("res: " + result.toString());
		final String body = result.readEntity(String.class);
		System.out.println(body);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.isArray());
		assertEquals(0, root.size());
	}
	
	
	@Test
	public void testGetGroup() throws Exception {
		final Long suid = network.getSUID();
		
		// This is invalid Group ID
		final Response result = target("/v1/networks/" + suid + "/groups/11111111").request().get();
		assertNotNull(result);
		assertEquals(404, result.getStatus());
		// TODO: Create mock
	}


	@Test
	public void testGetGroupCount() throws Exception {
		final Long suid = network.getSUID();
		final Response result = target("/v1/networks/" + suid + "/groups/count").request().get();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		System.out.println("res: " + result.toString());
		final String body = result.readEntity(String.class);
		System.out.println(body);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.isObject());
		assertEquals(0, root.get("count").asInt());
	}
}