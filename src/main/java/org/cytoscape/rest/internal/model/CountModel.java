package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;

/**
 * 
 * @author David Otasek (dotasek.dev@gmail.com)
 *
 */
@ApiModel
public class CountModel 
{
	public Long count;
	
	public CountModel(Long count) {
		this.count=count;
	}
}
