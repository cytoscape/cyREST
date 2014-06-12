package org.cytoscape.rest.internal.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.rest.internal.task.HeadlessTaskMonitor;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

@Singleton
@Path("/v1")
public class AlgorithmicService extends AbstractDataService {

	@Context
	private TaskMonitor headlessTaskMonitor;
	
	@Context
	private CyLayoutAlgorithmManager layoutManager;

	@GET
	@Path("/apply/layouts/{algorithmName}/{targetId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String applyLayout(@PathParam("algorithmName") String algorithmName, @PathParam("targetId") Long targetId) {

		final CyNetwork network = getCyNetwork(targetId);
		Collection<CyNetworkView> views = this.networkViewManager.getNetworkViews(network);
		if (views.isEmpty()) {
			throw new NotFoundException("View is not available for the network " + targetId);
		}

		final CyNetworkView view = views.iterator().next();
		final CyLayoutAlgorithm layout = this.layoutManager.getLayout(algorithmName);

		final TaskIterator itr = layout.createTaskIterator(view, layout.getDefaultLayoutContext(),
				CyLayoutAlgorithm.ALL_NODE_VIEWS, "");
		try {
			itr.next().run(headlessTaskMonitor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "{\"status\":\"OK\"}";
	}


	@GET
	@Path("/apply/styles/{styleName}/{targetId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String applyStyle(@PathParam("styleName") String styleName, @PathParam("targetId") Long targetId) {

		final CyNetwork network = getCyNetwork(targetId);
		final Set<VisualStyle> styles = vmm.getAllVisualStyles();
		VisualStyle targetStyle = null;
		for(final VisualStyle style:styles) {
			final String name = style.getTitle();
			if(name.equals(styleName)) {
				targetStyle = style;
				break;
			}
		}
		if(targetStyle == null) {
			throw new NotFoundException("Could not find Visual Style: " + styleName);
		}
		
		Collection<CyNetworkView> views = this.networkViewManager.getNetworkViews(network);
		if (views.isEmpty()) {
			throw new NotFoundException("View is not available for the network " + targetId);
		}

		
		final CyNetworkView view = views.iterator().next();
		vmm.setVisualStyle(targetStyle, view);
		vmm.setCurrentVisualStyle(targetStyle);
		targetStyle.apply(view);
		
		return "{\"status\":\"OK\"}";
	}

	@GET
	@Path("/layouts")
	@Produces(MediaType.APPLICATION_JSON)
	public String getLayouts() {
		final Collection<CyLayoutAlgorithm> layouts = layoutManager.getAllLayouts();
		final List<String> layoutNames= new ArrayList<String>();
		for(final CyLayoutAlgorithm layout: layouts) {
			layoutNames.add(layout.getName());
		}
		try {
			return getNames(layoutNames);
		} catch (IOException e) {
			throw new WebApplicationException(e, 500);
		}
	}



	@GET
	@Path("/images/{image}")
	@Produces("image/*")
	public Response getImage(@PathParam("image") String image) {
		// TODO implement this
		File f = new File(image);
		

		if (!f.exists()) {
			throw new WebApplicationException(404);
		}

		final String mt = new MimetypesFileTypeMap().getContentType(f);
		return Response.ok(f, mt).build();
	}
}
