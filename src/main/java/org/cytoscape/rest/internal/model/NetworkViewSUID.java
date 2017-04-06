package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;

/**
 * 
 * @author David Otasek (dotasek.dev@gmail.com)
 *
 */
@ApiModel
public class NetworkViewSUID 
{
	public Long networkViewSUID;
	
	public NetworkViewSUID(Long networkViewSUID) {
		this.networkViewSUID=networkViewSUID;
	}
}
