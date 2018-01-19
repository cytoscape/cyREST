package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;

/**
 * 
 * Session file data for changes to Session file location or save status.
 * 
 * @author David Otasek (dotasek.dev@gmail.com)
 *
 */
@ApiModel
public class FileModel 
{
	public String file;
	
	public FileModel(String file)
	{
		this.file = file;
	}
}
