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
import org.cytoscape.model.CyNode;
import org.cytoscape.rest.Translator;


public class CyNode2JSONTranslator implements Translator<String, CyNode> {
	
	private final ObjectMapper jackson;
	
	public CyNode2JSONTranslator() {
		jackson = new ObjectMapper();
		SimpleModule testModule = new SimpleModule("MyModule", new Version(1, 0, 0, null));
		testModule.addSerializer(new CyNodeSerializer());
		jackson.registerModule(testModule);
	}
	
	
	/**
	 * Convert CyNetwork Object to JSON
	 */
	public String translate(final CyNode node) {
		try {
			return jackson.writeValueAsString(node);
		} catch (Exception e) {
			return "";
		}
	}
	
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

}
