package org.cytoscape.rest.internal.resource;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.cytoscape.ci.CIResponseFactory;
import org.cytoscape.event.CyEventHelper;

import com.google.inject.Inject;

@Provider
public class EventFlushResponseFilter implements ContainerResponseFilter {
	
	@Inject
	protected CyEventHelper cyEventHelper;
	
	@Override
	public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
		if ("POST".equals(request.getMethod()) 
				|| "PUT".equals(request.getMethod())
				|| "PATCH".equals(request.getMethod())
				|| "DELETE".equals(request.getMethod())
			) {
			cyEventHelper.flushPayloadEvents();
			//System.out.println("Flushing events after model change: " + request.getMethod() + " "+  request.getUriInfo().getPath());
		}
		
	}
}