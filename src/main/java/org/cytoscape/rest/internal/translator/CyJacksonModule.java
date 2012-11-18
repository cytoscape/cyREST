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
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

/** Module for Jackson to hold the custom serializers for CyNetworks, CyNodes, CyEdges, and CyTables.
 */
public class CyJacksonModule extends SimpleModule {

	/** Construct this CyJacksonModule and add all of the defined serializers. */
	public CyJacksonModule() {
		super("CyJacksonModule", new Version(1, 0, 0, null));
		addSerializer(new CyNetworkSerializer());
		addSerializer(new CyNodeSerializer());
		addSerializer(new CyEdgeSerializer());
	}
	
	/** Serializer for CyNetworks. */
	private class CyNetworkSerializer extends JsonSerializer<CyNetwork> {

		@Override
		public void serialize(CyNetwork network, JsonGenerator jgen,
		        SerializerProvider provider) throws IOException,
		        JsonProcessingException {
		    jgen.writeStartObject();
		    jgen.writeNumberField("SUID", network.getSUID());
		    jgen.writeEndObject();
		}
		
		public Class<CyNetwork> handledType() {
			return CyNetwork.class;
		}
	}
	
	/** Serializer for CyNodes. */
	private class CyNodeSerializer extends JsonSerializer<CyNode> {

		@Override
		public void serialize(CyNode node, JsonGenerator jgen,
		        SerializerProvider provider) throws IOException,
		        JsonProcessingException {
			jgen.writeStartObject();
			
		    Map<String, Object> nodeAttrs = node.getNetworkPointer().getRow(node).getAllValues();
		    for (String key : nodeAttrs.keySet()) {
		    	jgen.writeObjectField(key,nodeAttrs.get(key));
		    }
		    jgen.writeEndObject();
		}
		
		public Class<CyNode> handledType() {
			return CyNode.class;
		}
	}
	
	/** Serializer for CyEdges. */
	private class CyEdgeSerializer extends JsonSerializer<CyEdge> {

		@Override
		public void serialize(CyEdge edge, JsonGenerator jgen,
		        SerializerProvider provider) throws IOException,
		        JsonProcessingException {
			jgen.writeStartObject();
			jgen.writeNumberField("source", edge.getSource().getSUID());
			jgen.writeNumberField("target", edge.getTarget().getSUID());
			jgen.writeBooleanField("isDirected", edge.isDirected());
		    Map<String, Object> edgeAttrs = edge.getSource().getNetworkPointer().getRow(edge).getAllValues();
		    for (String key : edgeAttrs.keySet()) {
		    	jgen.writeObjectField(key,edgeAttrs.get(key));
		    }
		    jgen.writeEndObject();
		}
		
		public Class<CyEdge> handledType() {
			return CyEdge.class;
		}
	}
}
