package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Message model for returning messages from REST calls.
 * 
 * @author David Otasek (dotasek.dev@gmail.com)
 *
 */
@ApiModel
public class MessageModel 
{
	@ApiModelProperty(value="Message text")
	public String message;
	
	public MessageModel(String string) {
		message = string;
	}
}
