package org.cytoscape.rest.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.cytoscape.ci.CIExceptionFactory;
import org.cytoscape.ci.model.CIError;
import org.cytoscape.ci.model.CIResponse;

public class CIExceptionFactoryImpl implements CIExceptionFactory{

	@Override
	public WebApplicationException getCIException(int httpStatus, CIError[] errors) {
		return getCIException(httpStatus, new Object(), errors);
	}

	@Override
	public <K> WebApplicationException getCIException(int httpStatus, K data, CIError[] errors) {
		CIResponse<K> ciResponse = new CIResponse<K>();
	
		ciResponse.data = data;
		ciResponse.errors = new ArrayList<CIError>();
		Collections.addAll(ciResponse.errors, Arrays.stream(errors).toArray(CIError[]::new));
		Response response = Response.status(httpStatus).encoding("application/json").entity(ciResponse).build();
		WebApplicationException webApplicationException = new WebApplicationException(response);
		return webApplicationException;
	}

}
