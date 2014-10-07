package org.cytoscape.rest.internal.serializer;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.cytoscape.group.CyGroup;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNode;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class GroupSerializer extends JsonSerializer<CyGroup> {

	@Override
	public Class<CyGroup> handledType() {
		return CyGroup.class;
	}

	@Override
	public void serialize(final CyGroup group, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {

		jgen.useDefaultPrettyPrinter();
		
		jgen.writeStartObject();

		final CyNode groupNode = group.getGroupNode();
		final List<CyNode> memberNodes = group.getNodeList();
		final List<CyEdge> internalEdges = group.getInternalEdgeList();
		final Set<CyEdge> externalEdges = group.getExternalEdgeList();
		jgen.writeNumberField(CyIdentifiable.SUID, groupNode.getSUID());

		// TODO: Bug?  Always false
//		jgen.writeBooleanField("collapsed", group.isCollapsed(group.getRootNetwork()));
		
		jgen.writeArrayFieldStart("nodes");
		for (final CyNode node : memberNodes) {
			jgen.writeNumber(node.getSUID());
		}
		jgen.writeEndArray();

		jgen.writeArrayFieldStart("internal_edges");
		for (final CyEdge edge : internalEdges) {
			jgen.writeNumber(edge.getSUID());
		}
		jgen.writeEndArray();
		
		jgen.writeArrayFieldStart("external_edges");
		for (final CyEdge edge : externalEdges) {
			jgen.writeNumber(edge.getSUID());
		}
		jgen.writeEndArray();

		jgen.writeEndObject();
	}
}
