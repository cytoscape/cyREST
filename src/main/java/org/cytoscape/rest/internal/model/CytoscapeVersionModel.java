package org.cytoscape.rest.internal.model;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Cytoscape and CyREST API Versions")
@XmlRootElement
public class CytoscapeVersionModel {
	private String apiVersion;
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public void setCytoscapeVersion(String cytoscapeVersion) {
		this.cytoscapeVersion = cytoscapeVersion;
	}

	private String cytoscapeVersion;

	public CytoscapeVersionModel()
	{
		
	}
	
	public CytoscapeVersionModel(final String apiVersion, final String cytoscapeVersion) {
		this.apiVersion = apiVersion;
		this.cytoscapeVersion = cytoscapeVersion;
	}

	/**
	 * @return the apiVersion
	 */
	@ApiModelProperty(value="CyREST API Version")
	public String getApiVersion() {
		return apiVersion;
	}

	/**
	 * @return the cytoscapeVersion
	 */
	@ApiModelProperty(value="Cytoscape Version")
	public String getCytoscapeVersion() {
		return cytoscapeVersion;
	}

}
