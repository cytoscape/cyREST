package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class NetworkViewVisualPropertyModel {
	
	@ApiModelProperty(value = "Visual Property Name", example="NODE_FILL_COLOR", required=true)
	public String visualProperty;
	
	@ApiModelProperty(value = "Serialized form of value, or null", example="red", required=true)
	public Object value;
}
