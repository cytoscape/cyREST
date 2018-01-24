package org.cytoscape.rest.internal.model;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
@ApiModel
public class VisualStyleDefaultsModel {
	@ApiModelProperty(value="A list of Visual Properties and their default values.")
	public List<VisualPropertyValueModel> defaults;
}
