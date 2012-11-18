package org.cytoscape.rest.internal.translator;

import org.codehaus.jackson.map.ObjectMapper;
import org.cytoscape.model.CyNode;
import org.cytoscape.rest.Translator;


public class CyNode2JSONTranslator implements Translator<String, CyNode> {
	
	private final ObjectMapper jackson;
	
	public CyNode2JSONTranslator() {
		jackson = new ObjectMapper();
		jackson.registerModule(new CyJacksonModule());
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

}
