package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.NetworkTestSupport;
import org.cytoscape.rest.TaskFactoryManager;
import org.cytoscape.rest.internal.resource.NetworkResource;
import org.cytoscape.rest.internal.task.CyBinder;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.cytoscape.ding.NetworkViewTestSupport;

import static org.mockito.Mockito.*;


public class NetworkDataServiceTest {

	private final NetworkViewTestSupport testSupport = new NetworkViewTestSupport();
	private CyNetwork network;

	private NetworkResource nds = new NetworkResource();

	@Before
	public void setUp() throws Exception {
		network = buildNetwork();
		nds = new NetworkResource();
		assertNotNull(nds);
		
	}

	@After
	public void tearDown() throws Exception {
	}
	
	private final void createServer() {
		
		
		CyNetworkFactory networkFactory = testSupport.getNetworkFactory();
		CyNetworkManager networkManager = testSupport.getNetworkManager();
		CyNetworkViewFactory networkViewFactory = testSupport.getNetworkViewFactory();
		CyNetworkViewManager networkViewManager = mock(CyNetworkViewManager.class);
		VisualMappingManager vmm = mock(VisualMappingManager.class);
		CyApplicationManager applicationManager = mock(CyApplicationManager.class);

		TaskFactoryManager tfManager = mock(TaskFactoryManager.class);
		CyNetworkViewWriterFactory cytoscapeJsWriterFactory;
		//CyBinder binder = new CyBinder(networkManager, networkViewManager, networkFactory, tfManager , applicationManager, vmm, cytoscapeJsWriterFactory, cytoscapeJsReaderFactory)
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
