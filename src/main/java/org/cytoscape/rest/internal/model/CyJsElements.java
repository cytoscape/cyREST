package org.cytoscape.rest.internal.model;

import java.util.Collection;

public class CyJsElements {
	private Collection<NodeModel> nodes;
	private Collection<EdgeModel> edges;
	/**
	 * @return the nodes
	 */
	public Collection<NodeModel> getNodes() {
		return nodes;
	}
	/**
	 * @param nodes the nodes to set
	 */
	public void setNodes(Collection<NodeModel> nodes) {
		this.nodes = nodes;
	}
	/**
	 * @return the edges
	 */
	public Collection<EdgeModel> getEdges() {
		return edges;
	}
	/**
	 * @param edges the edges to set
	 */
	public void setEdges(Collection<EdgeModel> edges) {
		this.edges = edges;
	}
}
