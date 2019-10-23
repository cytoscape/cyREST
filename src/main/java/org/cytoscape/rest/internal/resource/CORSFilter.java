package org.cytoscape.rest.internal.resource;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class CORSFilter implements ContainerResponseFilter {

	/**
	 * Add CORS support to cyREST
	 */
	@Override
	public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
		response.getHeaders().add("Access-Control-Allow-Origin", "*");
		response.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
		response.getHeaders().add("Access-Control-Allow-Credentials", request.getMethod().equals("OPTIONS") ? "false" : "true");
		response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
	}
}
