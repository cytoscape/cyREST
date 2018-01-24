package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Created Edge")
public class CreatedCyEdgeModel {
	@ApiModelProperty(value="SUID of the new Edge")
	public long SUID;
	@ApiModelProperty(value="SUID of the Edge's Source Node")
	public long source;
	@ApiModelProperty(value="SUID of the Edge's Target Node")
	public long target;
}
