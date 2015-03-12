package org.cytoscape.rest.service;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javassist.bytecode.analysis.ControlFlow.Node;

import javax.ws.rs.core.Application;

import org.cytoscape.model.CyNode;
import org.cytoscape.rest.internal.resource.GlobalTableResource;
import org.cytoscape.rest.internal.resource.NetworkViewResource;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
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
	public void testGetViews() throws Exception {
		
		final Long suid = network.getSUID();
		final Long viewSuid = view.getSUID();
		
		String result = target("/v1/networks/" + suid.toString() + "/views/").request().get(
				String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		assertNotNull(root);
		System.out.println(root);
		assertTrue(root.isArray());
		assertEquals(1, root.size());
		assertEquals(viewSuid, Long.valueOf(root.get(0).asLong()));
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
		
		String result = target("/v1/networks/" + suid.toString() + "/views/" + viewSuid + "/nodes/" + node.getSUID()).request().get(
				String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
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
}