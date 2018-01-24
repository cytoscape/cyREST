package org.cytoscape.rest.internal.model;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Cytoscape Table with Rows", description="A definition of a column from a Cytoscape table, and a list of its rows.", parent=CyTableModel.class)
public class CyTableWithRowsModel extends CyTableModel{
	@ApiModelProperty(value = "Rows in this Table", required=true)
	public List<CyRowModel> rows;
}
