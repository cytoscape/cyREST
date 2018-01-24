package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel
public class GroupSUIDModel 
{
	@ApiModelProperty(value="SUID of the Node representing the group.")
	public Long groupSUID;
	
	public GroupSUIDModel(Long groupSUID) {
		this.groupSUID=groupSUID;
	}
}
