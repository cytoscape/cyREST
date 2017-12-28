package org.cytoscape.rest.internal.model;

import java.util.Collection;

import io.swagger.annotations.ApiModel;

@ApiModel(value="Cytoscape Column with Rows", description="A definition of a column from a Cytoscape table, and a list of its rows.")
public class CyColumnWithRowsModel extends CyColumnModel{
	Collection<CyRowModel> rows;
}
