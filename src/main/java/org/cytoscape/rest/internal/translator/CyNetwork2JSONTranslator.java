package org.cytoscape.rest.internal.translator;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.rest.Translator;

import com.google.gson.Gson;

public class CyNetwork2JSONTranslator implements Translator<String, CyNetwork> {

	
	private final Gson gson;
	
	public CyNetwork2JSONTranslator() {
		this.gson = new Gson();
	}
	
	
	/**
	 * Convert CyNetwork Object to JSON
	 */
	@Override
	public String translate(final CyNetwork network) {
		final String jsonNetwork = gson.toJson(network);
		
		System.out.println(jsonNetwork);
		
		return jsonNetwork;
	}

}
