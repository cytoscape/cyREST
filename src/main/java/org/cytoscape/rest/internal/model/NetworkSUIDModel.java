package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class NetworkSUIDModel 
{
	@ApiModelProperty(value="SUID of the Network")
	public Long networkSUID;
	
	public NetworkSUIDModel(Long networkSUID) {
		this.networkSUID=networkSUID;
	}
}
