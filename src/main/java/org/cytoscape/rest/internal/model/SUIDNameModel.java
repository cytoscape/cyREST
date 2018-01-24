package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class SUIDNameModel {
	@ApiModelProperty(value="SUID of the Object")
	public long SUID;
	@ApiModelProperty(value="Name of the Object. This is the content of the Objects `name` column in its default table.")
	public String name;
}
