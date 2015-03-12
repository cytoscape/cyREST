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

import org.cytoscape.model.CyNetwork;

import com.qmino.miredot.annotations.ReturnType;

@Singleton
@Path("/v1/networks.names")
public class NetworkNameResource extends AbstractResource {

	public NetworkNameResource() {
		super();
	}

	/**
	 * 
	 * Returns full list of network data as a JSON array. JSON is in <a
	 * href="http://cytoscape.github.io/cytoscape.js/">Cytoscape.js</a> format.
	 * If no query parameter is given, returns all networks in current session.
	 * 
	 * @summary Get networks in Cytoscape.js JSON format
	 * 
	 * @param column
	 *            Optional. Network table column name to be used for search.
	 * @param query
	 *            Optional. Search query.
	 * 
	 * @return Matched networks in Cytoscape.js JSON. If no query is given, all
	 *         networks.
	 * 
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@ReturnType("java.util.List<org.cytoscape.rest.internal.model.CyJsNetwork>")
	public List<Map<String,?>> getNetworks(@QueryParam("column") String column,
			@QueryParam("query") String query) {
		Set<CyNetwork> networks;

		if (column == null && query == null) {
			networks = networkManager.getNetworkSet();
		} else {
			if (column == null || column.length() == 0) {
				throw getError("Column name parameter is missing.",
						new IllegalArgumentException(),
						Response.Status.INTERNAL_SERVER_ERROR);
			}
			if (query == null || query.length() == 0) {
				throw getError("Query parameter is missing.",
						new IllegalArgumentException(),
						Response.Status.INTERNAL_SERVER_ERROR);
			}
			networks = getNetworksByQuery(query, column);
		}

		return getNetworksAsSimpleList(networks);
	}

	private final List<Map<String, ?>> getNetworksAsSimpleList(final Set<CyNetwork> networks) {
		if (networks.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		
		final List<Map<String, ?>> networksList = new ArrayList<>();
		for(final CyNetwork network: networks) {
			final Map<String, Object> values = new HashMap<>();
			values.put("SUID", network.getSUID());
			values.put("name", network.getRow(network).get(CyNetwork.NAME, String.class));
			networksList.add(values);
		}

		return networksList;
	}
}