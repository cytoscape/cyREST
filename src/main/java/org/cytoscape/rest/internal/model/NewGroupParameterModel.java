package org.cytoscape.rest.internal.model;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class NewGroupParameterModel {
	@ApiModelProperty(value="Name of the Group.")
	public String name;
	@ApiModelProperty(value="Nodes contained in the Group, represented as SUIDs")
	public List<Long> nodes;
}
