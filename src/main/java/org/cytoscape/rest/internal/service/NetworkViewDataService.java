package org.cytoscape.rest.internal.service;

import java.util.Collection;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST API for Network View objects.
 *
 */
@Singleton
@Path("/v1") // API version
public class NetworkViewDataService extends AbstractDataService {

	private final static Logger logger = LoggerFactory.getLogger(NetworkViewDataService.class);

	public NetworkViewDataService() {
		super();
	}

	/**
	 * Returns number of all network views in current session
	 * 
	 * @return
	 */
	@GET
	@Path("/" + NETWORKS + "/views/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Integer getAllNetworkViewCount() {
		return this.networkViewManager.getNetworkViewSet().size();
	}

	/**
	 * 
	 * @param id
	 * @param viewId
	 * @return
	 */
	@GET
	@Path("/" + NETWORKS + "/{id}/views/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Integer getNetworkViewCount(@PathParam("id") Long id) {
		return this.networkViewManager.getNetworkViews(getNetwork(id)).size();
	}


	/**
	 * Get the entire view object as JSON.
	 * 
	 * @param id
	 * @return
	 */
	@GET
	@Path("/" + NETWORKS + "/{id}/views/{viewId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getNetworkView(@PathParam("id") Long id, @PathParam("viewId") Long viewId) {
		
		
		return "{}";
	}
}
