package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;

/**
 * 
 * @author David Otasek (dotasek.dev@gmail.com)
 *
 */
@ApiModel
public class GroupSUID 
{
	public Long groupSUID;
	
	public GroupSUID(Long groupSUID) {
		this.groupSUID=groupSUID;
	}
}
