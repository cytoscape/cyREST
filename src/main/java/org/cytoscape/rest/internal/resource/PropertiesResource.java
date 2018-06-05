package org.cytoscape.rest.internal.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.cytoscape.ci.model.CIResponse;
import org.cytoscape.rest.internal.model.CyPropertyModel;
import org.cytoscape.rest.internal.model.CyPropertyValueModel;
import org.cytoscape.rest.internal.task.CyPropertyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Resource to provide the status of installed Cytoscape apps. 
 * 
 * @servicetag Server status
 * 
 */
@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.PROPERTIES_TAG})
@Singleton
@Path("/v1/properties")
public class PropertiesResource extends AbstractResource {
	
	static final String RESOURCE_URN = "properties";

	private final static Logger logger = LoggerFactory.getLogger(PropertiesResource.class);

	@Override
	public String getResourceURI() {
		return RESOURCE_URN;
	}
	
	@Override
	public Logger getResourceLogger() {
		return logger;
	}
	
	static final int NOT_FOUND_ERROR = 1;

	static final int INVALID_PARAMETER_ERROR = 2;

	
	
	@Inject
	protected CyPropertyListener cyPropertyListener;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="List available Cytoscape Property namespaces",
	notes="Returns a list of available Cytoscape Property namespaces")
	public CIResponse<List<String>> getPropertyNamespaceList() {
		return ciResponseFactory.getCIResponse(cyPropertyListener.getPropertyNames());
	}
	
	private Properties getProperties(String namespace) {
		if (cyPropertyListener.getCyProperty(namespace) == null) {
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NOT_FOUND_ERROR, 
					"Could not find property namespace: " + namespace, 
					logger, null);
		}
		Properties properties = (Properties) cyPropertyListener.getCyProperty(namespace).getProperties();
		if (properties == null) {
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NOT_FOUND_ERROR, 
					"Property namespace does not contain properties: " + namespace, 
					logger, null);
		}
		return properties;
	}
	
	@GET
	@Path("/{namespace}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Gets a list of Cytoscape Properties for a namespace",
	notes="Returns the Cytoscape Properties in the namespace specified by the `namespace` parameter.")
	public CIResponse<List<String>> getPropertyList(@ApiParam(value="Cytoscape Property namespace") @PathParam("namespace") String namespace)
	{
		Properties properties = getProperties(namespace);
		List<String> output = new ArrayList<String>();
		output.addAll(properties.stringPropertyNames());
		return ciResponseFactory.getCIResponse(output);
	}
	
	private static class CyPropertyModelResponse extends CIResponse<CyPropertyModel> {};
	
	@GET
	@Path("/{namespace}/{propertyKey}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Gets a Cytoscape Property",
	notes="Returns the Cytoscape Property specified by the `namespace` and `propertyKey` parameters.",
	response=CyPropertyModelResponse.class)
	public Response getProperty(@ApiParam(value="Cytoscape Property namespace") @PathParam("namespace") String namespace ,
			@ApiParam(value="Key of the CyProperty") @PathParam("propertyKey") String propertyKey 
			){
		CyPropertyModel output = null;
		Properties properties = getProperties(namespace);
		output = new CyPropertyModel();
		if (!properties.containsKey(propertyKey)) {
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NOT_FOUND_ERROR, 
					"Property namespace \"" + namespace + "\" does not contain property: " + propertyKey, 
					logger, null);
		}
		String property = properties.getProperty(propertyKey);
		output.key = propertyKey;
		output.value = property;
			
		return Response.ok(ciResponseFactory.getCIResponse(output)).build();
	}
	
	@PUT
	@Path("/{namespace}/{propertyKey}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Sets a Cytoscape Property",
	notes="Sets the Cytoscape Property specified by the `namespace` and `propertyKey` parameters.")
	public CIResponse<Object> putProperty(@ApiParam(value="Cytoscape Property namespace") @PathParam("namespace") String namespace ,
			@ApiParam(value="Key of the CyProperty") @PathParam("propertyKey") String propertyKey,
			@ApiParam(value="A CyProperty value") CyPropertyValueModel propertyValue
			){
		Properties properties = getProperties(namespace);
		if (properties.containsKey(propertyKey)) {
			properties.setProperty(propertyKey, propertyValue.value);
		}
		else {
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NOT_FOUND_ERROR, 
					"Property namespace \"" + namespace + "\" does not contain property: " + propertyKey, 
					logger, null);
		}
		return ciResponseFactory.getCIResponse(new Object());
	}
	
	@DELETE
	@Path("/{namespace}/{propertyKey}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Deletes a Cytoscape Property",
	notes="Deletes the Cytoscape Property specified by the `namespace` and `propertyKey` parameters.")
	public CIResponse<Object> deleteProperty(@ApiParam(value="Cytoscape Property namespace") @PathParam("namespace") String namespace ,
			@ApiParam(value="Key of the CyProperty") @PathParam("propertyKey") String propertyKey
			){
		Properties properties = getProperties(namespace);
		if (properties.containsKey(propertyKey)) {
			properties.remove(propertyKey);
		}
		else {
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NOT_FOUND_ERROR, 
					"Property namespace \"" + namespace + "\" does not contain property: " + propertyKey, 
					logger, null);
		}
		return ciResponseFactory.getCIResponse(new Object());
	}
	
	@POST
	@Path("/{namespace}/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Creates a Cytoscape Property",
	notes="Creates a Cytoscape Property in the namespace specified by the `namespace` parameter.")
	public CIResponse<Object> postProperty(@ApiParam(value="Cytoscape Property namespace") @PathParam("namespace") String namespace ,
			@ApiParam(value="A CyProperty with a key and value") CyPropertyModel propertyValue
			){
		Properties properties = getProperties(namespace);
		properties.setProperty(propertyValue.key, propertyValue.value);
		return ciResponseFactory.getCIResponse(new Object());
	}
}
