package org.cytoscape.rest.internal.resource;

import java.io.ByteArrayOutputStream;
import java.util.Collection;

import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cytoscape.io.write.CyWriter;
import org.cytoscape.view.model.CyNetworkView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST API for Network View objects.
 * 
 * TODO: add custom view information section
 *
 */
@Singleton
@Path("/v1/networks/{networkId}/views") // API version
public class NetworkViewResource extends AbstractResource {

	private final static Logger logger = LoggerFactory.getLogger(NetworkViewResource.class);

	
	public NetworkViewResource() {
		super();
	}


	/**
	 * Get number of views for the given network model
	 * 
	 * @param networkId Network SUID
	 * 
	 * @return Number of views for the network model
	 * 
	 */
	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Integer getNetworkViewCount(@PathParam("networkId") Long networkId) {
		return this.networkViewManager.getNetworkViews(getCyNetwork(networkId)).size();
	}


	/**
	 * Delete all network views
	 * 
	 * @param networkId Network SUID
	 * 
	 */
	@DELETE
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteAllNetworkViews(@PathParam("networkId") Long networkId) {
		final Collection<CyNetworkView> views = this.networkViewManager.getNetworkViews(getCyNetwork(networkId));
		for(final CyNetworkView view: views) {
			networkViewManager.destroyNetworkView(view);
		}
	}


	/**
	 * Convenience method to get the first view object.
	 * 
	 * @param networkId Network SUID
	 * 
	 * @return
	 */
	@GET
	@Path("/first")
	@Produces(MediaType.APPLICATION_JSON)
	public String getFirstNetworkView(@PathParam("networkId") Long networkId) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(networkId);
		return getNetworkViewString(views.iterator().next());
	}


	@DELETE
	@Path("/first")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteFirstNetworkView(@PathParam("networkId") Long networkId) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(networkId);
		networkViewManager.destroyNetworkView(views.iterator().next());
	}


	/**
	 * Get the entire view object as JSON.
	 * 
	 * @param id
	 * @return
	 */
	@GET
	@Path("/{viewId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getNetworkView(@PathParam("networkId") Long networkId, @PathParam("viewId") Long viewId) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(networkId);
		for(final CyNetworkView view:views) {
			final Long vid = view.getSUID();
			if(vid.equals(viewId)) {
				return getNetworkViewString(view);
			}
		}
		
		return "{}";
	}


	private final String getNetworkViewString(final CyNetworkView networkView) {
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		CyWriter writer = cytoscapeJsWriterFactory.createWriter(stream, networkView);
		String jsonString = null;
		try {
			writer.run(null);
			jsonString = stream.toString();
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonString;
	}
}