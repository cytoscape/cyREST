package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="New Edge Parameter")
public class CreateCyEdgeParameterModel {
	@ApiModelProperty(required=true, value="The SUID of the source node for the new edge")
	public long source;
	@ApiModelProperty(required=true, value="The SUID of the target node for the new edge")
	public long target;
	@ApiModelProperty(required=false, value="Sets the `directed` property of the edge.")
	public boolean directed;
	@ApiModelProperty(required=false, value="The value to include in the new edge's `interaction` column.")
	public String interaction;
}
