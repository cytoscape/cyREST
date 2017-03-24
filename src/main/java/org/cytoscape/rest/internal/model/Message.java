package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;

/**
 * Message model for returning messages from REST calls.
 * 
 * @author David Otasek (dotasek.dev@gmail.com)
 *
 */
@ApiModel
public class Message 
{
	private String message;
	
	public Message()
	{
		
	}
	
	public Message(String string) {
		setMessage(string);
	}

	/**
	 * 
	 * @return the message
	 */
	public String getMessage()
	{
		return message;
	}
	
	/**
	 * 
	 * @param message
	 * 		the message to set.
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}
}
