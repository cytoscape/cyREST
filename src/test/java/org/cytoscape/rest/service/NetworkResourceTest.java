package org.cytoscape.rest.service;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
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
import javax.xml.crypto.NodeSetData;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.rest.internal.resource.NetworkResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
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
		assertTrue(count == 2);
	}
	
	@Test
	public void testGetNetworks() throws Exception {
		String result = target("/v1/networks").request().get(
				String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		assertEquals(2, root.size());
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
