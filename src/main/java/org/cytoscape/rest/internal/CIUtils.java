package org.cytoscape.rest.internal;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.cytoscape.rest.internal.model.CIError;
import org.cytoscape.rest.internal.model.CIResponse;
import org.slf4j.Logger;

/**
 * This is a temporary class, intended to provide CIError for Cytoscape 3.5 API. When Cytoscape 3.6 is released, this 
 * class should be replaced by the org.cytoscape.ci bundle equivalent.
 * 
 * @author David Otasek (dotasek.dev@gmail.com)
 *
 */
public class CIUtils {
	public static final CIResponse<Object> getCIErrorResponse(Logger logger, String logLocation, int status, String resourcePath, String code, String message, Exception e)
	{
		CIResponse<Object> ciResponse = new CIResponse<Object>();
		ciResponse.data = new Object();
		List<CIError> errors = new ArrayList<CIError>();
		CIError error= new CIError();
		error.type = CyRESTConstants.cyRESTCIRoot + ":" + resourcePath + CyRESTConstants.cyRESTCIErrorRoot + ":"+ code;
	
		if (e != null)
		{
			logger.error(message, e);
		}
		else
		{
			logger.error(message);
		}
		URI link = (new File(logLocation)).toURI();
		error.link = link;
		error.status = status;
		error.message = message;
		errors.add(error);
		ciResponse.errors = errors;
		return ciResponse;
	}
}
