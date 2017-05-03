package org.cytoscape.rest.service;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.ci.CIWrapping;
import org.cytoscape.ci.model.CIError;
import org.cytoscape.rest.internal.CIErrorFactoryImpl;
import org.cytoscape.rest.internal.CIExceptionFactoryImpl;
import org.cytoscape.rest.internal.model.Message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Path("/ciresource")
public class CIHandlingResource {
	
	@Path("/success")
	@GET    
	@Produces(MediaType.APPLICATION_JSON)
	@CIWrapping
	public Message success() {
		return new Message("Hello!");
	}
	
	@Path("/successFromEmptyResponse")
	@GET    
	@Produces(MediaType.APPLICATION_JSON)
	@CIWrapping
	public Response successFromEmptyResponse() {
		return Response.ok().build();
	}
	
	@Path("/successFromEntityResponse")
	@GET    
	@Produces(MediaType.APPLICATION_JSON)
	@CIWrapping
	public Response successFromEntityResponse() {
		return Response.ok(new Message("Hello!")).build();
	}
	
	@Path("/successFromStringResponse")
	@GET    
	@Produces(MediaType.APPLICATION_JSON)
	@CIWrapping
	public Response successFromStringResponse() {
		return Response.ok("Hello!", MediaType.APPLICATION_JSON).build();
	}
	
	@Path("/fail")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@CIWrapping
	public String fail() throws Exception {
		throw new Exception("Kaboom.");
	}
	
	@Path("/failwithresource")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@CIWrapping
	public String failWithCIError() throws WebApplicationException {
		CIError ciError = new CIErrorFactoryImpl().getCIError(500, "urn:cytoscape:ci:ci-wrap-test:v1:fail-with-ci-error:errors:1", "Intentional fail to report with CI Resource.", URI.create("http://www.google.ca"));
		throw new CIExceptionFactoryImpl().getCIException(500, new CIError[]{ciError});
	}
	
}
