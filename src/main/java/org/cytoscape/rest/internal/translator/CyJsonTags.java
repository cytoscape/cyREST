package org.cytoscape.rest.internal.translator;

public enum CyJsonTags {
	NETWORK("network"), NODE("node"), EDGE("edge"), NODES("nodes"), EDGES("edges");
	
	private final String name;
	
	private CyJsonTags(final String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
