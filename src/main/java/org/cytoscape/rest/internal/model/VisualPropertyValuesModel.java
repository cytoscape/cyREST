package org.cytoscape.rest.internal.model;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class VisualPropertyValuesModel {
	@ApiModelProperty(value="Unique internal name of the Visual Property.", example="NODE_SHAPE")
	public String visualProperty;
	@ApiModelProperty(value="Values available for the Visual Property")
	public List<Object> values;
}
