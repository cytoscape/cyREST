package org.cytoscape.rest.internal.resource;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.cytoscape.event.CyEventHelper;

import com.google.inject.Inject;

@Provider
public class EventFlushRequestFilter implements ContainerRequestFilter {

	@Inject
	protected CyEventHelper cyEventHelper;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		cyEventHelper.flushPayloadEvents();
	}
}