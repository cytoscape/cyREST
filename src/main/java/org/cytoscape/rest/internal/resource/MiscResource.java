package org.cytoscape.rest.internal.resource;

import java.util.Properties;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cytoscape.rest.internal.model.CytoscapeVersion;
import org.cytoscape.rest.internal.model.ServerStatus;

/**
 * Resource to provide general status of the Cytoscape REST server. 
 * 
 * 
 * @servicetag Server status
 * 
 */
@Singleton
@Path("/v1")
public class MiscResource extends AbstractResource {

	

	/**
	 * @summary Cytoscape RESTful API server status
	 * 
	 * @return Summary of server status
	 * 
	 * @statuscode 500 If REST API Module is not working.
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ServerStatus getStatus() {
		return new ServerStatus();
	}


	/**
	 * Run System.gc().  In general, this is not necessary.
	 * 
	 * @summary Force to run garbage collection to free up memory
	 */
	@GET
	@Path("/gc")
	@Produces(MediaType.APPLICATION_JSON)
	public void runGarbageCollection() {
		Runtime.getRuntime().gc();
	}
	
	
	/**
	 * 
	 * @summary Get Cytoscape and REST API version
	 * 
	 * @return Cytoscape version and REST API version
	 * 
	 */
	@GET
	@Path("/version")
	@Produces(MediaType.APPLICATION_JSON)
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
}
