package org.cytoscape.rest.internal.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.rest.internal.model.SUIDNameModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import static org.cytoscape.rest.internal.resource.NetworkErrorConstants.*;

@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.NETWORKS_TAG})
@Singleton
@Path("/v1/networks.names")
public class NetworkNameResource extends AbstractResource {

	private static final String RESOURCE_URN = "networks";

	@Override
	public String getResourceURI() {
		return RESOURCE_URN;
	}
	
	private final static Logger logger = LoggerFactory.getLogger(NetworkNameResource.class);
	
	@Override
	public Logger getResourceLogger() {
		return logger;
	}
	
	public NetworkNameResource() {
		super();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@ApiOperation(value = "Returns a list of network names with corresponding SUIDs",
		notes="Returns a list of all networks as names and their corresponding SUIDs.\n\n" + NETWORK_QUERY_DESCRIPTION,
		    response = SUIDNameModel.class, responseContainer="List")
	public List<Map<String,Object>> getNetworksNames(
			@ApiParam(value=COLUMN_DESCRIPTION) @QueryParam("column") String column,
			@ApiParam(value=QUERY_STRING_DESCRIPTION) @QueryParam("query") String query) {
		Set<CyNetwork> networks;

		if (column == null && query == null) {
			networks = networkManager.getNetworkSet();
		} else {
			if (column == null || column.length() == 0) {
				//throw getError("Column name parameter is missing.",
				//		new IllegalArgumentException(),
				//		Response.Status.INTERNAL_SERVER_ERROR);
				throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						getResourceURI(), 
						INVALID_PARAMETER_ERROR, 
						"Column parameter is missing.", 
						getResourceLogger(), null);
			}
			if (query == null || query.length() == 0) {
				//throw getError("Query parameter is missing.",
				//		new IllegalArgumentException(),
				//		Response.Status.INTERNAL_SERVER_ERROR);
				throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						getResourceURI(), 
						INVALID_PARAMETER_ERROR, 
						"Query parameter is missing.", 
						getResourceLogger(), null);
			}
			networks = getNetworksByQuery(query, column);
		}

		return getNetworksAsSimpleList(networks);
	}

	@SuppressWarnings("unchecked")
	private final List<Map<String, Object>> getNetworksAsSimpleList(final Set<CyNetwork> networks) {
		if (networks.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		
		final List<Map<String, Object>> networksList = new ArrayList<>();
		for(final CyNetwork network: networks) {
			final Map<String, Object> values = new HashMap<>();
			values.put("SUID", network.getSUID());
			values.put("name", network.getRow(network).get(CyNetwork.NAME, String.class));
			networksList.add(values);
		}

		return networksList;
	}
}