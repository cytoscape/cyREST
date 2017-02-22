package org.cytoscape.rest.internal.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CytoscapeVersion {
	private String apiVersion;
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public void setCytoscapeVersion(String cytoscapeVersion) {
		this.cytoscapeVersion = cytoscapeVersion;
	}

	private String cytoscapeVersion;

	public CytoscapeVersion()
	{
		
	}
	
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
