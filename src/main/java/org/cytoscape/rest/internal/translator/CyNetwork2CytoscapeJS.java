package org.cytoscape.rest.internal.translator;

import java.io.IOException;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CyNetwork2CytoscapeJS extends JsonSerializer<CyNetwork> {

	@Override
	public void serialize(CyNetwork network, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {

		jgen.useDefaultPrettyPrinter();

		jgen.writeStartObject();
		jgen.writeObjectFieldStart("elements");

		System.out.println("ELEMENT OK");

		// Write array
		final List<CyNode> nodes = network.getNodeList();
		final List<CyEdge> edges = network.getEdgeList();

		jgen.writeArrayFieldStart(CyJsonToken.NODES.getName());
		for (final CyNode node : nodes) {
			jgen.writeStartObject();

			jgen.writeObjectFieldStart("data");
			jgen.writeStringField("id", node.getSUID().toString());
			jgen.writeEndObject();

			jgen.writeEndObject();
		}
		jgen.writeEndArray();

		jgen.writeArrayFieldStart(CyJsonToken.EDGES.getName());
		for (final CyEdge edge : edges) {
			jgen.writeStartObject();

			jgen.writeObjectFieldStart("data");
			jgen.writeStringField("id", edge.getSUID().toString());
			jgen.writeStringField("source", edge.getSource().getSUID().toString());
			jgen.writeStringField("target", edge.getTarget().getSUID().toString());
			jgen.writeEndObject();

			jgen.writeEndObject();

		}
		jgen.writeEndArray();

		jgen.writeEndObject();
	}

	@Override
	public Class<CyNetwork> handledType() {
		return CyNetwork.class;
	}

}
