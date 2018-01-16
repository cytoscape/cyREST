package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Visual Property Value")
public class VisualPropertyValueModel {
	@ApiModelProperty(value="Name of the Visual Property", example="NODE_SHAPE")
	public String visualProperty;
	@ApiModelProperty(value="Value of the Visual Property", example="ELLIPSE")
	public Object value;
}
