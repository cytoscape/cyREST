package org.cytoscape.rest.internal.serializer;

public enum JSONTags {
	TABLE_TITLE("title");

	private final String tag;

	private JSONTags(final String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return this.tag;
	}
}
