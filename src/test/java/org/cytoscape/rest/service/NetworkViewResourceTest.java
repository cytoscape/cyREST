package org.cytoscape.rest.service;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Paint;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javassist.bytecode.analysis.ControlFlow.Node;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.rest.internal.resource.GlobalTableResource;
import org.cytoscape.rest.internal.resource.NetworkViewResource;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NetworkViewResourceTest extends BasicResourceTest {

	private ObjectMapper mapper = new ObjectMapper();

	
	@Override
	protected Application configure() {
		return new ResourceConfig(NetworkViewResource.class);
	}
	
	
	@Test
	public void testConstructor() throws Exception {
		NetworkViewResource nvr = new NetworkViewResource();
		assertNotNull(nvr);
	}

	@Test
	public void testCreateNetworkView() throws Exception {
		final Long suid = network.getSUID();
		
		final Entity<String> entity = Entity.entity("", MediaType.APPLICATION_JSON_TYPE);
		Response result = target("/v1/networks/" + suid.toString() + "/views").request().post(entity);
		assertNotNull(result);
		System.out.println("res: " + result.toString());
		assertFalse(result.getStatus() == 500);
		assertEquals(201, result.getStatus());
		
		final String body = result.readEntity(String.class);
		System.out.println("BODY: " + body);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.isObject());
		assertNotNull(root.get("networkViewSUID").asLong());
		
	}
	
	@Test
	public void testGetViews() throws Exception {
		
		final Long suid = network.getSUID();
		final Long viewSuid = view.getSUID();
		
		Response result = target("/v1/networks/" + suid.toString() + "/views").request().get();
		assertNotNull(result);
		String body = result.readEntity(String.class);
		System.out.println("Result = " + result);

		final JsonNode root = mapper.readTree(body);
		assertNotNull(root);
		System.out.println(root);
		assertTrue(root.isArray());
		assertEquals(1, root.size());
		assertEquals(viewSuid, Long.valueOf(root.get(0).asLong()));
	}


	@Test
	public void testGetNetworkView() throws Exception {
		
		final Long suid = network.getSUID();
		final Long viewSuid = view.getSUID();
		
		String result = target("/v1/networks/" + suid.toString() + "/views/" + viewSuid.toString()).request().get(
				String.class);
		assertNotNull(result);
		System.out.println("Result = " + result);
		// TODO: Mock JSON writer?
	}
	

	@Test
	public void testGetFirstNetworkImage() throws Exception {
		
		final Long suid = network.getSUID();
		
		Response result = target("/v1/networks/" + suid.toString() + "/views/first.png").request().get();
		assertNotNull(result);
		assertEquals(500, result.getStatus());
		System.out.println("Result = " + result);
		result = target("/v1/networks/" + suid.toString() + "/views/first.svg").request().get();
		assertNotNull(result);
		assertEquals(500, result.getStatus());
		System.out.println("Result = " + result);
		result = target("/v1/networks/" + suid.toString() + "/views/first.pdf").request().get();
		assertNotNull(result);
		assertEquals(500, result.getStatus());
		System.out.println("Result = " + result);
		// TODO: Mock IMAGE writer?
	}
	
	@Test
	public void testGetNetworkImage() throws Exception {
		
		final Long suid = network.getSUID();
		final Long viewSuid = view.getSUID();
		
		Response result = target("/v1/networks/" + suid.toString() + "/views/"+ viewSuid.toString() +".png").request().get();
		assertNotNull(result);
		assertEquals(500, result.getStatus());
		System.out.println("Result = " + result);
		result = target("/v1/networks/" + suid.toString() + "/views/"+ viewSuid.toString() +".svg").request().get();
		assertNotNull(result);
		assertEquals(500, result.getStatus());
		System.out.println("Result = " + result);
		result = target("/v1/networks/" + suid.toString() + "/views/"+ viewSuid.toString() +".pdf").request().get();
		assertNotNull(result);
		assertEquals(500, result.getStatus());
		System.out.println("Result = " + result);
		// TODO: Mock IMAGE writer?
	}


	@Test
	public void testGetFirstViews() throws Exception {
		
		final Long suid = network.getSUID();
		Response result = target("/v1/networks/" + suid.toString() + "/views/first").request().get();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
	}
	
	@Test
	public void testGetViewCount() throws Exception {
		
		final Long suid = network.getSUID();
		
		String result = target("/v1/networks/" + suid.toString() + "/views/count").request().get(
				String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		assertNotNull(root);
		System.out.println(root);
		assertEquals(1, root.get("count").asInt());
	}
	
	@Test
	public void testGetView() throws Exception {
		
		final Long suid = network.getSUID();
		final Long viewSuid = view.getSUID();
		final CyNode node = network.getNodeList().iterator().next();
		
		Response result = target("/v1/networks/" + suid.toString() + "/views/" + viewSuid + "/nodes/" + node.getSUID()).request().get();
		assertNotNull(result);
		System.out.println("res: " + result.toString());
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		
		final JsonNode root = mapper.readTree(result.readEntity(String.class));
		assertNotNull(root);
		System.out.println(root);
		assertTrue(root.isArray());
		assertEquals(lexicon.getAllDescendants(BasicVisualLexicon.NODE).size(), root.size());
		
		final Map<String, String> vps = new HashMap<>();
		for(JsonNode n: root) {
			vps.put(n.get("visualProperty").asText(), n.get("value").asText());
		}
		
		assertTrue(vps.keySet().contains("NODE_PAINT"));
		assertTrue(vps.keySet().contains("NODE_BORDER_PAINT"));
		assertEquals("#000000", vps.get("NODE_BORDER_PAINT"));
	}


	@Test
	public void testGetNetworkViewVP() throws Exception {
		final Long suid = network.getSUID();
		final Long viewSuid = view.getSUID();
		
		final String vp = "NETWORK_BACKGROUND_PAINT";
		
		Response result = target("/v1/networks/" + suid.toString() + "/views/" + viewSuid + "/network/" + vp).request().get();
		assertNotNull(result);
		System.out.println("res: " + result.toString());
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		
		final JsonNode root = mapper.readTree(result.readEntity(String.class));
		assertNotNull(root);
		System.out.println(root);
		assertTrue(root.isObject());
		
		final String vpName = root.get("visualProperty").asText();
		assertEquals(vp, vpName);
		
		final String val = root.get("value").asText();
		assertEquals("#ffffff".toUpperCase(), val.toUpperCase());
	}


	@Test
	public void testUpdateNetworkViewVP() throws Exception {
		final Long suid = network.getSUID();
		final Long viewSuid = view.getSUID();
		
		final String vp = "NETWORK_BACKGROUND_PAINT";
		final String newVal = "[{"
				+ "\"visualProperty\": \"" + vp + "\","
				+ "\"value\": \"red\" }]";
		
		Entity<String> entity = Entity.entity(newVal, MediaType.APPLICATION_JSON_TYPE);
		Response result = target("/v1/networks/" + suid.toString() + "/views/" + viewSuid + "/network").request().put(entity);
		assertNotNull(result);
		System.out.println("res: " + result.toString());
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		
		final Paint newValue = view.getVisualProperty(BasicVisualLexicon.NETWORK_BACKGROUND_PAINT);
		assertEquals(Color.RED, newValue);
	}


	@Test
	public void testUpdateView() throws Exception {
		final Long suid = network.getSUID();
		final Long viewSuid = view.getSUID();
	
		// Test node VPs
		final CyNode node = network.getNodeList().iterator().next();
		String vp = BasicVisualLexicon.NODE_FILL_COLOR.getIdString();
		String newVal = "[{"
				+ "\"visualProperty\": \"" + vp + "\","
				+ "\"value\": \"blue\" }]";
		testViewUpdates(newVal, suid, viewSuid, "nodes", node.getSUID());
		Paint newColor = view.getNodeView(node).getVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR);
		assertEquals(Color.BLUE, newColor);
		
		vp = BasicVisualLexicon.NODE_WIDTH.getIdString();
		newVal = "[{"
				+ "\"visualProperty\": \"" + vp + "\","
				+ "\"value\": 200.0 }]";
		testViewUpdates(newVal, suid, viewSuid, "nodes", node.getSUID());
		Double newWidth = view.getNodeView(node).getVisualProperty(BasicVisualLexicon.NODE_WIDTH);
		assertEquals(new Double(200), newWidth);
		
		// Test edges
		final CyEdge edge = network.getEdgeList().iterator().next();
		vp = BasicVisualLexicon.EDGE_WIDTH.getIdString();
		newVal = "[{"
				+ "\"visualProperty\": \"" + vp + "\","
				+ "\"value\": 22.1 }]";
		testViewUpdates(newVal, suid, viewSuid, "edges", edge.getSUID());
		newWidth = view.getEdgeView(edge).getVisualProperty(BasicVisualLexicon.EDGE_WIDTH);
		assertEquals(new Double(22.1), newWidth);
	}
	
	private void testViewUpdates(final String newVal, final Long suid, final Long viewSuid, final String type, final Long objId) {
		Entity<String> entity = Entity.entity(newVal, MediaType.APPLICATION_JSON_TYPE);
		Response result = target("/v1/networks/" + suid.toString() + "/views/" + viewSuid + "/" + type + "/" + objId).request().put(entity);
		assertNotNull(result);
		System.out.println("res: " + result.toString());
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
	}
	
	
	@Test
	public void testDeleteAllNetworkViews() throws Exception {
		
		final Response result = target("/v1/networks/" + network.getSUID().toString() + "/views").request().delete();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
	}
	
	@Test
	public void testDeleteFirstView() throws Exception {
		final Long suid = network.getSUID();
		
		final Response result = target("/v1/networks/" + suid.toString() + "/views/first").request().delete();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
	}
	
}