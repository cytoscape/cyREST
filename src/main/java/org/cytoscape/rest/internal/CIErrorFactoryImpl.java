package org.cytoscape.rest.internal;

import java.net.URI;

import org.cytoscape.ci.CIErrorFactory;
import org.cytoscape.ci.model.CIError;

public class CIErrorFactoryImpl implements CIErrorFactory {

	private URI logLocation;
	
	public CIErrorFactoryImpl(URI logLocation) {
		this.logLocation = logLocation;
	}
	
	@Override
	public CIError getCIError(Integer status, String type, String message, URI link) {
		CIError error = new CIError();
		error.status = status;
		error.type = type;
		error.message = message;
		error.link = link;
		return error;
	}

	@Override
	public CIError getCIError(Integer status, String type, String message) {
		// Note: for efficient lookup in the log files, JAX-RS resources are all executed in their own threads. 
		// The thread name, accessed in the commented code below, could be used to sift through log entries.
		// System.out.println("CIError source thread:" + Thread.currentThread().getName());
		CIError error = new CIError();
		error.status = status;
		error.type = type;
		error.message = message;
		
		URI link = logLocation;
		error.link = link;
		return error;
	}
}
