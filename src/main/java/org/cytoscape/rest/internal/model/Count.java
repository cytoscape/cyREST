package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;

/**
 * 
 * @author David Otasek (dotasek.dev@gmail.com)
 *
 */
@ApiModel
public class Count 
{
	public Long count;
	
	public Count(Long count) {
		this.count=count;
	}
}
