package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Visual Property Value")
public class VisualPropertyValueModel {
	@ApiModelProperty(value="Name of the Visual Property", example="VISUAL_PROPERTY_NAME")
	public String visualProperty;
	@ApiModelProperty(value="Serialized value of the Visual Property, or null.")
	public Object value;
}
