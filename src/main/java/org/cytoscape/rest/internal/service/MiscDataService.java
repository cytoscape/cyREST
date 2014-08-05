package org.cytoscape.rest.internal.service;

import java.util.Properties;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Singleton
@Path("/v1")
public class MiscDataService extends AbstractDataService {


	/**
	 * Return the Cytoscape and API version.
	 * 
	 * @return
	 */
	@GET
	@Path("/version")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCytoscapeVersion() {
	
		if(props == null) {
			throw new InternalServerErrorException("Could not find CyProperty object.");
		}

		final Properties property = (Properties) this.props.getProperties();
		final Object versionNumber = property.get("cytoscape.version.number");
		if(versionNumber != null) {
			
			return "{ \"apiVersion\":\"" + API_VERSION + "\",\"cytoscapeVersion\": \"" + versionNumber.toString() + "\"}";
		} else {
			throw new NotFoundException("Could not find Cytoscape version number property.");
		}
	}
}
