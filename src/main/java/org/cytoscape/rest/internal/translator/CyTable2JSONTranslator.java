package org.cytoscape.rest.internal.translator;

import org.codehaus.jackson.map.ObjectMapper;
import org.cytoscape.model.CyTable;
import org.cytoscape.rest.Translator;


public class CyTable2JSONTranslator implements Translator<String, CyTable> {
	
	private final ObjectMapper jackson;
	
	public CyTable2JSONTranslator() {
		jackson = new ObjectMapper();
		jackson.registerModule(new CyJacksonModule());
	}
	
	
	/**
	 * Convert CyTable Object to JSON
	 */
	public String translate(final CyTable table) {
		try {
			return jackson.writeValueAsString(table);
		} catch (Exception e) {
			return "";
		}
	}

}
