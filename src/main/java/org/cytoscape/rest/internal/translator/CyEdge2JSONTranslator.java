package org.cytoscape.rest.internal.translator;

import org.cytoscape.model.CyEdge;
import org.cytoscape.rest.Translator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CyEdge2JSONTranslator implements Translator<String, CyEdge> {

	private static final Logger logger = LoggerFactory.getLogger(CyEdge2JSONTranslator.class);

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
			logger.error("Could not translate object: " + edge, e);
			return "ERR";
		}
	}

}
