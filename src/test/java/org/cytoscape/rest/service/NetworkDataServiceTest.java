package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.NetworkTestSupport;
import org.cytoscape.rest.internal.service.NetworkDataService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NetworkDataServiceTest extends JerseyTest {

	private final NetworkTestSupport testSupport = new NetworkTestSupport();
	private CyNetwork network;

	private NetworkDataService nds = new NetworkDataService();

	@Before
	public void setUp() throws Exception {
		network = buildNetwork();
		nds = new NetworkDataService();
		assertNotNull(nds);
	}

	@After
	public void tearDown() throws Exception {
	}

	private final CyNetwork buildNetwork() {
		final CyNetwork network = testSupport.getNetwork();
		final CyNode node1 = network.addNode();
		final CyNode node2 = network.addNode();
		final CyEdge edge = network.addEdge(node1, node2, true);
		testSupport.getNetworkManager().addNetwork(network);

		assertEquals(testSupport.getNetworkManager().getNetworkSet().size(), 1);
		return network;
	}


	@Override
	protected Application configure() {
		return new ResourceConfig(NetworkDataService.class);
	}

	@Test
	public void test() {
	}

	@Test
	public void testGetNetworkCount() {
	}

	@Test
	public void testGetNodeCount() {
	}

	@Test
	public void testGetEdgeCount() {
	}

	@Test
	public void testGetNetworks() {
	}

	@Test
	public void testGetNetworkJSON() {
	}

	@Test
	public void testGetNodes() {
	}

	@Test
	public void testGetEdges() {
	}

	@Test
	public void testGetGraphObjectStringStringString() {
	}

	@Test
	public void testGetAdjEdges() {
	}

	@Test
	public void testGetNetworkPointer() {
	}

	@Test
	public void testGetNeighbours() {
	}

	@Test
	public void testCreateNode() {
	}

	@Test
	public void testUpdateNetwork() {
	}

	@Test
	public void testDeleteAllNetworks() {
	}

	@Test
	public void testDeleteNetwork() {
	}

	@Test
	public void testDeleteAllNodes() {
	}

	@Test
	public void testDeleteAllEdges() {
	}

	@Test
	public void testDeleteNode() {
	}

	@Test
	public void testDeleteEdge() {
	}

	@Test
	public void testCreateNetwork() {
	}

	@Test
	public void testRunNetworkTask() {
	}

	@Test
	public void testRunNetworkCollectionTask() {
	}

	@Test
	public void testRunStatelessTask() {
	}
}
