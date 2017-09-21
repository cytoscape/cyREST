package org.cytoscape.rest.internal.resource;

import java.io.IOException;
import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Priority(javax.ws.rs.Priorities.USER + 1)
public class InstrumentationFilter implements ContainerResponseFilter {

	private static final Logger logger = LoggerFactory.getLogger(InstrumentationFilter.class);

	@Override
	public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
			System.out.println("Instrumentation filter: " + request.getRequest().getMethod() + " " + request.getUriInfo().getPath());
	}
}