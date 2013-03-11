package org.cytoscape.rest.internal.translator;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.rest.Translator;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CyNetwork2CytoscapeJSTranslator implements Translator<String, CyNetwork> {

	private final ObjectMapper jackson;

	public CyNetwork2CytoscapeJSTranslator() {
		jackson = new ObjectMapper();
		final CytoscapeJSModule module = new CytoscapeJSModule();
		jackson.registerModule(module);
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