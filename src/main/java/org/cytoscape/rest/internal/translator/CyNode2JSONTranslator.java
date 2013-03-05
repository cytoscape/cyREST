package org.cytoscape.rest.internal.translator;

import org.codehaus.jackson.map.ObjectMapper;
import org.cytoscape.model.CyNode;
import org.cytoscape.rest.Translator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CyNode2JSONTranslator implements Translator<String, CyNode> {

	private static final Logger logger = LoggerFactory.getLogger(CyNode2JSONTranslator.class);

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
			logger.error("Could not translate object: " + node, e);
			return "";
		}
	}

}
