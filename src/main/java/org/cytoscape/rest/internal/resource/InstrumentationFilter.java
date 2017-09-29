package org.cytoscape.rest.internal.resource;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.exporter.PushGateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Priority(javax.ws.rs.Priorities.USER + 1)
public class InstrumentationFilter implements ContainerResponseFilter {

	
private static final Logger logger = LoggerFactory.getLogger(InstrumentationFilter.class);
	
	private static final PushGateway pg = new PushGateway("v1.prometheus-pushgateway.test.cytoscape.io:9091");
	
	private static final CollectorRegistry collectorRegistry = CollectorRegistry.defaultRegistry;
	
	public final static String[] LABELS = {
		"METHOD",
		"PATH"
	};
	
	public final static String SUCCESSFUL_REQUESTS = "successful_requests";
	
	static Counter requestCounter = Counter.build()
			.name(SUCCESSFUL_REQUESTS).help("Successful requests to CyREST").labelNames(LABELS).register();
	
	static RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
	static String jvmName = runtimeBean.getName();
	
	static String hostAddress = "";
	static {
		try {
			hostAddress = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	static Map<String, String> groupingKey = Collections.singletonMap("instance", hostAddress+" "+jvmName);
	
	@Override
	public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
		
		try {
			final String[] values = getValues(request);
			requestCounter.labels(values).inc();
			pg.push(collectorRegistry, SUCCESSFUL_REQUESTS, groupingKey);
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