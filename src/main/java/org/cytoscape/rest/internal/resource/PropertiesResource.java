package org.cytoscape.rest.internal.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cytoscape.rest.internal.CyRESTConstants;
import org.cytoscape.rest.internal.model.AppModel;
import org.cytoscape.rest.internal.task.AutomationAppTracker;
import org.cytoscape.rest.internal.task.CyPropertyListener;
import org.osgi.framework.Bundle;

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
	@ApiOperation(value="Gets a Cytoscape Property",
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
	
}
