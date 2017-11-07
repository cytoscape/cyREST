package org.cytoscape.rest.internal.resource;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.cytoscape.ci.CIErrorFactory;
import org.cytoscape.ci.CIExceptionFactory;
import org.cytoscape.ci.CIResponseFactory;
import org.cytoscape.ci.CIWrapping;
import org.cytoscape.ci.model.CIError;
import org.cytoscape.ci.model.CIResponse;

import org.cytoscape.rest.internal.CyRESTConstants;
import org.cytoscape.rest.internal.task.LogLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

@Provider
public class CIResponseFilter implements ContainerResponseFilter {

	private static final Logger logger = LoggerFactory.getLogger(CIResponseFilter.class);
	
	@Inject
	@LogLocation
	private URI logLocation;
	
	@Inject
	protected CIResponseFactory ciResponseFactory;
	
	@Inject
	protected CIErrorFactory ciErrorFactory;
	
	@Inject
	protected CIExceptionFactory ciExceptionFactory;
	
	public static final String INTERCEPTED_ERROR_URN = CyRESTConstants.cyRESTCIRoot + ":ciresponsefilter:0";
	
	@Override
	public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {

		boolean hasCIWrapperAnnotation = false;

		Annotation[] annotations = response.getEntityAnnotations();

		for (Annotation annotation : annotations) {
			if (annotation.annotationType().equals(CIWrapping.class)) {
				hasCIWrapperAnnotation = true;
			}
		}
		//System.out.println("ci-filter CIWrapping header: " + request.getHeaders().get("CIWrapping") + " annotation:" + hasCIWrapperAnnotation);
		if ((request.getHeaders().get("CIWrapping") != null && request.getHeaders().get("CIWrapping").contains("true"))
				|| hasCIWrapperAnnotation
				)
		{
			Object object = response.getEntity();
			if (!(object instanceof CIResponse)) { //If the response isn't a CI response, wrap it.
				if (response.getStatus() >= 200 && response.getStatus() <= 300) { //Success; wrap any returned data

					if (object instanceof String) {
						String wrappedObject = "{\n   \"data\":" + object + ",\n   \"errors\":[]\n}";
						response.setEntity(wrappedObject);
					} else {
						try {
							CIResponse<Object> ciResponse = CIResponse.class.newInstance();
							ciResponse.data = object;
							ciResponse.errors = new ArrayList<CIError>();
							response.setEntity(ciResponse);
						} catch (InstantiationException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
				else if (response.getStatus() >= 400 && response.getStatus() <= 600) { //Something went wrong; produce an error
					
					logger.error(response.getEntity().toString());
					CIResponse<Object> ciResponse;
					try {
						ciResponse = CIResponse.class.newInstance();
						ciResponse.data = new Object();
						ciResponse.errors = Arrays.asList(ciErrorFactory.getCIError(response.getStatus(), INTERCEPTED_ERROR_URN, response.getStatusInfo().getReasonPhrase()));
						response.setEntity(ciResponse);
					} catch (InstantiationException e) {
					
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						
						e.printStackTrace();
					}
				}
			}
		}

	}
}