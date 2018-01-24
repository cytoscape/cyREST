package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel
public class FileModel 
{
	@ApiModelProperty(value="Full name of the file.", required=true)
	public String file;
	
	public FileModel(String file)
	{
		this.file = file;
	}
}
