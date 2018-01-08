package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Created Edge")
public class CreatedCyEdgeModel {
	@ApiModelProperty
	public long SUID;
	@ApiModelProperty
	public long source;
	@ApiModelProperty
	public long target;
}
