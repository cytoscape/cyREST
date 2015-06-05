package org.cytoscape.rest.internal.resource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javassist.NotFoundException;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.rest.internal.CyActivator.LevelOfDetails;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler.Builder;


@Singleton
@Path("/v1/ui")
public class UIResource extends AbstractResource {

	@Context
	@NotNull
	protected CySwingApplication desktop;
	
	@Context
	@NotNull
	protected LevelOfDetails detailsTF;
	
	@Context
	@NotNull
	private TaskMonitor headlessTaskMonitor;


	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Map<String, String> getDesktop() {
		final Map<String, String> status = new HashMap<>();
		status.put("message", "GUI is available");
		return status;
	}
	

	/**
	 * 
	 * @summary Update graphics level of details.
	 * 
	 * @return Success message.
	 */
	@PUT
	@Path("/lod")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response updateLodState() {
		final TaskIterator lod = detailsTF.getLodTF().createTaskIterator(null);
		
		try {
			lod.next().run(headlessTaskMonitor);
		} catch (Exception e) {
			throw getError("Could not toggle LOD.", e,
					Response.Status.INTERNAL_SERVER_ERROR);
		}

		return Response.status(Response.Status.OK)
				.type(MediaType.APPLICATION_JSON)
				.entity("{\"message\":\"Toggled Graphics level of details.\"}").build();
	}

	/**
	 * 
	 * @summary Get panel status 
	 * 
	 * @return Panel status
	 */
	@GET
	@Path("/panels")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public List<Map<String, String>> getAllPanelStatus() {
		return Arrays.asList(CytoPanelName.values()).stream()
			.map(panelName->desktop.getCytoPanel(panelName))
			.map(panel->getMap(panel)).collect(Collectors.toList());
	}


	private final Map<String, String> getMap(final CytoPanel panel) {
		final Map<String, String> values = new HashMap<>();
		values.put("name", panel.getCytoPanelName().name());
		values.put("state", panel.getState().name());
		return values;
	}
	
	
	@GET
	@Path("/panels/{panelName}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getPanelStatus(@PathParam("panelName") String panelName) {
		final CytoPanelName panel = CytoPanelName.valueOf(panelName);
		if(panel == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		final CytoPanel panelObject = desktop.getCytoPanel(panel);
		return Response.ok(getMap(panelObject)).build();
	}
}
