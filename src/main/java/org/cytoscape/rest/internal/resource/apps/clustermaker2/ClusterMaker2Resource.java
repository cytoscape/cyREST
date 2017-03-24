package org.cytoscape.rest.internal.resource.apps.clustermaker2;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.cytoscape.command.AvailableCommands;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.rest.internal.model.Message;
import org.cytoscape.rest.internal.resource.apps.AppConstants;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskObserver;
import com.google.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags = {AppConstants.APP_TAG, "Clustering"})
@Path(AppConstants.APPS_ROOT + "clustermaker2/")
public class ClusterMaker2Resource  
{
	
	@Inject
	protected CyNetworkManager networkManager;
	
	@Inject
	protected SynchronousTaskManager<?> taskManager;
	
	@Inject
	@NotNull
	private AvailableCommands available;

	@Inject
	@NotNull
	private CommandExecutorTaskFactory ceTaskFactory;
	
	public ClusterMaker2Resource(){
	
	}
	
	class MCODETaskObserver implements TaskObserver {
		
		private String message;
		
		public MCODETaskObserver(){
			message = null;
		}
		
		@Override
		public void allFinished(FinishStatus arg0) {
			message = arg0.getType().toString();
		}

		@Override
		public void taskFinished(ObservableTask arg0) {
			//Unfortunately, clustermaker2's MCODE is not an observableTask, so this won't get called and we cannot access
			//the algorithm's results.
		}
	}
	
	private static final String CLUSTER_ATTRIBUTE_KEY = "clusterAttribute";
	private static final String CLUSTER_ATTRIBUTE_DESCRIPTION = "Cluster attribute name";
	private static final String CREATE_GROUPS_KEY = "createGroups";
	private static final String CREATE_GROUPS_DESCRIPTION = "Create groups (metanodes) with results";
	
	private static final void applyAdvancedProperties(UriInfo info, String defaultClusterAttribute, Map<String, Object> map)
	{
		String clusterAttribute = info.getQueryParameters().getFirst(CLUSTER_ATTRIBUTE_KEY);
		if (clusterAttribute != null){
			map.put(CLUSTER_ATTRIBUTE_KEY, clusterAttribute);
		}
		else{
			map.put(CLUSTER_ATTRIBUTE_KEY, defaultClusterAttribute);
		}
			
		if (info.getQueryParameters().containsKey(CREATE_GROUPS_KEY)){
			Boolean createGroups = new Boolean(info.getQueryParameters().getFirst(CREATE_GROUPS_KEY));
			map.put(CREATE_GROUPS_KEY, createGroups);
		}
	}

	private static final String SHOW_UI_KEY = "showUI";
	private static final String SHOW_UI_DESCRIPTION = "Create new clustered network";
	private static final String RESTORE_EDGES_KEY = "restoreEdges";
	private static final String RESTORE_EDGES_DESCRIPTION = "Restore inter-cluster edges after layout";
	
	private static final void applyNetworkVizProperties(UriInfo info, Map<String, Object> map) {
		if (info.getQueryParameters().containsKey(SHOW_UI_KEY)){
			Boolean showUI = new Boolean(info.getQueryParameters().getFirst(SHOW_UI_KEY));
			map.put(SHOW_UI_KEY, showUI);
		}
		
		if (info.getQueryParameters().containsKey(RESTORE_EDGES_KEY)){
			Boolean restoreEdges = new Boolean(info.getQueryParameters().getFirst(RESTORE_EDGES_KEY));
			map.put(RESTORE_EDGES_KEY, restoreEdges);
		}

	}
	
	private static final String CLUSTERMAKER2_NAMESPACE = "cluster";
	private static final String MCODE_COMMAND = "mcode";
	private static final String MCODE_CLUSTER_ATTRIBUTE = "_mcodeCluster";
	
	@Path("/"+MCODE_COMMAND+"/{networkSUID}")
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@ApiOperation(value = "Execute MCODE Clustering",
    notes = "",
    response = Message.class)
	@ApiImplicitParams({
	    @ApiImplicitParam(name = CLUSTER_ATTRIBUTE_KEY, value = CLUSTER_ATTRIBUTE_DESCRIPTION, required = false, dataType = "string", paramType = "query", defaultValue=MCODE_CLUSTER_ATTRIBUTE),
	    @ApiImplicitParam(name = CREATE_GROUPS_KEY, value = CREATE_GROUPS_DESCRIPTION, required = false, dataType = "boolean", paramType = "query", defaultValue="false"),
	    @ApiImplicitParam(name = SHOW_UI_KEY, value = SHOW_UI_DESCRIPTION, required = false, dataType = "boolean", paramType = "query", defaultValue="false"),
	    @ApiImplicitParam(name = RESTORE_EDGES_KEY, value = RESTORE_EDGES_DESCRIPTION, required = false, dataType = "boolean", paramType = "query", defaultValue="false")})
	public Message cluster(@ApiParam(value = "The SUID of the Network", required = true) @PathParam("networkSUID") long suid, MCODEParameters parameters, @Context UriInfo info){
		
		if (!available.getNamespaces().contains(CLUSTERMAKER2_NAMESPACE) || !available.getCommands(CLUSTERMAKER2_NAMESPACE).contains(MCODE_COMMAND)){
			String messageString = "clusterMaker2 MCODE command is unavailable";
			throw new ServiceUnavailableException(messageString, Response.status(Response.Status.SERVICE_UNAVAILABLE)
					.type(MediaType.APPLICATION_JSON)
					.entity(new Message(messageString)).build());
		}
		
		MCODETaskObserver taskObserver = new MCODETaskObserver();
		
		Map<String, Object> tunableMap = new HashMap<String, Object>();
		
		tunableMap.putAll(parameters.getTunableMap());
		tunableMap.put("network", networkManager.getNetwork(suid));
		
		applyAdvancedProperties(info, MCODE_CLUSTER_ATTRIBUTE, tunableMap);
		applyNetworkVizProperties(info, tunableMap);
		
		TaskIterator taskIterator = ceTaskFactory.createTaskIterator(CLUSTERMAKER2_NAMESPACE,MCODE_COMMAND, tunableMap, taskObserver); 
		taskManager.execute(taskIterator, taskObserver);
		
		return new Message(taskObserver.message); 
	}
	
}
