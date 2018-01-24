package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Node Data")
public class NodeModel {
	private NodeDataModel data;

	/**
	 * @return the data
	 */
	@ApiModelProperty(value="Associated Data from the Node Table. " + ModelConstants.ROW_DESCRIPTION)
	public NodeDataModel getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(NodeDataModel data) {
		this.data = data;
	}
}
