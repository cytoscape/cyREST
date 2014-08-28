package org.cytoscape.rest.internal.resource;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.glassfish.grizzly.utils.Exceptions;

@Provider
public class CyExceptionMapper implements ExceptionMapper<InternalServerErrorException> {

	@Override
	public Response toResponse(InternalServerErrorException ex) {
		return Response.status(500).entity(Exceptions.getStackTraceAsString(ex)).type(MediaType.APPLICATION_JSON)
				.build();
	}
}
