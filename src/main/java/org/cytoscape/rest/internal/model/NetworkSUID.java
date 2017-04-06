package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;

/**
 * 
 * @author David Otasek (dotasek.dev@gmail.com)
 *
 */
@ApiModel
public class NetworkSUID 
{
	public Long networkSUID;
	
	public NetworkSUID(Long networkSUID) {
		this.networkSUID=networkSUID;
	}
}
