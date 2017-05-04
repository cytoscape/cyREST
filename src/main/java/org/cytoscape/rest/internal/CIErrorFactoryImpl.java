package org.cytoscape.rest.internal;

import java.io.File;
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
		System.out.println("CIError source thread:" + Thread.currentThread().getName());
		CIError error = new CIError();
		error.status = status;
		error.type = type;
		error.message = message;
		
		URI link = logLocation;
		error.link = link;
		return error;
	}
}
