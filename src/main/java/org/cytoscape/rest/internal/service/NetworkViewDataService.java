package org.cytoscape.rest.internal.service;

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
import org.cytoscape.model.CyNetwork;
import org.cytoscape.rest.internal.serializer.ViewSerializer;
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
@Path("/v1/networks/{id}/views") // API version
public class NetworkViewDataService extends AbstractDataService {

	private final static Logger logger = LoggerFactory.getLogger(NetworkViewDataService.class);

	
	public NetworkViewDataService() {
		super();
	}


	/**
	 * 
	 * @param id
	 * @return
	 */
	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Integer getNetworkViewCount(@PathParam("id") Long id) {
		return this.networkViewManager.getNetworkViews(getCyNetwork(id)).size();
	}

	
	/**
	 * Delete all views for the target network.
	 * 
	 * @param id
	 */
	@DELETE
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteAllNetworkViews(@PathParam("id") Long id) {
		final Collection<CyNetworkView> views = this.networkViewManager.getNetworkViews(getCyNetwork(id));
		for(final CyNetworkView view: views) {
			networkViewManager.destroyNetworkView(view);
		}
	}

	/**
	 * Convenience method to get the first view object.
	 * 
	 * @param id Network SUID
	 * 
	 * @return
	 */
	@GET
	@Path("/first")
	@Produces(MediaType.APPLICATION_JSON)
	public String getFirstNetworkView(@PathParam("id") Long id) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(id);
		return getNetworkViewString(views.iterator().next());
	}
	
	@DELETE
	@Path("/first")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteFirstNetworkView(@PathParam("id") Long id) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(id);
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
	public String getNetworkView(@PathParam("id") Long id, @PathParam("viewId") Long viewId) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(id);
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