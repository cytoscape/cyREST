package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class CyPropertyValueModel {
	
	@ApiModelProperty(value="Value of the CyProperty")
	public String value;
	
}
