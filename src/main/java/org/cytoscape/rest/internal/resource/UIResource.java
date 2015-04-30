package org.cytoscape.rest.internal.resource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelName;


@Singleton
@Path("/v1/ui")
public class UIResource extends AbstractResource {

	
	@Context
	@NotNull
	protected CySwingApplication desktop;


	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getDesktop() {
		return "{\"message\": \"Cytoscape Desktop is available.\"}";
	}

	/**
	 * 
	 * @summary Get panel status 
	 * 
	 * @return Summary of server status
	 */
	@GET
	@Path("/panels")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Map<String, String> getPanelStatus() {
		final Map<String, String> status = new HashMap<>();
		Arrays.asList(CytoPanelName.values()).stream()
			.map(panelName->desktop.getCytoPanel(panelName))
			.forEach(panel->status.put(panel.getCytoPanelName().name(), panel.getState().name()));
		return status;
	}

}
