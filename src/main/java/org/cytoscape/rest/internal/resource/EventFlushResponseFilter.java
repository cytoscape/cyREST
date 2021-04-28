package org.cytoscape.rest.internal.resource;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.cytoscape.event.CyEventHelper;

import com.google.inject.Inject;

@Provider
public class EventFlushResponseFilter implements ContainerResponseFilter {

	@Inject
	protected CyEventHelper cyEventHelper;

	@Override
	public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
		cyEventHelper.flushPayloadEvents();
	}
}