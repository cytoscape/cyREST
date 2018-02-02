package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Created Edge")
public class CreatedCyEdgeModel {
	@ApiModelProperty(value="SUID of the new Edge", example="203")
	public long SUID;
	@ApiModelProperty(value="SUID of the Edge's Source Node", example="101")
	public long source;
	@ApiModelProperty(value="SUID of the Edge's Target Node", example="102")
	public long target;
}
