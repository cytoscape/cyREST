package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author David Otasek (dotasek.dev@gmail.com)
 *
 */
@ApiModel
public class NetworkViewSUIDModel 
{
	@ApiModelProperty(value="SUID of the Network View")
	public Long networkViewSUID;
	
	public NetworkViewSUIDModel(Long networkViewSUID) {
		this.networkViewSUID=networkViewSUID;
	}
}
