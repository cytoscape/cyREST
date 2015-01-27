package org.cytoscape.rest.internal.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.cytoscape.io.read.AbstractCyNetworkReader;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskMonitor;

/**
 * Network Reader for Edge Lists
 */
public class EdgeListReader extends AbstractCyNetworkReader {

	private final static Pattern SPLIT_PATTERN = Pattern.compile("[\\s\\t]+");
	private final static String SOURCE = "source";
	private final static String TARGET= "target";
	
	private final CyNetworkManager cyNetworkManager;
	private final CyRootNetworkManager cyRootNetworkManager;
	
	private final String collectionName;

	public EdgeListReader(final InputStream inputStream, final CyNetworkViewFactory cyNetworkViewFactory,
			final CyNetworkFactory cyNetworkFactory, final CyNetworkManager cyNetworkManager,
			final CyRootNetworkManager cyRootNetworkManager, final String collecitonName) {
		super(inputStream, cyNetworkViewFactory, cyNetworkFactory, cyNetworkManager, cyRootNetworkManager);
		this.collectionName = collecitonName;
		this.cyRootNetworkManager = cyRootNetworkManager;
		this.cyNetworkManager = cyNetworkManager;
	}
	
	private CyRootNetwork getRootNetworkByName(final String collectionName) {
		
		for (CyNetwork net : cyNetworkManager.getNetworkSet()) {
			final CyRootNetwork rootNet = cyRootNetworkManager.getRootNetwork(net);
			if(rootNet.getRow(rootNet).get(CyRootNetwork.NAME, String.class).equals(collectionName)) {
				return rootNet;
			}
		}
		
		return null;
	}

	@Override
	public CyNetworkView buildCyNetworkView(final CyNetwork network) {
		// No layout will be applied for performance.
		return this.cyNetworkViewFactory.createNetworkView(network);
	}


	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		try {
			read(taskMonitor);
		} finally {
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
		}
	}


	private final void read(TaskMonitor tm) throws IOException {
		String line;
		final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")
				.newDecoder()), 128 * 1024);

		CySubNetwork subNetwork = null;
		CyRootNetwork rootNetwork = getRootNetwork();
		
		if (rootNetwork != null) {
			// Root network exists
			subNetwork = rootNetwork.addSubNetwork();
		} else {
			// Search networks
			rootNetwork = getRootNetworkByName(collectionName);
			if(rootNetwork == null) {
				// Need to create new network with new root.
				subNetwork = (CySubNetwork) cyNetworkFactory.createNetwork();
			} else {
				subNetwork = rootNetwork.addSubNetwork();
			}
		}
		
		// Create default columns
		if(subNetwork.getDefaultEdgeTable().getColumn(SOURCE) == null)
			subNetwork.getDefaultEdgeTable().createColumn(SOURCE, String.class, true);
		if(subNetwork.getDefaultEdgeTable().getColumn(TARGET) == null)
			subNetwork.getDefaultEdgeTable().createColumn(TARGET, String.class, true);
		
		Map<Object, CyNode> nMap = new HashMap<>();

		while ((line = br.readLine()) != null) {
			
			// Cancel called. Clean up the garbage.
			if (cancelled) {
				nMap.clear();
				nMap = null;
				subNetwork = null;
				br.close();
				return;
			}

			// Ignore invalid line
			if (line.trim().length() <= 0)
				continue;

			final String[] parts = SPLIT_PATTERN.split(line);
			if (parts.length == 2) {
				CyNode sourceNode = nMap.get(parts[0]);
				if (sourceNode == null) {
					sourceNode = subNetwork.addNode();
					subNetwork.getRow(sourceNode).set(CyNetwork.NAME, parts[0]);
					nMap.put(parts[0], sourceNode);
				}
				CyNode targetNode = nMap.get(parts[1]);
				if (targetNode == null) {
					targetNode = subNetwork.addNode();
					subNetwork.getRow(targetNode).set(CyNetwork.NAME, parts[1]);
					nMap.put(parts[1], targetNode);
				}
				
				final CyEdge edge = subNetwork.addEdge(sourceNode, targetNode, true);
				subNetwork.getRow(edge).set(CyEdge.INTERACTION, "-");
				subNetwork.getRow(edge).set(SOURCE, parts[0]);
				subNetwork.getRow(edge).set(TARGET, parts[1]);
				
			} else if(parts.length == 1){
				// No edge will be added.  Node only.
			}
		}

		br.close();
		nMap.clear();
		nMap = null;

		this.networks = new CyNetwork[] { subNetwork };
	}
}