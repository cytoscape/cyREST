package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel()
public class CyPropertyModel {
	@ApiModelProperty(value="Value of the CyProperty")
	public String value;
	
	@ApiModelProperty(value="Key of the CyProperty")
	public String key;
	
}
