package org.cytoscape.rest.internal.resource;

import java.util.Set;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.rest.internal.CyRESTConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


import static org.cytoscape.rest.internal.resource.NetworkErrorConstants.*;

@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.NETWORKS_TAG})
@Singleton
@Path("/v1/networks.json")
public class NetworkFullResource extends AbstractResource {

	private static final String RESOURCE_URN = "networks";

	@Override
	public String getResourceURI() {
		return RESOURCE_URN;
	}
	
	private final static Logger logger = LoggerFactory.getLogger(NetworkFullResource.class);
	
	@Override
	public Logger getResourceLogger() {
		return logger;
	}
	
	public NetworkFullResource() {
		super();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@ApiOperation(
			value=" Get networks in Cytoscape.js JSON format",
			notes="Returns a list of all networks as an array of "
					+ "[Cytoscape.js]("+CyRESTConstants.CYTOSCAPE_JS_FILE_FORMAT_LINK+") format entries.\n\n" + NETWORK_QUERY_DESCRIPTION)
	public String getNetworks(
			@ApiParam(value=COLUMN_DESCRIPTION, required=false) @QueryParam("column") String column, 
			@ApiParam(value=QUERY_STRING_DESCRIPTION, required=false) @QueryParam("query") String query) {
		Set<CyNetwork> networks;
		
		if (column == null && query == null) {
			networks = networkManager.getNetworkSet();
		} else {
			try {
				networks = getNetworksByQuery(INVALID_PARAMETER_ERROR, query, column);
			} catch(WebApplicationException e) {
				throw(e);
			} catch (Exception e) {
				//throw getError("Could not get networks.", e, Response.Status.INTERNAL_SERVER_ERROR);
				throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						getResourceURI(), 
						INTERNAL_METHOD_ERROR, 
						"Error executing Network query.", 
						getResourceLogger(), e);
			}
		}
		
		return getNetworksAsString(networks);
	}


	private final String getNetworksAsString(final Set<CyNetwork> networks) {
		if (networks.isEmpty()) {
			return "[]";
		}

		final StringBuilder result = new StringBuilder();
		result.append("[");

		for (final CyNetwork network : networks) {
			result.append(getNetworkString(SERVICE_UNAVAILABLE_ERROR, SERIALIZATION_ERROR, network));
			result.append(",");
		}
		String jsonString = result.toString();
		jsonString = jsonString.substring(0, jsonString.length() - 1);

		return jsonString + "]";
	}
}
