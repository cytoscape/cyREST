package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author David Otasek (dotasek.dev@gmail.com)
 *
 */
@ApiModel
public class CountModel 
{
	@ApiModelProperty(value="Count value.", example="1")
	public Long count;
	
	public CountModel(Long count) {
		this.count=count;
	}
}
