package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class NodeNameSUIDModel {
	@ApiModelProperty(value="Name of the Node")
	public String name;
	@ApiModelProperty(value="SUID of the Node")
	public Long SUID;
}
