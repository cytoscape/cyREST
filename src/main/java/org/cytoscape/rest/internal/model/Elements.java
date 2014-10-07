package org.cytoscape.rest.internal.model;

import java.util.Collection;

public class Elements {
	private Collection<Node> nodes;
	private Collection<Edge> edges;
	/**
	 * @return the nodes
	 */
	public Collection<Node> getNodes() {
		return nodes;
	}
	/**
	 * @param nodes the nodes to set
	 */
	public void setNodes(Collection<Node> nodes) {
		this.nodes = nodes;
	}
	/**
	 * @return the edges
	 */
	public Collection<Edge> getEdges() {
		return edges;
	}
	/**
	 * @param edges the edges to set
	 */
	public void setEdges(Collection<Edge> edges) {
		this.edges = edges;
	}
}
