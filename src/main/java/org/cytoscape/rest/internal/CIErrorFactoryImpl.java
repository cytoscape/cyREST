package org.cytoscape.rest.internal;

import java.net.URI;

import org.cytoscape.ci.CIErrorFactory;
import org.cytoscape.ci.model.CIError;

public class CIErrorFactoryImpl implements CIErrorFactory {

	@Override
	public CIError getCIError(Integer status, String type, String message, URI link) {
		CIError error = new CIError();
		error.status = status;
		error.type = type;
		error.message = message;
		error.link = link;
		return error;
	}
}
