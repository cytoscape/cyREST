package org.cytoscape.rest.internal.resource;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineManager;

/**
 * REST API for Network View objects.
 * 
 * TODO: add custom view information section
 * 
 */
@Singleton
@Path("/v1/networks/{networkId}/views")
public class NetworkViewResource extends AbstractResource {

	// Filter rendering engine by ID.
	private static final String DING_ID = "org.cytoscape.ding";

	private static final String DEF_HEIGHT = "600";

	@Context
	@NotNull
	private RenderingEngineManager renderingEngineManager;

	public NetworkViewResource() {
		super();
	}

	/**
	 * @summary Create view for the network
	 * 
	 * @param networkId
	 *            Network SUID
	 * 
	 * @return SUID for the new Network View.
	 * 
	 */
	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String createNetworkView(@PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyNetworkView view = networkViewFactory.createNetworkView(network);
		networkViewManager.addNetworkView(view);
		return getNumberObjectString("networkViewSUID", view.getSUID());
	}

	/**
	 * Cytoscape can have multiple views per network model. This feature is not
	 * exposed to end-users, but you can access it from this API.
	 * 
	 * @summary Get number of views for the given network model
	 * 
	 * @param networkId
	 *            Network SUID
	 * 
	 * @return Number of views for the network model
	 * 
	 */
	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	public String getNetworkViewCount(@PathParam("networkId") Long networkId) {
		return getNumberObjectString(JsonTags.COUNT, networkViewManager.getNetworkViews(getCyNetwork(networkId)).size());
	}

	/**
	 * 
	 * In general, a network has one view. But if there are multiple views, this
	 * deletes all of them.
	 * 
	 * @summary Delete all network views
	 * 
	 * @param networkId
	 *            Network SUID
	 * 
	 */
	@DELETE
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteAllNetworkViews(@PathParam("networkId") Long networkId) {
		try {
			final Collection<CyNetworkView> views = this.networkViewManager.getNetworkViews(getCyNetwork(networkId));
			final Set<CyNetworkView> toBeDestroyed = new HashSet<CyNetworkView>(views);
			for (final CyNetworkView view : toBeDestroyed) {
				networkViewManager.destroyNetworkView(view);
			}
		} catch (Exception e) {
			throw getError("Could not delete network views for network with SUID: " + networkId, e,
					Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 
	 * This returns a view for the network.
	 * 
	 * 
	 * @summary Convenience method to get the first view object.
	 * 
	 * @param networkId
	 *            Network SUID
	 * 
	 * @return
	 */
	@GET
	@Path("/first")
	@Produces(MediaType.APPLICATION_JSON)
	public String getFirstNetworkView(@PathParam("networkId") Long networkId) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(networkId);
		if (views.isEmpty()) {
			throw new NotFoundException("Could not find view for the network: " + networkId);
		}
		return getNetworkViewString(views.iterator().next());
	}

	/**
	 * @summary Delete a view whatever found first.
	 * 
	 * @param networkId
	 *            Network SUID
	 * 
	 */
	@DELETE
	@Path("/first")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteFirstNetworkView(@PathParam("networkId") Long networkId) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(networkId);
		if (views.isEmpty()) {
			return;
		}
		networkViewManager.destroyNetworkView(views.iterator().next());
	}

	/**
	 * To use this API, you need to know SUID of the CyNetworkView, in addition
	 * to CyNetwork SUID.
	 * 
	 * @summary Get a network view
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param viewId
	 *            Network View SUID
	 * 
	 * @return View in Cytoscape.js JSON. Currently, view information is (x, y)
	 *         location only.
	 */
	@GET
	@Path("/{viewId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getNetworkView(@PathParam("networkId") Long networkId, @PathParam("viewId") Long viewId) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(networkId);
		for (final CyNetworkView view : views) {
			final Long vid = view.getSUID();
			if (vid.equals(viewId)) {
				return getNetworkViewString(view);
			}
		}

		return "{}";
	}

	/**
	 * @summary Get SUID of all network views
	 * 
	 * @param networkId
	 *            Network SUID
	 * 
	 * @return Array of all network view SUIDs
	 * 
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Long> getAllNetworkViews(@PathParam("networkId") Long networkId) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(networkId);

		final Collection<Long> suids = new HashSet<Long>();

		for (final CyNetworkView view : views) {
			final Long viewId = view.getSUID();
			suids.add(viewId);
		}
		return suids;
	}

	private final String getNetworkViewString(final CyNetworkView networkView) {
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		CyWriter writer = cytoscapeJsWriterFactory.createWriter(stream, networkView);
		String jsonString = null;
		try {
			writer.run(null);
			jsonString = stream.toString("UTF-8");
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonString;
	}

	/**
	 * Generate a PNG image as stream. Default size is 600 px.
	 * 
	 * @summary Get PNG image of a network view
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param viewId
	 *            Network View SUID
	 * @param height
	 *            Optional height of the image. Width will be set automatically.
	 * 
	 * @return PNG image stream.
	 */
	@GET
	@Path("/first.png")
	@Produces("image/png")
	public Response getFirstImage(@PathParam("networkId") Long networkId,
			@DefaultValue(DEF_HEIGHT) @QueryParam("h") int height) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(networkId);
		if (views.isEmpty()) {
			throw new NotFoundException("Could not find view for the network: " + networkId);
		}

		final CyNetworkView view = views.iterator().next();
		return imageGenerator(view, height, height);
	}

	/**
	 * Generate a PNG network image as stream. Default size is 600 px.
	 * 
	 * @summary Get PNG image of a network view
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param viewId
	 *            Network View SUID
	 * @param height
	 *            Optional height of the image. Width will be set automatically.
	 * 
	 * @return PNG image stream.
	 */
	@GET
	@Path("/{viewId}.png")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getImage(@PathParam("networkId") Long networkId, @PathParam("viewId") Long viewId,
			@DefaultValue(DEF_HEIGHT) @QueryParam("h") int height) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(networkId);
		for (final CyNetworkView view : views) {
			final Long vid = view.getSUID();
			if (vid.equals(viewId)) {
				return imageGenerator(view, height, height);
			}
		}

		throw new NotFoundException("Could not find view for the network: " + networkId);
	}

	private final Response imageGenerator(final CyNetworkView view, int width, int height) {
		final Collection<RenderingEngine<?>> re = renderingEngineManager.getRenderingEngines(view);
		if (re.isEmpty()) {
			throw new IllegalArgumentException("No rendering engine.");
		}
		try {
			RenderingEngine<?> engine = null;

			for (RenderingEngine<?> r : re) {
				if (r.getRendererId().equals(DING_ID)) {
					engine = r;
					break;
				}
			}
			if (engine == null) {
				throw new IllegalArgumentException("Could not find Ding rendering eigine.");
			}

			// This is safe for Ding network view.
			final BufferedImage image = (BufferedImage) engine.createImage(width, height);
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
			final byte[] imageData = baos.toByteArray();

			return Response.ok(new ByteArrayInputStream(imageData)).build();
		} catch (Exception e) {
			e.printStackTrace();
			throw getError("Could not create image.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
}