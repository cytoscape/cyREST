package org.cytoscape.rest.internal.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CytoscapeVersion {
	private final String apiVersion;
	private final String cytoscapeVersion;

	public CytoscapeVersion(final String apiVersion, final String cytoscapeVersion) {
		this.apiVersion = apiVersion;
		this.cytoscapeVersion = cytoscapeVersion;
	}

	/**
	 * @return the apiVersion
	 */
	public String getApiVersion() {
		return apiVersion;
	}

	/**
	 * @return the cytoscapeVersion
	 */
	public String getCytoscapeVersion() {
		return cytoscapeVersion;
	}

}
