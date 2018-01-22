package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel()
public class CytoPanelModel {
	@ApiModelProperty(value="Name of the Panel", allowableValues="SOUTH,EAST,WEST,SOUTH_WEST")
	public String name;
	@ApiModelProperty(value="State of the Panel", allowableValues="FLOAT,DOCK,HIDE")
	public String state;
}
