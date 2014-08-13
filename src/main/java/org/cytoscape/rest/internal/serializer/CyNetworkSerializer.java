package org.cytoscape.rest.internal.serializer;

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CyNetworkSerializer extends JsonSerializer<CyNetwork> { 

	private final String toMatrix(final CyNetwork network) {
		// Create header
		final SortedSet<Long> sortedNodeIds = new TreeSet<Long>();
		for (CyNode node : network.getNodeList()) {
			sortedNodeIds.add(node.getSUID());
		}

		final StringBuilder builder = new StringBuilder();

		builder.append(" ");
		for (Long suid : sortedNodeIds) {
			builder.append(suid + " ");
		}

		for (Long suid : sortedNodeIds) {
			final CyNode node = network.getNode(suid);
			for (Long inSUID : sortedNodeIds) {

			}
		}
		return null;
	}

	@Override
	public void serialize(CyNetwork network, JsonGenerator arg1, SerializerProvider arg2) throws IOException,
			JsonProcessingException {
		
	}
}
