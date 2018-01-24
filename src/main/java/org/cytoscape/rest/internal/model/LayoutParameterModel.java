package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel()
public class LayoutParameterModel extends LayoutParameterValueModel {
	@ApiModelProperty(value="Long-form description of Parameter")
	public String description;
	@ApiModelProperty(value="The type of Parameter, represented as the simple name of its Java class")
	public String type;
}
