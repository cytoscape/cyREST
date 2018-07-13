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
import org.cytoscape.rest.internal.model.NetworkSUIDModel;
import org.cytoscape.rest.internal.model.NetworkViewSUIDModel;
import org.cytoscape.rest.internal.resource.NetworkResource;
import org.cytoscape.view.model.CyNetworkView;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NetworkResourceTest extends BasicResourceTest {
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	protected Application configure() {
		return new ResourceConfig(NetworkResource.class);
	}
	
	@Test
	public void testGetNetworkCount() throws Exception {
		Response result = target("/v1/networks/count").request().get();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		final String body = result.readEntity(String.class);
		System.out.println("BODY: " + body);
		
		final JsonNode root = mapper.readTree(body);
		Long count = root.get("count").asLong();
		assertTrue(count == 2);
	}
	
	@Test
	public void testGetNetworks() throws Exception {
		String result = target("/v1/networks").request().get(
				String.class);
		assertNotNull(result);
		System.out.println(result);
		final JsonNode root = mapper.readTree(result);
		assertEquals(2, root.size());
	}

	@Test
	public void testGetCurrentNetwork() throws Exception {
		CyNetwork currentNetwork = mock(CyNetwork.class);
		when(currentNetwork.getSUID()).thenReturn(668l);
		when(cyApplicationManager.getCurrentNetwork()).thenReturn(currentNetwork);
		String result = target("/v1/networks/currentNetwork").request().get(
				String.class);
		assertNotNull(result);
		System.out.println(result);
		final JsonNode root = mapper.readTree(result);
		assertEquals(2, root.size());
		JsonNode data = root.get("data");
		long suid = data.get("networkSUID").asLong();
		assertEquals(668l, suid);
	}
	
	@Test
	public void testPutCurrentNetwork() throws Exception {
		
		NetworkSUIDModel networkSUIDModel = new NetworkSUIDModel(network.getSUID());
		Entity<NetworkSUIDModel> entity = Entity.entity(networkSUIDModel, MediaType.APPLICATION_JSON);
	
		Response response = target("/v1/networks/currentNetwork").request().put(
				entity);
		String result = response.readEntity(String.class);
		assertNotNull(result);
		System.out.println(result);
		final JsonNode root = mapper.readTree(result);
		assertEquals(2, root.size());
		assertTrue(root.has("data"));
		assertTrue(root.has("errors"));
		assertEquals(0,root.get("errors").size());
		verify(cyApplicationManager).setCurrentNetwork(network);
	}
	
	@Test
	public void testGetCurrentNetworkView() throws Exception {
		CyNetworkView currentNetworkView = mock(CyNetworkView.class);
		when(currentNetworkView.getSUID()).thenReturn(664l);
		when(cyApplicationManager.getCurrentNetworkView()).thenReturn(currentNetworkView);
		String result = target("/v1/networks/views/currentNetworkView").request().get(
				String.class);
		assertNotNull(result);
		System.out.println(result);
		final JsonNode root = mapper.readTree(result);
		assertEquals(2, root.size());
		JsonNode data = root.get("data");
		long suid = data.get("networkViewSUID").asLong();
		assertEquals(664l, suid);
	}
	

	@Test
	public void testPutCurrentNetworkView() throws Exception {
		
		NetworkViewSUIDModel networkViewSUIDModel = new NetworkViewSUIDModel(view.getSUID());
		Entity<NetworkViewSUIDModel> entity = Entity.entity(networkViewSUIDModel, MediaType.APPLICATION_JSON);
	
		Response response = target("/v1/networks/views/currentNetworkView").request().put(
				entity);
		String result = response.readEntity(String.class);
		assertNotNull(result);
		System.out.println(result);
		final JsonNode root = mapper.readTree(result);
		assertEquals(2, root.size());
		assertTrue(root.has("data"));
		assertTrue(root.has("errors"));
		assertEquals(0,root.get("errors").size());
		verify(cyApplicationManager).setCurrentNetworkView(view);
	}
	
	@Test
	public void testGetNetworksQueryNullProducesError() throws Exception {
		Response response = target("/v1/networks").queryParam("column", "dummy").request().get();
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
	public void testGetNetworksColumnNullProducesError() throws Exception {
		Response response = target("/v1/networks").queryParam("query", "dummy").request().get();
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
	public void testGetNetworksQuery() throws Exception {
		Response result = target("/v1/networks").queryParam("column", "dummy").queryParam("query", "dummy").request().get();
		assertNotNull(result);
		System.out.println(result.readEntity(String.class));
		//TODO Verify for actual queries
		assertEquals(200, result.getStatus());
	}
	
	@Test
	public void testGetNodeCount() throws Exception {
		final Long suid = network.getSUID();
		Response result = target("/v1/networks/" + suid.toString() + "/nodes/count").request().get();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		final String body = result.readEntity(String.class);
		System.out.println("BODY: " + body);
		
		final JsonNode root = mapper.readTree(body);
		Long count = root.get("count").asLong();
		assertTrue(count == 4);
	}
	
	@Test
	public void testGetEdgeCount() throws Exception {
		final Long suid = network.getSUID();
		Response result = target("/v1/networks/" + suid.toString() + "/edges/count").request().get();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		final String body = result.readEntity(String.class);
		System.out.println("BODY: " + body);
		final JsonNode root = mapper.readTree(body);
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
	/**
	 * Test to delete node and then get all nodes.  This is currently broken.
	 * 
	 * @throws Exception
	 */
	public void testDeleteAndGetNodes() throws Exception {
		final Long suid = network.getSUID();
		final int nodeCount = network.getNodeCount();
		
		// First, delete first node
		final CyNode node1 = network.getNodeList().get(0);
		final Long nodeSuid = node1.getSUID();
		final Response delResult = target("/v1/networks/" + suid.toString() + "/nodes/" + nodeSuid).request().delete();
		assertNotNull(delResult);
		assertEquals(200, delResult.getStatus());
		
		assertEquals(nodeCount-1, network.getNodeCount());
		
		String result = target("/v1/networks/" + suid.toString() + "/nodes").request().get(String.class);
		assertNotNull(result);
		
		final JsonNode root = mapper.readTree(result);
		assertTrue(root.isArray());
		assertEquals(nodeCount-1, root.size());
	}
	
	@Test
	public void testDeleteAndGetNodesByQuery() throws Exception {
		final Long suid = network.getSUID();
		final int nodeCount = network.getNodeCount();
		
		// First, delete two nodes
		final CyNode node1 = network.getNodeList().get(0);
		final CyNode node2 = network.getNodeList().get(1);
		final CyNode node3 = network.getNodeList().get(2);
		final Long nodeSuid1 = node1.getSUID();
		final Long nodeSuid2 = node2.getSUID();
		
		String nodeName1 = network.getRow(node1).get(CyNetwork.NAME, String.class);
		String nodeName2 = network.getRow(node2).get(CyNetwork.NAME, String.class);
		String nodeName3 = network.getRow(node3).get(CyNetwork.NAME, String.class);
		
		Response delResult = target("/v1/networks/" + suid.toString() + "/nodes/" + nodeSuid1).request().delete();
		assertNotNull(delResult);
		assertEquals(200, delResult.getStatus());
		
		delResult = target("/v1/networks/" + suid.toString() + "/nodes/" + nodeSuid2).request().delete();
		assertNotNull(delResult);
		assertEquals(200, delResult.getStatus());
		
		assertEquals(nodeCount-2, network.getNodeCount());
	
		// Check node count
		final String countResult = target("/v1/networks/" + suid.toString() + "/nodes/count").request().get(String.class);
		assertNotNull(countResult);
		System.out.println(countResult);
		final JsonNode countRoot = mapper.readTree(countResult);
		System.out.println(countRoot.get("count"));
		assertEquals(nodeCount-2, countRoot.get("count").asInt());
		
		// Get by query
		
		// Test existing node
		System.out.println(nodeName1);
		System.out.println(nodeName2);
		System.out.println(nodeName3);
		String result = target("/v1/networks/" + suid.toString() + "/nodes").request().get(String.class);
		assertNotNull(result);
		System.out.println(result);
		
		JsonNode root = mapper.readTree(result);
		assertTrue(root.isArray());
		assertEquals(nodeCount-2, root.size());
		
		String queryUrl = "/v1/networks/" + suid.toString() + "/nodes";
		result = target(queryUrl).queryParam("query", nodeName3).queryParam("column", "name").request().get(String.class);
		assertNotNull(result);
		System.out.println(result);
		root = mapper.readTree(result);
		assertTrue(root.isArray());
		assertEquals(1, root.size());
		assertEquals(node3.getSUID(), (Long)root.get(0).asLong());
		
		// Check with deleted node
		result = target(queryUrl).queryParam("query", nodeName1).queryParam("column", "name").request().get(String.class);
		assertNotNull(result);
		root = mapper.readTree(result);
		assertTrue(root.isArray());
		assertEquals(0, root.size());
		
		result = target(queryUrl).queryParam("query", nodeName2).queryParam("column", "name").request().get(String.class);
		assertNotNull(result);
		root = mapper.readTree(result);
		assertTrue(root.isArray());
		assertEquals(0, root.size());
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
	public void testPutSelectedNodes() throws Exception {
		final Long suid = network.getSUID();
		final List<CyNode> nodes = network.getNodeList();
		final CyNode node1 =  nodes.get(0);
		final CyNode node2 =  nodes.get(1);
		final CyNode node3 =  nodes.get(2);
		// Set selected.
		
		network.getRow(node1).set(CyNetwork.SELECTED, false);
		network.getRow(node2).set(CyNetwork.SELECTED, false);
		network.getRow(node3).set(CyNetwork.SELECTED, false);
		
		final Entity<String> entity = Entity.entity("[" + node1.getSUID() +"," + node2.getSUID() +"]", MediaType.APPLICATION_JSON_TYPE);
				
		Response response = target("/v1/networks/" + suid.toString() + "/nodes/selected")
				.request()
				.put(entity);
		
		assertEquals(200, response.getStatus());
		
		assertTrue(network.getRow(node1).get(CyNetwork.SELECTED, Boolean.class));
		assertTrue(network.getRow(node2).get(CyNetwork.SELECTED, Boolean.class));
		assertFalse(network.getRow(node3).get(CyNetwork.SELECTED, Boolean.class));
	}
	
	@Test
	public void testPutSelectedEdges() throws Exception {
		final Long suid = network.getSUID();
		final List<CyEdge> edges = network.getEdgeList();
		final CyEdge edge1 =  edges.get(0);
		final CyEdge edge2 =  edges.get(1);
		final CyEdge edge3 =  edges.get(2);
		// Set selected.
		
		network.getRow(edge1).set(CyNetwork.SELECTED, false);
		network.getRow(edge2).set(CyNetwork.SELECTED, false);
		network.getRow(edge3).set(CyNetwork.SELECTED, false);
		
		final Entity<String> entity = Entity.entity("[" + edge1.getSUID() +"," + edge2.getSUID() +"]", MediaType.APPLICATION_JSON_TYPE);
				
		Response response = target("/v1/networks/" + suid.toString() + "/edges/selected")
				.request()
				.put(entity);
		
		assertEquals(200, response.getStatus());
		
		assertTrue(network.getRow(edge1).get(CyNetwork.SELECTED, Boolean.class));
		assertTrue(network.getRow(edge2).get(CyNetwork.SELECTED, Boolean.class));
		assertFalse(network.getRow(edge3).get(CyNetwork.SELECTED, Boolean.class));
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
	public void testGetNetworkPointer() throws Exception {
		final Long suid = network.getSUID();
		final List<CyNode> nodes = network.getNodeList();
		final CyNode node1 =  nodes.get(0);
		Response result = target("/v1/networks/" + suid.toString() + "/nodes/" + node1.getSUID() + "/pointer").request().get();
		assertNotNull(result);
		assertEquals(404, result.getStatus());
		
		// Now set pointer to a network
		node1.setNetworkPointer(network);
		result = target("/v1/networks/" + suid.toString() + "/nodes/" + node1.getSUID() + "/pointer").request().get();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		
		final String body = result.readEntity(String.class);
		System.out.println("BODY: " + body);
		
		final JsonNode root = mapper.readTree(body);
		JsonNode data = root.get("networkSUID");
		assertNotNull(data);
		assertEquals(network.getSUID(), (Long)data.asLong());
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
	
	@Test
	public void testGetEdgeSource() throws Exception {
		final Long suid = network.getSUID();
		final List<CyEdge> edges = network.getEdgeList();
		final CyEdge edge1 =  edges.get(0);
		final CyNode source = edge1.getSource();
		
		String result = target("/v1/networks/" + suid.toString() + "/edges/" + edge1.getSUID() + "/source").request().get(
				String.class);
		assertNotNull(result);
		
		final JsonNode root = mapper.readTree(result);

		System.out.println(result);
		assertNotNull(root);
		assertEquals(source.getSUID().toString(), root.get("source").asText());
	
	}
	
	@Test
	public void testGetEdgeSourceInvalidEdge() throws Exception {
		final Long suid = network.getSUID();
		final List<CyEdge> edges = network.getEdgeList();
		final CyEdge edge1 =  edges.get(0);
		final CyNode source = edge1.getSource();
		
		Response  response = target("/v1/networks/" + suid.toString() + "/edges/" + 0l + "/source").request().get();
			
		assertEquals(404, response.getStatus());
		JsonNode ci = mapper.readTree(response.readEntity(String.class));
		assertEquals(1, ci.get("errors").size());
		assertEquals("urn:cytoscape:ci:cyrest-core:v1:networks:errors:1", ci.get("errors").get(0).get("type").asText());
	
	}
	
	@Test
	public void testGetEdgeInvalidType() throws Exception {
		final Long suid = network.getSUID();
		final List<CyEdge> edges = network.getEdgeList();
		final CyEdge edge1 =  edges.get(0);
		
		Response response = target("/v1/networks/" + suid.toString() + "/edges/" + edge1.getSUID() + "/invalid_type").request().get();
		
		assertEquals(404, response.getStatus());
		JsonNode ci = mapper.readTree(response.readEntity(String.class));
		assertEquals(1, ci.get("errors").size());
		assertEquals("urn:cytoscape:ci:cyrest-core:v1:networks:errors:1", ci.get("errors").get(0).get("type").asText());
	
	}
	
	@Test
	public void testGetEdgeTarget() throws Exception {
		final Long suid = network.getSUID();
		final List<CyEdge> edges = network.getEdgeList();
		final CyEdge edge1 =  edges.get(0);
		final CyNode target = edge1.getTarget();
		
		String result = target("/v1/networks/" + suid.toString() + "/edges/" + edge1.getSUID() + "/target").request().get(
				String.class);
		assertNotNull(result);
		
		final JsonNode root = mapper.readTree(result);

		System.out.println(result);
		assertNotNull(root);
		assertEquals(target.getSUID().toString(), root.get("target").asText());
	
	}
	
	
	@Test
	public void testCreateNode() throws Exception {
		final Long suid = network.getSUID();
		final int originalNodeCount = network.getNodeCount();
		
		final String newVal = "[\"new_node_1\", \"new_node_2\"]";
		final Entity<String> entity = Entity.entity(newVal, MediaType.APPLICATION_JSON_TYPE);
		Response result = target("/v1/networks/" + suid.toString() + "/nodes").request().post(entity);
		assertNotNull(result);
		System.out.println("res: " + result.toString());
		assertFalse(result.getStatus() == 500);
		assertEquals(201, result.getStatus());
		
		final String body = result.readEntity(String.class);
		System.out.println("BODY: " + body);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.isArray());
		final Set<String> names = StreamSupport.stream(root.spliterator(), false)
			.map(entry->entry.get("name").asText())
			.collect(Collectors.toSet());
		assertTrue(names.contains("new_node_1"));
		assertTrue(names.contains("new_node_2"));
		
		assertEquals(2, network.getNodeCount() - originalNodeCount);
		final List<CyNode> nodes = network.getNodeList();
		final Set<String> nodeNames = nodes.stream()
			.map(node->network.getRow(node).get(CyNetwork.NAME, String.class))
			.collect(Collectors.toSet());
		System.out.println(nodeNames);
		assertTrue(nodeNames.contains("new_node_1"));
		assertTrue(nodeNames.contains("new_node_2"));
	}
	
	
	private final String createEdgeJson() throws Exception {
		final JsonFactory factory = new JsonFactory();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = null;
		generator = factory.createGenerator(stream);
		generator.writeStartArray();
		
		final List<CyNode> nodes = network.getNodeList();
		
		Map<String, Long> idmap = nodes.stream()
			.collect(Collectors.toMap(
						node->network.getRow(node).get(CyNetwork.NAME, String.class), 
						n->n.getSUID()
					)
				);
		
		System.out.println("Nodes: " + idmap);
		
		// Edge 1:
		generator.writeStartObject();
		generator.writeNumberField("source", idmap.get("n1"));
		generator.writeNumberField("target", idmap.get("n4"));
		generator.writeStringField("interaction", "itr1");
		generator.writeBooleanField("directed", true);
		generator.writeEndObject();
		
		// Edge 2:
		generator.writeStartObject();
		generator.writeNumberField("source", idmap.get("n2"));
		generator.writeNumberField("target", idmap.get("n4"));
		generator.writeStringField("interaction", "itr1");
		generator.writeEndObject();
		
		// Edge 3:
		generator.writeStartObject();
		generator.writeNumberField("source", idmap.get("n3"));
		generator.writeNumberField("target", idmap.get("n1"));
		generator.writeEndObject();
		
		// Edge 4:
		generator.writeStartObject();
		generator.writeNumberField("source", idmap.get("n3"));
		generator.writeNumberField("target", idmap.get("n3"));
		generator.writeStringField("interaction", "itr2");
		generator.writeBooleanField("directed", false);
		generator.writeEndObject();
		
		generator.writeEndArray();
		generator.close();
		final String result = stream.toString("UTF-8");
		stream.close();
		return result;
	}
	
	@Test
	public void testCreateEdge() throws Exception {
		final Long suid = network.getSUID();
		final int originalEdgeCount = network.getEdgeCount();
		
		final String newVal = createEdgeJson();
		System.out.println("New values: " + newVal);
		final Entity<String> entity = Entity.entity(newVal, MediaType.APPLICATION_JSON_TYPE);
		Response result = target("/v1/networks/" + suid.toString() + "/edges").request().post(entity);
		assertNotNull(result);
		System.out.println("res: " + result.toString());
		assertFalse(result.getStatus() == 500);
		assertEquals(201, result.getStatus());
		
		final String body = result.readEntity(String.class);
		System.out.println("BODY: " + body);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.isArray());
		List<Long> sources = StreamSupport.stream(root.spliterator(), false)
			.map(entry->entry.get("source").asLong())
			.collect(Collectors.toList());
		
		System.out.println("Source: " + sources);
		assertEquals(4, network.getEdgeCount() - originalEdgeCount);
		assertEquals(4, sources.size());
	}

	
	
	private final String createNetworkJson() throws Exception {
		final JsonFactory factory = new JsonFactory();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = null;
		generator = factory.createGenerator(stream);
		generator.writeStartObject();
		
		// Network Data
		generator.writeObjectFieldStart("data");
		generator.writeStringField("name", "new_network1");
		generator.writeEndObject();
		
		generator.writeObjectFieldStart("elements");
		
		generator.writeArrayFieldStart("nodes");
		generator.writeEndArray();
		generator.writeArrayFieldStart("edges");
		generator.writeEndArray();
		
		generator.writeEndObject();
		
		
		generator.writeEndObject();
		generator.close();
		final String result = stream.toString("UTF-8");
		stream.close();
		return result;
	}
	
	private final String createNetworkListJson(String sourceLocation) throws Exception {
		final JsonFactory factory = new JsonFactory();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = null;
		
		generator = factory.createGenerator(stream);
		generator.writeStartArray();
		
		// Entry 1
		generator.writeStartObject();
		generator.writeStringField("source_location", sourceLocation);
		generator.writeStringField("source_method", "GET");
		generator.writeStringField("ndex_uuid", "12345");
		generator.writeEndObject();
		
		
		generator.writeEndArray();
		generator.close();
		final String result = stream.toString("UTF-8");
		stream.close();
		return result;
	}

	@Test
	public void testCreateNetworkFromUrl() throws Exception {
		String urlString = "https://raw.githubusercontent.com/cytoscape/cytoscape-impl/develop/io-impl/impl/src/test/resources/testData/sif/sample.sif";
		final String newVal = createNetworkListJson(urlString);
		System.out.println("New values: " + newVal);
		final Entity<String> entity = Entity.entity(newVal, MediaType.APPLICATION_JSON_TYPE);
		Response result = target("/v1/networks").queryParam("source", "url").queryParam("format", "edgelist").request().post(entity);
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		verify(loadNetworkURLTaskFactory).loadCyNetworks(eq(new URL(urlString)));
		final String body = result.readEntity(String.class);
		final JsonNode root = mapper.readTree(body);
		assertEquals(urlString, root.get(0).get("source").asText());
		System.out.println("BODY: " + body);
		System.out.println("res: " + result.toString());
	}
	
	@Test
	public void testCreateNetworkFromUrlCx() throws Exception {
		verify(tfManager, never()).getInputStreamTaskFactory(eq("cytoscapeCxNetworkReaderFactory"));
		verify(inputStreamCXTaskFactory, never()).createTaskIterator(any(InputStream.class), eq("cx file"));
		verify(inputStreamCXNetworkReader, never()).getNetworks();
		
		
		String urlString = "http://www.ndexbio.org/v2/network/01c83ba5-0d90-11e6-b550-06603eb7f303?download=true";
		final String newVal = createNetworkListJson(urlString);
		System.out.println("New values: " + newVal);
		final Entity<String> entity = Entity.entity(newVal, MediaType.APPLICATION_JSON_TYPE);
		Response result = target("/v1/networks").queryParam("source", "url").queryParam("format", "cx").request().post(entity);
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		verify(tfManager).getInputStreamTaskFactory(eq("cytoscapeCxNetworkReaderFactory"));
		verify(inputStreamCXTaskFactory).createTaskIterator(any(InputStream.class), eq("cx file"));
		verify(inputStreamCXNetworkReader).getNetworks();
		//verify(rootNetworkManager).getRootNetwork(arg0);
		final String body = result.readEntity(String.class);
		System.out.println("BODY: " + body);
		final JsonNode root = mapper.readTree(body);
		assertEquals(urlString, root.get(0).get("source").asText());
		System.out.println("res: " + result.toString());
	}
	
	@Test
	public void testCreateNetwork() throws Exception {
		
		final String newVal = createNetworkJson();
		System.out.println("New values: " + newVal);
		final Entity<String> entity = Entity.entity(newVal, MediaType.APPLICATION_JSON_TYPE);
		Response result = target("/v1/networks").request().post(entity);
		assertNotNull(result);
		final String body = result.readEntity(String.class);
		System.out.println("BODY: " + body);
		System.out.println("res: " + result.toString());
//		assertFalse(result.getStatus() == 500);
//		assertEquals(201, result.getStatus());
	}


	@Test
	public void testCreateNetworkFromSelected() throws Exception {
		
		final String newVal = createNetworkJson();
		System.out.println("New values: " + newVal);
		final Entity<String> entity = Entity.entity(newVal, MediaType.APPLICATION_JSON_TYPE);
		Response result = target("/v1/networks/" + network.getSUID()).queryParam("title","network from selection").request().post(entity);
		assertNotNull(result);
		final String body = result.readEntity(String.class);
		System.out.println("BODY: " + body);
		System.out.println("res: " + result.toString());
		
		assertEquals(200, result.getStatus());
	}
	
	@Test
	public void testGetNeighbours() throws Exception {
		final Long suid = network.getSUID();
		
		final CyNode node = network.getNodeList().get(1);
		
		String result = target("/v1/networks/" + suid.toString() + "/nodes/" + node.getSUID() + "/neighbors").request().get(String.class);
		assertNotNull(result);
		
		final JsonNode root = mapper.readTree(result);
		System.out.println(result);
		assertTrue(root.isArray());
		assertEquals(2, root.size());
	}


	@Test
	public void testDeleteNode() throws Exception {
		final Long suid = network.getSUID();
		final CyNode node = network.getNodeList().get(1);
		final Long nodeSuid = node.getSUID();
		final int nodeCount = network.getNodeCount();
		
		final Response result = target("/v1/networks/" + suid.toString() + "/nodes/" + nodeSuid).request().delete();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		
		assertNull(network.getNode(nodeSuid));
		assertEquals(nodeCount-1, network.getNodeCount());
	}
	
	@Test
	public void testDeleteAllNodes() throws Exception {
		final Long suid = network.getSUID();
		
		final Response result = target("/v1/networks/" + suid.toString() + "/nodes").request().delete();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		
		assertEquals(0, network.getNodeCount());
	}
	
	
	@Test
	public void testDeleteEdge() throws Exception {
		final Long suid = network.getSUID();
		final CyEdge edge = network.getEdgeList().get(1);
		final Long edgeSuid = edge.getSUID();
		final int edgeCount = network.getEdgeCount();
		
		final Response result = target("/v1/networks/" + suid.toString() + "/edges/" + edgeSuid).request().delete();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		
		assertNull(network.getNode(edgeSuid));
		assertEquals(edgeCount-1, network.getEdgeCount());
	}
	
	@Test
	public void testDeleteEdge404() throws Exception {
		final Long suid = network.getSUID();
		
		assertFalse(network.getEdgeList().contains(665l));
		
		final Response result = target("/v1/networks/" + suid.toString() + "/edges/" + 665).request().delete();
		assertNotNull(result);
		assertEquals(404, result.getStatus());
	}
	
	@Test
	public void testDeleteAllEdges() throws Exception {
		final Long suid = network.getSUID();
		
		final Response result = target("/v1/networks/" + suid.toString() + "/edges").request().delete();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		
		assertEquals(0, network.getEdgeCount());
	}


	@Test
	public void testDeleteAllNetwork() throws Exception {
		assertEquals(2, this.networkManager.getNetworkSet().size());

		final Response result = target("/v1/networks").request().delete();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		assertEquals(0, this.networkManager.getNetworkSet().size());
	}
	
	
	@Test
	public void testDeleteNetwork() throws Exception {
		assertEquals(2, this.networkManager.getNetworkSet().size());
		final Long suid = network.getSUID();

		final Response result = target("/v1/networks/" + suid).request().delete();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		assertEquals(1, this.networkManager.getNetworkSet().size());
	}
}
