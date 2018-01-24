package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Cytoscape's GUI Panel Element")
public class CytoPanelModel {
	@ApiModelProperty(value="Name of the Panel", allowableValues="SOUTH,EAST,WEST,SOUTH_WEST", required=true)
	public String name;
	@ApiModelProperty(value="State of the Panel", allowableValues="FLOAT,DOCK,HIDE", required=true)
	public String state;
}
