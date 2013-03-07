package org.cytoscape.rest.internal.translator;

public enum CyJsonToken {
	NETWORK("network"), NODE("node"), EDGE("edge"), NODES("nodes"), EDGES("edges"),
	STRING("string");
	
	private final String name;
	
	private CyJsonToken(final String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
