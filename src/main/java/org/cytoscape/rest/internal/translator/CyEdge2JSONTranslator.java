package org.cytoscape.rest.internal.translator;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.module.SimpleModule;
import org.cytoscape.model.CyEdge;
import org.cytoscape.rest.Translator;


public class CyEdge2JSONTranslator implements Translator<String, CyEdge> {
	
	private final ObjectMapper jackson;
	
	public CyEdge2JSONTranslator() {
		jackson = new ObjectMapper();
		SimpleModule testModule = new SimpleModule("MyModule", new Version(1, 0, 0, null));
		testModule.addSerializer(new CyEdgeSerializer());
		jackson.registerModule(testModule);
	}
	
	
	/**
	 * Convert CyEdge Object to JSON
	 */
	public String translate(final CyEdge edge) {
		try {
			return jackson.writeValueAsString(edge);
		} catch (Exception e) {
			return "";
		}
	}
	
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
