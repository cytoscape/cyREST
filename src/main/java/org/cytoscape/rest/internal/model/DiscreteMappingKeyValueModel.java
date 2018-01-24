package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel()
public class DiscreteMappingKeyValueModel {
	@ApiModelProperty(value="CyTable cell value to match")
	public String key;
	@ApiModelProperty(value="VisualProperty returned when the CyTable cell value is matched.")
	public String value;
}
