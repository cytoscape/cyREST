package org.cytoscape.rest.internal.datamapper;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.group.CyGroup;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;

import com.fasterxml.jackson.databind.JsonNode;

public class GroupMapper {

	
	/**
	 * Create a new group from a list of nodes.
	 * 
	 * @param rootNode
	 * @param factory
	 * @param network
	 * @return
	 */
	public CyGroup createGroup(final JsonNode rootNode, final CyGroupFactory factory, final CyNetwork network) {
		
		// Extract required fields: member nodes and name of new node.
		final String groupName = rootNode.get(CyNetwork.NAME).textValue();
		final JsonNode memberNodes = rootNode.get("nodes");
		
		// This is optional.
		final JsonNode memberEdges = rootNode.get("edges");
		
		if(groupName == null || groupName.isEmpty()) {
			throw new IllegalArgumentException("Group name is missing.");
		}
		
		if(memberNodes.isArray() == false) {
			throw new IllegalArgumentException("Invalid parameter.");
		}
		
		if(memberNodes.size() == 0) {
			throw new IllegalArgumentException("Group member list is empty.");
		}
		
		// Phase 1: Create list of member nodes.
		final List<CyNode> nodes = new ArrayList<CyNode>();
		for (final JsonNode node : memberNodes) {
			final CyNode n = network.getNode(node.asLong());
			if (n != null) {
				nodes.add(n);
			}
		}
	
		// Phase 2: Create group from the list of nodes.
		final CyGroup group = factory.createGroup(network, nodes, null, true);
		final CyRow groupRow = ((CySubNetwork)network).getRootNetwork().getRow(group.getGroupNode(), CyRootNetwork.SHARED_ATTRS);
		groupRow.set(CyRootNetwork.SHARED_NAME, groupName);

		// Add edges if necessary...
		if (memberEdges != null && memberEdges.isArray()) {
			final List<CyEdge> edges = new ArrayList<CyEdge>();
			for (final JsonNode edge : memberEdges) {
				final CyEdge e = network.getEdge(edge.asLong());
				if (e != null) {
					edges.add(e);
				}
			}
			group.addEdges(edges);
		}

		return group;
	}
}
