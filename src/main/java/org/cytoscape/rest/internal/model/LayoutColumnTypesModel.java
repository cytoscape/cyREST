package org.cytoscape.rest.internal.model;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class LayoutColumnTypesModel {
	
	@ApiModelProperty(value="Types of Compatible Node Columns")
	public List<ModelConstants.ColumnTypeAll> compatibleNodeColumnDataTypes;
	@ApiModelProperty(value="Types of Compatible Edge Columns")
	public List<ModelConstants.ColumnTypeAll> compatibleEdgeColumnDataTypes;
}
