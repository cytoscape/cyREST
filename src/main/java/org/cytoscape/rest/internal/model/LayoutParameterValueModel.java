package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel()
public class LayoutParameterValueModel {
	@ApiModelProperty(value="The name of the Parameter")
	public String name;
	@ApiModelProperty(value="The value of this Parameter as a JSON primitive")
	public Object value;
}
