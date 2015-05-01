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

import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler.Builder;


@Singleton
@Path("/v1/ui")
public class UIResource extends AbstractResource {

	
	@Context
	@NotNull
	protected CySwingApplication desktop;


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
