package org.cytoscape.rest.internal.model;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class VisualStyleModel {
	@ApiModelProperty(value="Name of the Visual Style", example="default")
	public String title;
	@ApiModelProperty(value="List of Visual Properties and their default values")
	public List<VisualPropertyValueModel> defaults;
	@ApiModelProperty(value="List of Mappings")
	public List<VisualStyleMappingModel> mappings;
}
