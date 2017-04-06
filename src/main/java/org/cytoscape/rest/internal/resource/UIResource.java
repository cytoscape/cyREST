package org.cytoscape.rest.internal.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.rest.internal.CyActivator.LevelOfDetails;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import io.swagger.annotations.Api;

@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.USER_INTERFACE_TAG})
@Singleton
@Path("/v1/ui")
public class UIResource extends AbstractResource {

	@Inject
	@NotNull
	protected CySwingApplication desktop;
	
	@Inject
	@NotNull
	protected LevelOfDetails detailsTF;
	
	@Inject
	@NotNull
	private TaskMonitor headlessTaskMonitor;


	/**
	 * 
	 * @summary Get status of Desktop
	 * 
	 * @return An object with isDesktopAvailable field.  
	 * 		This value is true if Cytoscape Desktop is available.
	 * 		And it is false if Cytoscape is running in headless mode (not available yet). 
	 * 
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Map<String, Boolean> getDesktop() {
		final Map<String, Boolean> status = new HashMap<>();
		boolean desktopAvailable = false;
		if(desktop != null) {
			desktopAvailable = true;
		}
		status.put("isDesktopAvailable", desktopAvailable);
		return status;
	}


	/**
	 * 
	 * Switch between full graphics details <---> fast rendering mode.
	 * 
	 * @summary Toggle level of graphics details (LoD).
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
	 * The return value will includes status of all CytoPanels.
	 * Each entry includes:
	 * 
	 * <ul>
	 * 		<li>
	 * 			name: Official name of the CytoPanel:
	 * 			<ul>
	 * 				<li>SOUTH</li>
	 * 				<li>EAST</li>
	 * 				<li>WEST</li>
	 * 				<li>SOUTH_WEST</li>
	 * 			</ul>
	 * 		</li>
	 * 		<li>
	 * 			state: State of the CytoPanel:
	 * 			<ul>
	 * 				<li>FLOAT</li>
	 * 				<li>DOCK</li>
	 * 				<li>HIDE</li>
	 * 			</ul>
	 * 		</li>
	 * </ul>
	 * 
	 * @summary Get status of all CytoPanels 
	 * 
	 * @return Panel status as an array
	 */
	@GET
	@Path("/panels")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public List<Map<String, String>> getAllPanelStatus() {
		try {
		return Arrays.asList(CytoPanelName.values()).stream()
			.map(panelName->desktop.getCytoPanel(panelName))
			.map(panel->getMap(panel))
			.collect(Collectors.toList());
		} catch(Exception ex) {
			throw getError("Could not getpanel status", ex, Status.INTERNAL_SERVER_ERROR);
		}
	}


	private final Map<String, String> getMap(final CytoPanel panel) {
		final Map<String, String> values = new HashMap<>();
		values.put("name", panel.getCytoPanelName().name());
		values.put("state", panel.getState().name());
		return values;
	}
	
	
	/**
	 * 
	 * @summary Get status of a CytoPanel
	 * 
	 * @param panelName official name of the CytroPanel
	 * 
	 * @return Status of the CytoPanel (name-state pair)
	 */
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


	/**
	 * 
	 * You can update multiple panel states at once.
	 * Body of your request should have same format as the return value of GET method.
	 * 
	 * @summary Update CytoPanel states
	 * 
	 * @return Response 200 if success
	 */
	@PUT
	@Path("/panels")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePanelStatus(final InputStream is) {
		
		final ObjectMapper objMapper = new ObjectMapper();

		JsonNode rootNode = null;
		try {
			rootNode = objMapper.readValue(is, JsonNode.class);
		} catch (IOException e) {
			throw new InternalServerErrorException("Could not parse input JSON.", e);
		}
		
		for (final JsonNode entry : rootNode) {
			final JsonNode panelName = entry.get("name");
			final JsonNode panelStatus = entry.get("state");
			
			if(panelName == null || panelStatus == null) {
				throw new IllegalArgumentException("Imput parameters are missing."); 
			}
			
			final CytoPanelName panel = CytoPanelName.valueOf(panelName.asText());
			if(panel == null) {
				throw new IllegalArgumentException("Could not find panel: " + panelName.asText()); 
			}
			
			final CytoPanelState state = CytoPanelState.valueOf(panelStatus.asText());
			if(state == null) {
				throw new IllegalArgumentException("Invalid Panel State: " + panelStatus.asText()); 
			}
			
			final CytoPanel panelObject = desktop.getCytoPanel(panel);
			panelObject.setState(state);
		}
		
		return Response.ok().build();
	}
}