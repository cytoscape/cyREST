package org.cytoscape.rest.internal.model;

import java.net.URI;

public class CIError {

	public Integer status;
	public String type;
	public String message;
	public URI link;
	
	public CIError() {
		
	}
	
	public CIError(Integer status, String code, String message, URI link) {
		this.status = status;
		this.type = code;
		this.message = message;
		this.link = link;
	}

}