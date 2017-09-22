package org.cytoscape.rest.internal.resource;

import java.io.IOException;
import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import io.prometheus.client.Counter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Priority(javax.ws.rs.Priorities.USER + 1)
public class InstrumentationFilter implements ContainerResponseFilter {

	private static final Logger logger = LoggerFactory.getLogger(InstrumentationFilter.class);
	
	public final static String[] LABELS = {
		"METHOD",
		"PATH"
	};
	
	public final static String SUCCESSFUL_REQUESTS = "SUCCESSFUL_REQUESTS";
	
	static Counter requestCounter = Counter.build()
			.name(SUCCESSFUL_REQUESTS).help("Successful requests to CyREST").labelNames(LABELS).register();
	
	@Override
	public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
		
		try {
			final String[] values = getValues(request);
			requestCounter.labels(values).inc();
			//System.out.println("Instrumentation filter: " + values[0] + " " + values[1] + " count=" + requestCounter.labels(values).get());
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("Instrumentation filter produced an exception.", e);
		}
		
	}
	
	public String[] getValues(ContainerRequestContext request) {
		return new String[]{request.getRequest().getMethod(), request.getUriInfo().getPath()};
	}
}