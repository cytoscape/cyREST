package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Session file name", description="")
public class SessionName 
{
	@ApiModelProperty()
	public String name;
	
	public SessionName(String name)
	{
		this.name = name;
	}
}
