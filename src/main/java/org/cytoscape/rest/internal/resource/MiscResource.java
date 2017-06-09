package org.cytoscape.rest.internal.resource;

import java.util.Properties;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cytoscape.rest.internal.model.CytoscapeVersion;
import org.cytoscape.rest.internal.model.ServerStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Resource to provide general status of the Cytoscape REST server. 
 * 
 * 
 * @servicetag Server status
 * 
 */
@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.REST_SERVICE_TAG,CyRESTSwagger.CyRESTSwaggerConfig.CYTOSCAPE_SYSTEM_TAG})
@Singleton
@Path("/v1")
public class MiscResource extends AbstractResource {

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@ApiOperation(value="Cytoscape RESTful API server status",
			notes="500 If REST API Module is not working")
	public ServerStatus getStatus() {
		return new ServerStatus();
	}

	@GET
	@Path("/gc")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Run Java garbage collection",
			notes="Call System.gc() to free up memory. In general, this is not necessary.")
	public void runGarbageCollection() {
		Runtime.getRuntime().gc();
	}
	
	@GET
	@Path("/version")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get Cytoscape and REST API version"
	)
	public CytoscapeVersion getCytoscapeVersion() {

		if (props == null) {
			throw new InternalServerErrorException("Could not find CyProperty object.");
		}

		final Properties property = (Properties) this.props.getProperties();
		final Object versionNumber = property.get("cytoscape.version.number");
		if (versionNumber != null) {
			return new CytoscapeVersion(API_VERSION, versionNumber.toString());
		} else {
			throw new NotFoundException("Could not find Cytoscape version number property.");
		}
	}
	
	//TODO Why is this here? It doesn't change anything, and appears to be the same as GET.
	@PUT
	@Path("/ui/show-details")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Show Level of Graphics Details", hidden=true,
	notes="### Unimplemented\n\nDo not rely on this function.")
	public CytoscapeVersion updateShowGraphicsDetailsOption() {

		if (props == null) {
			throw new InternalServerErrorException("Could not find CyProperty object.");
		}

		final Properties property = (Properties) this.props.getProperties();
		final Object versionNumber = property.get("cytoscape.version.number");
		if (versionNumber != null) {
			return new CytoscapeVersion(API_VERSION, versionNumber.toString());
		} else {
			throw new NotFoundException("Could not find Cytoscape version number property.");
		}
	}
}
