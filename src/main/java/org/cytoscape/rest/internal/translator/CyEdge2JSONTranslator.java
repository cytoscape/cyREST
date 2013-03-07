package org.cytoscape.rest.internal.translator;

import org.cytoscape.model.CyEdge;
import org.cytoscape.rest.Translator;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CyEdge2JSONTranslator implements Translator<String, CyEdge> {

	private final ObjectMapper jackson;

	public CyEdge2JSONTranslator() {
		jackson = new ObjectMapper();
		jackson.registerModule(new CyJacksonModule());
	}

	/**
	 * Convert CyEdge Object to JSON
	 */
	public String translate(final CyEdge edge) {
		try {
			return jackson.writeValueAsString(edge);
		} catch (Exception e) {
			return "ERR";
		}
	}

}
