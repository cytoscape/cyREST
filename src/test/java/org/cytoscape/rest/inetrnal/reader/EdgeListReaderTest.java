package org.cytoscape.rest.inetrnal.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.ding.NetworkViewTestSupport;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.NetworkTestSupport;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.rest.internal.reader.EdgeListReader;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class EdgeListReaderTest {

	protected TaskMonitor taskMonitor;
	protected CyNetworkFactory netFactory;
	protected CyNetworkViewFactory viewFactory;
	protected CyLayoutAlgorithmManager layouts;
	protected CyNetworkManager networkManager;
	protected CyRootNetworkManager rootNetworkManager;
	protected CyApplicationManager cyApplicationManager;

	static final class EmptyTask extends AbstractTask {
		public void run(final TaskMonitor tm) {
		}
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		taskMonitor = mock(TaskMonitor.class);

		CyLayoutAlgorithm def = mock(CyLayoutAlgorithm.class);
		Object context = new Object();
		when(def.createLayoutContext()).thenReturn(context);
		when(def.getDefaultLayoutContext()).thenReturn(context);
		when(
				def.createTaskIterator(Mockito.any(CyNetworkView.class), Mockito.any(Object.class), Mockito.anySet(),
						Mockito.any(String.class))).thenReturn(new TaskIterator(new EmptyTask()));

		layouts = mock(CyLayoutAlgorithmManager.class);
		when(layouts.getDefaultLayout()).thenReturn(def);

		NetworkTestSupport nts = new NetworkTestSupport();
		netFactory = nts.getNetworkFactory();
		networkManager = nts.getNetworkManager();
		rootNetworkManager = nts.getRootNetworkFactory();

		cyApplicationManager = mock(CyApplicationManager.class);
		NetworkViewTestSupport nvts = new NetworkViewTestSupport();
		viewFactory = nvts.getNetworkViewFactory();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSmallEdgeList() throws Exception {
		final EdgeListReader reader = readFile("small.el");
		final CyNetwork[] networks = reader.getNetworks();
		assertNotNull(networks);
		assertEquals(1, networks.length);
		final CyNetwork network = networks[0];

		int nodeCount = network.getNodeCount();
		assertEquals(331, nodeCount);
		int edgeCount = network.getEdgeCount();
		assertEquals(362, edgeCount);

	}

	@Test
	public void testIgraphEdgeList() throws Exception {
		final EdgeListReader reader = readFile("net10k.txt");
		final CyNetwork[] networks = reader.getNetworks();
		assertNotNull(networks);
		assertEquals(1, networks.length);
		final CyNetwork network = networks[0];

		int nodeCount = network.getNodeCount();
		assertEquals(1000, nodeCount);
		int edgeCount = network.getEdgeCount();
		assertEquals(999, edgeCount);
	}

	private EdgeListReader readFile(final String fileName) throws Exception {
		File f = new File("./src/test/resources/" + fileName);
		EdgeListReader snvp = new EdgeListReader(new FileInputStream(f), viewFactory, netFactory, this.networkManager,
				this.rootNetworkManager);
		new TaskIterator(snvp);
		snvp.run(taskMonitor);

		return snvp;
	}

}
