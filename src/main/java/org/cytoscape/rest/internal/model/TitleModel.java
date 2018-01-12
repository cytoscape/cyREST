package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Title Model", description="The new name", subTypes= {CyTableWithRowsModel.class})
public class TitleModel {
	
	@ApiModelProperty(value = "Title", required=true)
	public String title;
}