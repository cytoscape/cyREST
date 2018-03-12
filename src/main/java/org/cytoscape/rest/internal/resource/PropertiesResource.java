package org.cytoscape.rest.internal.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cytoscape.rest.internal.model.CyPropertyModel;
import org.cytoscape.rest.internal.model.CyPropertyValueModel;
import org.cytoscape.rest.internal.task.CyPropertyListener;
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
	
	@Inject
	protected CyPropertyListener cyPropertyListener;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="List available Cytoscape Property namespaces",
	notes="Returns a list of available Cytoscape Property namespaces")
	public List<String> getPropertyNamespaceList() {
		return cyPropertyListener.getPropertyNames();
	
	}
	
	@GET
	@Path("/{namespace}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Gets a list of Cytoscape Properties for a namespace",
	notes="Returns the Cytoscape Properties in the namespace specified by the `namespace` parameter.")
	public List<String> getPropertyList(@ApiParam(value="Cytoscape Property namespace") @PathParam("namespace") String namespace)
	{
		
		if (cyPropertyListener.getCyProperty(namespace) == null) {
			
		}
		Properties properties = (Properties) cyPropertyListener.getCyProperty(namespace).getProperties();
		if (properties == null) {
			
		}
		List<String> output = new ArrayList<String>();
		output.addAll(properties.stringPropertyNames());
		return output;
	}
	
	@GET
	@Path("/{namespace}/{propertyKey}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Gets a Cytoscape Property",
	notes="Returns the Cytoscape Property specified by the `namespace` and `propertyKey` parameters.")
	public CyPropertyModel getProperty(@ApiParam(value="Cytoscape Property namespace") @PathParam("namespace") String namespace ,
			@ApiParam(value="Key of the CyProperty") @PathParam("propertyKey") String propertyKey 
			){
		CyPropertyModel output = null;
		if (cyPropertyListener.getCyProperty(namespace) == null) {
			
		}
		Properties properties = (Properties) cyPropertyListener.getCyProperty(namespace).getProperties();
		if (properties == null) {
			
		} 
		else {
			output = new CyPropertyModel();
			String property = properties.getProperty(propertyKey);
			output.key = propertyKey;
			output.value = property;
			
		}
		return output;
	}
	
	@PUT
	@Path("/{namespace}/{propertyKey}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Sets a Cytoscape Property",
	notes="Sets the Cytoscape Property specified by the `namespace` and `propertyKey` parameters.")
	public void putProperty(@ApiParam(value="Cytoscape Property namespace") @PathParam("namespace") String namespace ,
			@ApiParam(value="Key of the CyProperty") @PathParam("propertyKey") String propertyKey,
			@ApiParam(value="A CyProperty value") CyPropertyValueModel propertyValue
			){
		
		if (cyPropertyListener.getCyProperty(namespace) == null) {
			
		}
		Properties properties = (Properties) cyPropertyListener.getCyProperty(namespace).getProperties();
		if (properties == null) {
			
		} 
		else {
			if (properties.containsKey(propertyKey)) {
				properties.setProperty(propertyKey, propertyValue.value);
			}
			else {
				
			}
		}
	}
	
	@POST
	@Path("/{namespace}/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Creates a Cytoscape Property",
	notes="Creates the Cytoscape Property specified by the `namespace` and `propertyKey` parameters.")
	public void postProperty(@ApiParam(value="Cytoscape Property namespace") @PathParam("namespace") String namespace ,
			@ApiParam(value="A CyProperty value") CyPropertyModel propertyValue
			){
		
		if (cyPropertyListener.getCyProperty(namespace) == null) {
			
		}
		Properties properties = (Properties) cyPropertyListener.getCyProperty(namespace).getProperties();
		if (properties == null) {
			
		} 
		else {
			properties.setProperty(propertyValue.key, propertyValue.value);
		}
		
	}
}
