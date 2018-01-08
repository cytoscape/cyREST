package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class SUIDNameModel {
	@ApiModelProperty
	public long SUID;
	@ApiModelProperty
	public String name;
}
