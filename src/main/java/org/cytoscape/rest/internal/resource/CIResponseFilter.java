package org.cytoscape.rest.internal.resource;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.cytoscape.ci.CIWrapping;
import org.cytoscape.ci.model.CIError;
import org.cytoscape.ci.model.CIResponse;

@Provider
public class CIResponseFilter implements ContainerResponseFilter {

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
				else if (response.getStatus() >= 400 && response.getStatus() <= 600) { //Something went wrong; produce an error
					System.out.println("CIResponseFilter caught error resource.");
				}
			}
		}

	}
}