package org.cytoscape.rest.internal.model;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Cytoscape Column Values")
public class CyColumnValuesModel {
	@ApiModelProperty(value = "Column Name", required=true, example="Weight")
	public String name;
	@ApiModelProperty(value = "Column Values. These are formatted as JSON primitives.", required=true, example="9")
	public List<?> values;
}
