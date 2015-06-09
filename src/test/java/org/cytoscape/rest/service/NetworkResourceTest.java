package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.Application;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.rest.internal.resource.NetworkResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NetworkResourceTest extends BasicResourceTest {
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	protected Application configure() {
		return new ResourceConfig(NetworkResource.class);
	}
	
	@Test
	public void testGetNetworkCount() throws Exception {
		String result = target("/v1/networks/count").request().get(
				String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		Long count = root.get("count").asLong();
		assertTrue(count == 1);
	}
	
	@Test
	public void testGetNetworks() throws Exception {
		String result = target("/v1/networks").request().get(
				String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		assertEquals(1, root.size());
		assertTrue(network.getSUID() == root.get(0).asLong());
	}

	@Test
	public void testGetNodeCount() throws Exception {
		final Long suid = network.getSUID();
		String result = target("/v1/networks/" + suid.toString() + "/nodes/count").request().get(
				String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		Long count = root.get("count").asLong();
		assertTrue(count == 4);
	}
	
	@Test
	public void testGetEdgeCount() throws Exception {
		final Long suid = network.getSUID();
		String result = target("/v1/networks/" + suid.toString() + "/edges/count").request().get(
				String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		Long count = root.get("count").asLong();
		assertTrue(count == 3);
	}

	@Test
	public void testGetNodes() throws Exception {
		final Long suid = network.getSUID();
		String result = target("/v1/networks/" + suid.toString() + "/nodes").request().get(
				String.class);
		assertNotNull(result);
		
		final JsonNode root = mapper.readTree(result);
		assertTrue(root.isArray());
		assertEquals(4, root.size());
	}
	
	
	@Test
	public void testGetSelectedNodes() throws Exception {
		final Long suid = network.getSUID();
		final List<CyNode> nodes = network.getNodeList();
		final CyNode node1 =  nodes.get(0);
		final CyNode node2 =  nodes.get(1);
		final CyNode node3 =  nodes.get(2);
		// Set selected.
		network.getRow(node1).set(CyNetwork.SELECTED, true);
		network.getRow(node2).set(CyNetwork.SELECTED, true);
		network.getRow(node3).set(CyNetwork.SELECTED, false);

		String result = target("/v1/networks/" + suid.toString() + "/nodes/selected")
				.request()
				.get(String.class);
		assertNotNull(result);
		
		final JsonNode root = mapper.readTree(result);
		assertTrue(root.isArray());
		assertEquals(2, root.size());
		System.out.println(root.asText());
		final Set<Long> selected = new HashSet<>();
		for(final JsonNode node: root) {
			final String idstr = node.asText();
			long id = Long.parseLong(idstr);
			selected.add(id);
		}
		
		assertTrue(selected.contains(node1.getSUID()));
		assertTrue(selected.contains(node2.getSUID()));
		assertFalse(selected.contains(node3.getSUID()));
	}

	@Test
	public void testGetNeighbors() throws Exception {
		final Long suid = network.getSUID();
		final List<CyNode> nodes = network.getNodeList();
		final List<CyNode> node1 = nodes.stream()
			.filter(node->network.getRow(node).get(CyNetwork.NAME, String.class).equals("n1"))
			.collect(Collectors.toList());
		assertEquals(1, node1.size());
		
		final CyNode node = node1.get(0);
		// Set selected.
		network.getRow(node).set(CyNetwork.SELECTED, true);

		String result = target("/v1/networks/" + suid.toString() + "/nodes/selected/neighbors")
				.request()
				.get(String.class);
		assertNotNull(result);
		
		final JsonNode root = mapper.readTree(result);
		assertTrue(root.isArray());
		assertEquals(2, root.size());
		System.out.println(root.asText());
		final Set<Long> selected = new HashSet<>();
		for(final JsonNode n: root) {
			final String idstr = n.asText();
			long id = Long.parseLong(idstr);
			selected.add(id);
		}
		
		assertEquals(2,selected.size());
	}


	@Test
	public void testGetSelectedEdges() throws Exception {
		final Long suid = network.getSUID();
		final List<CyEdge> edges = network.getEdgeList();
		final CyEdge edge1 =  edges.get(0);
		final CyEdge edge2 =  edges.get(1);
		
		// Set selected.
		network.getRow(edge1).set(CyNetwork.SELECTED, true);
		network.getRow(edge2).set(CyNetwork.SELECTED, true);

		String result = target("/v1/networks/" + suid.toString() + "/edges/selected")
				.request()
				.get(String.class);
		assertNotNull(result);
		
		final JsonNode root = mapper.readTree(result);
		assertTrue(root.isArray());
		assertEquals(2, root.size());
		System.out.println(root.asText());
		final Set<Long> selected = new HashSet<>();
		for(final JsonNode edge: root) {
			final String idstr = edge.asText();
			long id = Long.parseLong(idstr);
			selected.add(id);
		}
		
		assertTrue(selected.contains(edge1.getSUID()));
		assertTrue(selected.contains(edge2.getSUID()));
	}

	@Test
	public void testGetEdges() throws Exception {
		final Long suid = network.getSUID();
		String result = target("/v1/networks/" + suid.toString() + "/edges").request().get(
				String.class);
		assertNotNull(result);
		
		final JsonNode root = mapper.readTree(result);
		assertEquals(3, root.size());
	}
	
	@Test
	public void testGetNode() throws Exception {
		final Long suid = network.getSUID();
		final List<CyNode> nodes = network.getNodeList();
		final CyNode node1 =  nodes.get(0);
		String result = target("/v1/networks/" + suid.toString() + "/nodes/" + node1.getSUID()).request().get(
				String.class);
		assertNotNull(result);
		
		final JsonNode root = mapper.readTree(result);
		JsonNode data = root.get("data");
		assertNotNull(data);
		assertEquals(node1.getSUID(), (Long)data.get("id").asLong());
		assertEquals(node1.getSUID(), (Long)data.get("SUID").asLong());
		assertEquals(false, (Boolean)data.get("selected").asBoolean());
	}
	
	@Test
	public void testGetEdge() throws Exception {
		final Long suid = network.getSUID();
		final List<CyEdge> edges = network.getEdgeList();
		final CyEdge edge1 =  edges.get(0);
		final CyNode source = edge1.getSource();
		final CyNode target = edge1.getTarget();
		
		String result = target("/v1/networks/" + suid.toString() + "/edges/" + edge1.getSUID()).request().get(
				String.class);
		assertNotNull(result);
		
		final JsonNode root = mapper.readTree(result);
		JsonNode data = root.get("data");
		System.out.println(result);
		assertNotNull(data);
		assertEquals(edge1.getSUID(), (Long)data.get("SUID").asLong());
		assertEquals(source.getSUID().toString(), data.get("source").asText());
		assertEquals(target.getSUID().toString(), data.get("target").asText());
		assertEquals(false, (Boolean)data.get("selected").asBoolean());
	}
	
	
}
