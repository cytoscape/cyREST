package org.cytoscape.rest.internal.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Response from CI standard services
 * 
 */
public class CIResponse<T> {

	public T data;
	public List<CIError> errors; 
	
	public CIResponse() {
		errors = new ArrayList<CIError>();
	}
	
	public CIResponse(T data) {
		this.data = data;
		errors = new ArrayList<CIError>();
	}
}
