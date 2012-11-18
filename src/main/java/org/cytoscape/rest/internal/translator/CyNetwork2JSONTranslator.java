package org.cytoscape.rest.internal.translator;

import org.codehaus.jackson.map.ObjectMapper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.rest.Translator;

public class CyNetwork2JSONTranslator implements Translator<String, CyNetwork> {

	private final ObjectMapper jackson;

	public CyNetwork2JSONTranslator() {
		jackson = new ObjectMapper();
		jackson.registerModule(new CyJacksonModule());
	}
	
	
	/**
	 * Convert CyNetwork Object to JSON
	 */
	public String translate(final CyNetwork network) {
		try {
			return jackson.writeValueAsString(network);
		} catch (Exception e) {
			return "";
		}
	}
	
}
