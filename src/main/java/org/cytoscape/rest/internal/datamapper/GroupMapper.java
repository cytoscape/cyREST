package org.cytoscape.rest.internal.datamapper;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.cytoscape.group.CyGroup;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CyRootNetwork;

import com.fasterxml.jackson.databind.JsonNode;

public class GroupMapper {

	public CyGroup createGroup(final JsonNode rootNode, final CyGroupFactory factory, final CyNetwork network) {
		
		// Extract required fields
		final String groupName = rootNode.get(CyNetwork.NAME).textValue();
		final JsonNode memberNodes = rootNode.get("nodes");
		final JsonNode memberEdges = rootNode.get("edges");
		if(groupName == null || groupName.isEmpty()) {
			throw new WebApplicationException("Group name is missing.");
		}
		
		if(memberEdges.isArray() == false || memberNodes.isArray() == false) {
			throw new WebApplicationException("Invalid parameter.");
		}
		
		if(memberNodes.size() == 0) {
			throw new WebApplicationException("Empty node list.");
		}
		
		final CyGroup group = factory.createGroup(network, true);
		final CyNode groupNode = group.getGroupNode();
		group.getRootNetwork().getDefaultNodeTable().getRow(groupNode.getSUID()).set(CyRootNetwork.SHARED_NAME, groupName);

		final List<CyNode> nodes = new ArrayList<CyNode>();
		for (final JsonNode node : memberNodes) {
			final CyNode n = network.getNode(node.asLong());
			if (n != null) {
				nodes.add(n);
			}
		}
		group.addNodes(nodes);

		final List<CyEdge> edges = new ArrayList<CyEdge>();
		for (final JsonNode edge : memberEdges) {
			final CyEdge e = network.getEdge(edge.asLong());
			if (e != null) {
				edges.add(e);
			}
		}
		group.addEdges(edges);

		return group;
	}
}
