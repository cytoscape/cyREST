package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Session File name")
public class SessionNameModel 
{
	@ApiModelProperty(value="Full file name for the Session")
	public String name;
	
	public SessionNameModel(String name)
	{
		this.name = name;
	}
}
