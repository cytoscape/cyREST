package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Node Data")
public class Node {
	private NodeData data;

	/**
	 * @return the data
	 */
	@ApiModelProperty(value="Associated Data from the node table. " + ModelConstants.ROW_DESCRIPTION)
	public NodeData getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(NodeData data) {
		this.data = data;
	}
}
