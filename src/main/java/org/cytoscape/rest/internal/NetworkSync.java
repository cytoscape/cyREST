package org.cytoscape.rest.internal;

import java.util.HashMap;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;

/**
 * Simple data-structure to house all necessary information on a network instance managed by CytoBridge.
 * @author Michael Kirby
 */
public class NetworkSync {
	
	private CyNetwork network;
	private CyNetworkView networkView;
	private HashMap<Integer, CyNode> nodeMap;
	private HashMap<Integer, CyEdge> edgeMap;
	
	public NetworkSync(CyNetwork net, CyNetworkView networkView, HashMap<Integer, CyNode> nodeMap, HashMap<Integer, CyEdge> edgeMap) {
		this.network = net;
		this.nodeMap = nodeMap;
		this.edgeMap = edgeMap;
		this.setNetworkView(networkView);
	}
	
	public CyNetwork getNetwork() {
		return network;
	}

	public void setEdgeMap(HashMap<Integer, CyEdge> edgeMap) {
		this.edgeMap = edgeMap;
	}

	public HashMap<Integer, CyEdge> getEdgeMap() {
		return edgeMap;
	}

	public void setNodeMap(HashMap<Integer, CyNode> nodeMap) {
		this.nodeMap = nodeMap;
	}

	public HashMap<Integer, CyNode> getNodeMap() {
		return nodeMap;
	}

	public void setNetworkView(CyNetworkView networkView) {
		this.networkView = networkView;
	}

	public CyNetworkView getNetworkView() {
		return networkView;
	}

}
