package org.cytoscape.rest.internal.resource;

import java.net.URI;
import java.util.ArrayList;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.cytoscape.ci.model.CIError;
import org.cytoscape.ci.model.CIResponse;
import org.cytoscape.rest.internal.CyRESTConstants;
import org.cytoscape.rest.internal.task.LogLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

@Provider
public class CyExceptionMapper implements ExceptionMapper<Throwable> {

	private static final Logger logger = LoggerFactory.getLogger(CyExceptionMapper.class);
	
	@Inject
	@LogLocation
	private URI logLocation;
	
	@Override
	public Response toResponse(Throwable ex) {
		//System.out.println("CyExceptionMapper accessed.");
		if (ex instanceof WebApplicationException) {
			//System.out.println("Identified WebApplicationException");
			WebApplicationException webApplicationException = (WebApplicationException) ex;
			Response response = webApplicationException.getResponse();
			Object entity = response.getEntity();
			System.out.println("Identified WebApplicationException with entity: "+ entity);
			return webApplicationException.getResponse();
		} else {
			CIResponse<Object> ciResponse = new CIResponse<Object>();
			ciResponse.data = new Object();
			ciResponse.errors = new ArrayList<CIError>();
			CIError error = new CIError();
			//System.out.println("Error report from thread: " + Thread.currentThread());
			
			error.link = logLocation;
			error.type = CyRESTConstants.getErrorURI("error-handling", 0);
			error.status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
			error.message = "Uncaught exception while processing resource [" +Thread.currentThread().getName()+"]: " + ex.getMessage();
			ciResponse.errors.add(error);
			logger.error(error.message, ex);
			return Response.serverError().entity(ciResponse).build();
		}
	}
}
