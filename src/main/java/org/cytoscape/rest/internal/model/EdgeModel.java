package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Edge Data")
public class EdgeModel {
	private EdgeDataModel data;

	/**
	 * @return the data
	 */
	@ApiModelProperty(value="Associated Data from the edge table. " + ModelConstants.ROW_DESCRIPTION)
	public EdgeDataModel getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(EdgeDataModel data) {
		this.data = data;
	}
}
