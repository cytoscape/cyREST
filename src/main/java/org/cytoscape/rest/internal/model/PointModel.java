package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="A single point in the Continuous Mapping Graph")
public class PointModel {
	
	@ApiModelProperty(value="Value of the Column Cell (x coordinate in the Cytoscape GUI)")
	public Double value;
	
	//The following three values are copied directly from the BoundaryRangeValues API. Not exactly helpful, but the best I can do.
	@ApiModelProperty(value="Will be used for interpolation upon smaller domain values")
	public String lesser;
	
	@ApiModelProperty(value="Will be used when the domain value is exactly equal to the associated boundary domain value")
	public String equal;
	
	@ApiModelProperty(value="Will be used for interpolation upon larger domain values")
	public String greater;
}
