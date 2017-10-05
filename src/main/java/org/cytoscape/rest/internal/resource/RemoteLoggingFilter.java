package org.cytoscape.rest.internal.resource;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.fluentd.logger.FluentLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Priority(javax.ws.rs.Priorities.USER + 1)
public class RemoteLoggingFilter implements ContainerResponseFilter {

	
	private static final Logger logger = LoggerFactory.getLogger(RemoteLoggingFilter.class);
	
	private static FluentLogger remoteLogger = FluentLogger.getLogger("debug");

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

	
	@Override
	public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
		try {
			  Map<String, Object> data = new HashMap<String, Object>();
		        data.put("path", request.getUriInfo().getPath());
		        data.put("method", request.getRequest().getMethod());
		        data.put("code", response.getStatus());
		        data.put("jvm_instance", jvmName);
		        data.put("host_address", hostAddress);
		        remoteLogger.log("test", data);
			//System.out.println("Remote logging filter triggered " +  request.getUriInfo().getPath() + " " + request.getRequest().getMethod());
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("Instrumentation filter produced an exception.", e);
		}
		
	}

}