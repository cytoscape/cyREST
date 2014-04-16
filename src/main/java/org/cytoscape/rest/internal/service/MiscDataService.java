package org.cytoscape.rest.internal.service;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Singleton
@Path("/v1")
public class MiscDataService {

	@GET
	@Path("/version")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCytoscapeVersion() {
		
		// TODO: use CyVersion service here.
		return "{\"cytoscapeVersion\": \"3.1.0\"}";
	}
}
