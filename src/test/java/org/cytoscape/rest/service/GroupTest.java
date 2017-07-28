package org.cytoscape.rest.service;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.subnetwork.CyRootNetwork;
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
		assertEquals(1, root.size());
	}
	
	//TODO Re-implement this test.
	public void testCreateGroup() throws Exception {
		
		final Long suid = network.getSUID();
		final CyNode node = network.getNodeList().get(0);
		String newVal = "{ \"name\":\"dummyname\", \"nodes\": ["+node.getSUID()+"]}";
		//when(network.containsNode(cyGroupNode)).thenReturn(true);
		when(network.getRow(cyGroupNode, CyRootNetwork.SHARED_ATTRS)).thenReturn(mock(CyRow.class));
		
		final Entity<String> entity = Entity.entity(newVal, MediaType.APPLICATION_JSON_TYPE);
		
		final Response result = target("/v1/networks/" + suid + "/groups").request().post(entity);
	
		assertNotNull(result);
		final String body = result.readEntity(String.class);
		System.out.println(body);
		assertEquals(500, result.getStatus());
		// TODO: Create mock
	}
	
	@Test
	public void testGetGroup() throws Exception {
		final Long suid = network.getSUID();
		
		// This is invalid Group ID
		Response result = target("/v1/networks/" + suid + "/groups/11111111").request().get();
		assertNotNull(result);
		assertEquals(404, result.getStatus());
		// TODO: Create mock
		result = target("/v1/networks/" + suid + "/groups/" + cyGroupNode.getSUID()).request().get();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		final String body = result.readEntity(String.class);
		System.out.println(body);
		final JsonNode root = mapper.readTree(body);
	
		assertEquals(cyGroupNode.getSUID().longValue(), root.get("SUID").asLong());
		assertEquals(1, root.get("nodes").size());
		
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
		assertEquals(1, root.get("count").asInt());
	}
	
	@Test
	public void testCollapseGroup() throws Exception {
		final Long suid = network.getSUID();
		
		assertNull(cyGroupCollapseCalled);
		
		// This is invalid Group ID
		Response result = target("/v1/networks/" + suid + "/groups/11111111/collapse").request().get();
		assertNotNull(result);
		assertEquals(404, result.getStatus());
		// TODO: Create mock
		result = target("/v1/networks/" + suid + "/groups/" + cyGroupNode.getSUID() + "/collapse").request().get();
		assertNotNull(result);
		assertEquals(204, result.getStatus());
		
		assertTrue(cyGroupCollapseCalled);
	}

	@Test
	public void testExpandGroup() throws Exception {
		final Long suid = network.getSUID();
		
		assertNull(cyGroupCollapseCalled);
		
		// This is invalid Group ID
		Response result = target("/v1/networks/" + suid + "/groups/11111111/collapse").request().get();
		assertNotNull(result);
		assertEquals(404, result.getStatus());
		// TODO: Create mock
		result = target("/v1/networks/" + suid + "/groups/" + cyGroupNode.getSUID() + "/expand").request().get();
		assertNotNull(result);
		assertEquals(204, result.getStatus());
		
		assertFalse(cyGroupCollapseCalled);
	}
	
}