package org.cytoscape.rest.internal.resource;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.cytoscape.session.CySessionManager;



@Singleton
@Path("/v1/session")
public class SessionResource extends AbstractResource {

	@Context
	@NotNull
	private CySessionManager sessionManager;


	public SessionResource() {
		super();
	}


	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSessionName() {
		return sessionManager.getCurrentSessionFileName();
	}
}
