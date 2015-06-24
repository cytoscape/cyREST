package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.rest.internal.resource.NetworkResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

public class NetworkResourceDeletionTest extends BasicResourceTest {


	@Override
	protected Application configure() {
		return new ResourceConfig(NetworkResource.class);
	}

	@Test
	public void testGetNodes() throws Exception {
		final Long suid = network.getSUID();

		final List<CyNode> nodes = network.getNodeList();
		
		// Pick specific node:
		CyNode node1 = null;
		for(CyNode node: nodes) {
			if(network.getRow(node).get(CyNetwork.NAME, String.class).equals("n4")) {
				node1 = node;
				break;
			}
		}
		assertNotNull(node1);
		
		final Response result = target("/v1/networks/" + suid.toString() + "/nodes/" + node1.getSUID())
				.request().delete();
		assertEquals(200, result.getStatus());
		
		assertEquals(3, network.getNodeList().size());

	}


	@Test
	public void testGetEdges() throws Exception {
		final Long suid = network.getSUID();
		final List<CyEdge> edges = network.getEdgeList();
		final CyEdge edge1 = edges.get(0);
		assertNotNull(edge1);
		
		final Response result = target("/v1/networks/" + suid.toString() + "/edges/" + edge1.getSUID())
				.request().delete();
		assertEquals(200, result.getStatus());
		
		assertEquals(2, network.getEdgeList().size());

	}
}
