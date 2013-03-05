package org.cytoscape.rest.internal.translator;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.module.SimpleModule;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;

/**
 * Module for Jackson to hold the custom serializers for CyNetworks, CyNodes,
 * CyEdges, and CyTables.
 */
public class CyJacksonModule extends SimpleModule {

	/** Construct this CyJacksonModule and add all of the defined serializers. */
	public CyJacksonModule() {
		super("CyJacksonModule", new Version(1, 0, 0, null));
		addSerializer(new CyNodeSerializer());
		addSerializer(new CyEdgeSerializer());
		addSerializer(new CyTableSerializer());
		addSerializer(new CyNetworkSerializer());
	}

	/** Serializer for CyNetworks. */
	private class CyNetworkSerializer extends JsonSerializer<CyNetwork> {

		@Override
		public void serialize(CyNetwork network, JsonGenerator jgen, SerializerProvider provider) throws IOException,
				JsonProcessingException {
			jgen.writeStartObject();
			
			Map<String, Object> netAttrs = network.getRow(network).getAllValues();
			for (String key : netAttrs.keySet()) {
				jgen.writeObjectField(key, netAttrs.get(key));
			}
			jgen.writeObjectField("nodes", network.getNodeList());
			jgen.writeObjectField("edges", network.getEdgeList());
			
			jgen.writeEndObject();
		}

		public Class<CyNetwork> handledType() {
			return CyNetwork.class;
		}
	}

	/** Serializer for CyNodes. */
	private class CyNodeSerializer extends JsonSerializer<CyNode> {

		@Override
		public void serialize(CyNode node, JsonGenerator jgen, SerializerProvider provider) throws IOException,
				JsonProcessingException {
			jgen.writeStartObject();

			jgen.writeNumberField(CyIdentifiable.SUID, node.getSUID());
			if(node.getNetworkPointer() != null)
				jgen.writeNumberField("nestedNetwork", node.getNetworkPointer().getSUID());
			
			jgen.writeEndObject();
		}

		public Class<CyNode> handledType() {
			return CyNode.class;
		}
	}

	/** Serializer for CyEdges. */
	private class CyEdgeSerializer extends JsonSerializer<CyEdge> {

		@Override
		public void serialize(CyEdge edge, JsonGenerator jgen, SerializerProvider provider) throws IOException,
				JsonProcessingException {
			jgen.writeStartObject();

			jgen.writeNumberField("source", edge.getSource().getSUID());
			jgen.writeNumberField("target", edge.getTarget().getSUID());
			jgen.writeBooleanField("isDirected", edge.isDirected());

			jgen.writeEndObject();
		}

		@Override
		public Class<CyEdge> handledType() {
			return CyEdge.class;
		}
	}

	/** Serializer for CyTables. */
	private class CyTableSerializer extends JsonSerializer<CyTable> {

		@Override
		public void serialize(CyTable table, JsonGenerator jgen, SerializerProvider provider) throws IOException,
				JsonProcessingException {
			jgen.writeStartObject();
			jgen.writeEndObject();
		}

		public Class<CyTable> handledType() {
			return CyTable.class;
		}
	}
}
