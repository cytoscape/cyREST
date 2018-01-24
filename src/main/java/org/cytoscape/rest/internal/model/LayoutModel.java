package org.cytoscape.rest.internal.model;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel()
public class LayoutModel {
	@ApiModelProperty(value="Unique internal name of the Layout")
	public String name;
	@ApiModelProperty(value="Human-readable name of the Layout. This can be seen primarily in the Cytoscape GUI.")
	public String longName;
	@ApiModelProperty(value="Parameters for this layout and their values.")
	public List<LayoutParameterModel> parameters;
	@ApiModelProperty(value="Column Types that can be used by this layout.")
	public List<LayoutColumnTypesModel> compatibleColumnDataTypes;
	
}
