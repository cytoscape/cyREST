package org.cytoscape.rest.internal.resource;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.cytoscape.event.CyEventHelper;

import com.google.inject.Inject;

/**
 * This filter flushes payload events from Cytoscape's event model, forcing all events to be applied before returning.
 * 
 * It is necessary to keep CyREST responses from being out of sync with Cytoscape.
 * 
 * @author David Otasek
 *
 */
@Provider
public class EventFlushRequestFilter implements ContainerRequestFilter {

	@Inject
	protected CyEventHelper cyEventHelper;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		cyEventHelper.flushPayloadEvents();
	}
}