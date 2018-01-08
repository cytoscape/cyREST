package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Node Data", description=ModelConstants.ROW_DESCRIPTION)
public class NodeData {

	private String id;

	/**
	 * @return the id
	 */
	@ApiModelProperty
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
}
