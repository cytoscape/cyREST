package org.cytoscape.rest.internal.translator;

public enum CyJsonToken {
	NETWORK("network"), NODE("node"), EDGE("edge"), NODES("nodes"), EDGES("edges"),
	STRING("string"), 
	// For cytoscape.js
	ELEMENTS("elements"), DATA("data"), ID("id"), SOURCE("source"), TARGET("target");
	
	private final String name;
	
	private CyJsonToken(final String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
