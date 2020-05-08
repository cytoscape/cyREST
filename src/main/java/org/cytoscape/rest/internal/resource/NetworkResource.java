package org.cytoscape.rest.internal.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.WebApplicationException;

import org.cytoscape.ci.model.CIResponse;
import org.cytoscape.io.read.AbstractCyNetworkReader;
import org.cytoscape.io.read.CyNetworkReader;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.rest.internal.CyRESTConstants;
import org.cytoscape.rest.internal.model.CountModel;
import org.cytoscape.rest.internal.model.EdgeModel;
import org.cytoscape.rest.internal.model.NetworkSUIDModel;
import org.cytoscape.rest.internal.model.NetworkViewSUIDModel;
import org.cytoscape.rest.internal.model.CreatedCyEdgeModel;
import org.cytoscape.rest.internal.model.NodeModel;
import org.cytoscape.rest.internal.model.SUIDNameModel;
import org.cytoscape.rest.internal.task.HeadlessTaskMonitor;
import org.cytoscape.task.AbstractNetworkCollectionTask;
import org.cytoscape.task.select.SelectFirstNeighborsTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static org.cytoscape.rest.internal.resource.NetworkErrorConstants.*;

@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.NETWORKS_TAG})
@Singleton
@Path("/v1/networks")
public class NetworkResource extends AbstractResource {

	
	
	private static final String CX_READER_ID = "cytoscapeCxNetworkReaderFactory";
	private static final String CX_FORMAT = "cx";

	private static final String RESOURCE_URN = "networks";

	@Override
	public String getResourceURI() {
		return RESOURCE_URN;
	}
	
	private final static Logger logger = LoggerFactory.getLogger(NetworkResource.class);
	
	@Override
	public Logger getResourceLogger() {
		return logger;
	}
	

	@Inject
	protected SelectFirstNeighborsTaskFactory selectFirstNeighborsTaskFactory;

	// Preset types
	//	private static final String DEF_COLLECTION_PREFIX = "Created by cyREST: ";

	@Inject
	@NotNull
	private CyLayoutAlgorithmManager layoutManager;

	public NetworkResource() {
		super();
	}

	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get number of networks in current session",
	notes = "Returns the number of networks in current Cytoscape session.",
	response = CountModel.class)
	public Response getNetworkCount() {
		final String result = getNumberObjectString(SERIALIZATION_ERROR, JsonTags.COUNT, networkManager.getNetworkSet().size());
		return Response.ok(result).build();
	}

	private static class NetworkSUIDResponse extends CIResponse<NetworkSUIDModel> {};

	
	@GET
	@Path("/currentNetwork")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get the current network",
	notes = "Returns the current network.",
	response = NetworkSUIDResponse.class)
	public Response getCurrentNetwork() {
		CyNetwork network = applicationManager.getCurrentNetwork();
		if (network == null) {
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NETWORK_NOT_FOUND_ERROR, 
					"No current network available", 
					logger, null);
		}
		
		NetworkSUIDModel entity = new NetworkSUIDModel(network.getSUID());
		return Response.ok(ciResponseFactory.getCIResponse(entity)).build();
	}
	
	@PUT
	@Path("/currentNetwork")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Set the current network",
	notes = "Sets the current network.",
	response = CIResponse.class)
	public Response setCurrentNetwork(@ApiParam(value="SUID of the Network") NetworkSUIDModel networkSUIDModel) {
		
		if (networkSUIDModel == null || networkSUIDModel.networkSUID == null) {
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INVALID_PARAMETER_ERROR, 
					"Missing or invalid message body.", 
					logger, null);
		
		}
		CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkSUIDModel.networkSUID);
		
		applicationManager.setCurrentNetwork(network);
		
		return Response.ok(ciResponseFactory.getCIResponse(new Object())).build();
	}
	
	@PUT
	@Path("/views/currentNetworkView")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Set the current Network View",
	notes = "Sets the current Network View.",
	response = CIResponse.class,
	tags={CyRESTSwagger.CyRESTSwaggerConfig.NETWORK_VIEWS_TAG})
	public Response setCurrentNetworkView(@ApiParam(value="SUID of the Network View") NetworkViewSUIDModel networkViewSUIDModel) {
		
		if (networkViewSUIDModel == null || networkViewSUIDModel.networkViewSUID == null) {
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INVALID_PARAMETER_ERROR, 
					"Missing or invalid message body.", 
					logger, null);
		
		}
		CyNetworkView networkView = null;
		//try {
			Collection<CyNetwork> cyNetworks = networkManager.getNetworkSet();
			if (cyNetworks != null) {
				for (CyNetwork cyNetwork : cyNetworks) {
				final Collection<CyNetworkView> views = networkViewManager.getNetworkViews(cyNetwork);
				for (final CyNetworkView view : views) {
					final Long vid = view.getSUID();
					if (vid.equals(networkViewSUIDModel.networkViewSUID)) {
							networkView = view;
						}
					}
				}
			}
		//} catch (NotFoundException e) {
			/*	throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NOT_FOUND_ERROR, 
					e.getMessage(), 
					logger, e);
		}*/
		if (networkView == null) {
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NETWORK_VIEW_NOT_FOUND_ERROR, 
					"Could not find view matching SUID:" + networkViewSUIDModel.networkViewSUID, 
					logger, null);
		}
		applicationManager.setCurrentNetworkView(networkView);
		
		return Response.ok(ciResponseFactory.getCIResponse(new Object())).build();
	}
	
	private static class NetworkViewSUIDResponse extends CIResponse<NetworkViewSUIDModel> {};
	
	/* The placement of this operation in this class is kind of ugly, but since the network 
	 * views resource requires a network to be selected, this is the easiest way to organize it.
	 */
	@GET
	@Path("/views/currentNetworkView")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get the current Network View",
	notes = "Returns the current Network View.",
	response = NetworkViewSUIDResponse.class,
	tags={CyRESTSwagger.CyRESTSwaggerConfig.NETWORK_VIEWS_TAG})
	public Response getCurrentNetworkView() {
		CyNetworkView network = applicationManager.getCurrentNetworkView();
		if (network == null) {
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					NetworkViewResource.RESOURCE_URN, 
					NetworkViewResource.NETWORK_VIEW_NOT_FOUND_ERROR, 
					"No current network view available", 
					logger, null);
		}
		
		NetworkViewSUIDModel entity = new NetworkViewSUIDModel(network.getSUID());
		return Response.ok(ciResponseFactory.getCIResponse(entity)).build();
	}
	

	@GET
	@Path("/{networkId}/nodes/count")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get number of nodes in the network",
	notes = "Returns the number of nodes in the network specified by the `networkId` parameter.",
	response = CountModel.class)
	public Response getNodeCount(@ApiParam(value="SUID of the network containing the nodes") @PathParam("networkId") Long networkId) {
		final String result = getNumberObjectString(SERIALIZATION_ERROR, JsonTags.COUNT, getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId).getNodeCount());
		return Response.ok(result).build();
	}

	@GET
	@Path("/{networkId}/edges/count")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get number of edges in the network",
	notes = "Returns the number of edges in the network specified by the `networkId` parameter.",
	response = CountModel.class)
	public Response getEdgeCount(@ApiParam(value="SUID of the network containing the edges") @PathParam("networkId") Long networkId) {
		final String result = getNumberObjectString(SERIALIZATION_ERROR, JsonTags.COUNT, getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId).getEdgeCount());
		return Response.ok(result).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@ApiOperation(value="Get SUID list of networks", 
	notes="Returns a list of all networks as SUIDs.\n\n" + NETWORK_QUERY_DESCRIPTION)
	public Collection<Long> getNetworksAsSUID(
			@ApiParam(value=COLUMN_DESCRIPTION, required=false) @QueryParam("column") String column, 
			@ApiParam(value=QUERY_STRING_DESCRIPTION, required=false) @QueryParam("query") String query) {
		Collection<CyNetwork> networks = new HashSet<>();

		if (column == null && query == null) {
			networks = networkManager.getNetworkSet();
		} else {
			try {
				networks = getNetworksByQuery(INVALID_PARAMETER_ERROR, query, column);
			} catch(WebApplicationException e) {
				throw(e);
			} catch (Exception e) {
				//throw getError("Could not get networks.", e, Response.Status.INTERNAL_SERVER_ERROR);
				throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						getResourceURI(), 
						INTERNAL_METHOD_ERROR, 
						"Error executing Network query.", 
						getResourceLogger(), e);
			}
		}

		final Collection<Long> suids = new HashSet<Long>();
		for(final CyNetwork network: networks) {
			suids.add(network.getSUID());
		}

		return suids;
	}

	@GET
	@Path("/{networkId}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@ApiOperation(
			value="Get a network in Cytoscape.js format",
			notes="Returns the Network specified by the `networkId` parameter with all associated tables in "
					+ "[Cytoscape.js]("+ CyRESTConstants.CYTOSCAPE_JS_FILE_FORMAT_LINK +") format"
			)
	public String getNetwork(
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId) {
		return getNetworkString(SERVICE_UNAVAILABLE_ERROR, SERIALIZATION_ERROR, getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId));
	}

	@GET
	@Path("/{networkId}/nodes")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get nodes", notes="Returns a list of all nodes in the network specified by the `networkId` parameter as SUIDs.\n\n" + NODE_QUERY_DESCRIPTION)
	public Collection<Long> getNodes(
			@ApiParam(value="SUID of the network containing the nodes") @PathParam("networkId") Long networkId, 
			@ApiParam(value=COLUMN_DESCRIPTION, required=false) @QueryParam("column") String column,
			@ApiParam(value=QUERY_STRING_DESCRIPTION, required=false) @QueryParam("query") String query) {
		return getByQuery(NETWORK_NOT_FOUND_ERROR, INTERNAL_METHOD_ERROR, INVALID_PARAMETER_ERROR, networkId, "nodes", column, query);
	}

	@GET
	@Path("/{networkId}/nodes/selected")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get selected nodes", notes="Gets the selected nodes in the network specified by the `networkId` parameter. The results are presented as a list of SUIDs.")
	public Collection<Long> getSelectedNodes(
			@ApiParam(value="SUID of the network containing the nodes") @PathParam("networkId") Long networkId
			) {
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		final List<CyNode> selectedNodes = CyTableUtil.getNodesInState(network, CyNetwork.SELECTED, true);
		final List<Long> selectedNodeIds = selectedNodes.stream()
				.map(node -> node.getSUID())
				.collect(Collectors.toList());

		return selectedNodeIds;
	}

	@PUT
	@Path("/{networkId}/nodes/selected")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Set selected nodes", notes="Sets as selected the nodes specified by the `suids` and `networkId` parameters.\n\nReturns a list of selected SUIDs.")
	public Collection<Long> setSelectedNodes(
			@ApiParam(value="SUID of the network containing the nodes") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Array of node SUIDs to select") Collection<Double> suids) {
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		final CyTable table = network.getDefaultNodeTable();

		return setSelected(network, table, suids);
	}

	private Collection<Long> setSelected(CyNetwork network, CyTable table, Collection<Double> suids)
	{
		//Clear selection first.
		for (CyRow row : table.getAllRows()) {
			row.set("selected", false);
		}
		//Select rows
		Set<Long> output = new HashSet<Long>();
		for (Double suid : suids) {
			//Check if edge exists; table side-effect new rows if you try to get a non-existent edge.
			if (table.rowExists(suid.longValue())) {
				CyRow row = table.getRow(suid.longValue());
				row.set("selected", true);
				output.add(suid.longValue());
			}
			else {
				//throw getError("SUID " + suid + " cannot be found in table.", new IllegalArgumentException(), Response.Status.INTERNAL_SERVER_ERROR);
				throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						getResourceURI(), 
						INTERNAL_METHOD_ERROR, 
						"SUID " + suid + " cannot be found in table.", 
						getResourceLogger(), null);
			}
		}
		return output;
	}

	@GET
	@Path("/{networkId}/nodes/selected/neighbors")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get all neighbors of the selected nodes", notes="Returns the neighbors of the nodes currently selected in the network specified by the `networkId` parameter as a list of SUIDs.\n\n"
			+ "Note that this does not include the nodes in the original selection.")
	public Collection<Long> getNeighborsSelected(
			@ApiParam(value="SUID of the network") @PathParam("networkId") Long networkId
			) {
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		final List<CyNode> selectedNodes = CyTableUtil.getNodesInState(network, CyNetwork.SELECTED, true);

		final Set<Long> res = selectedNodes.stream()
				.map(node -> network.getNeighborList(node, Type.ANY))
				.flatMap(List::stream)
				.map(neighbor -> neighbor.getSUID())
				.collect(Collectors.toSet());

		return res;
	}

	@GET
	@Path("/{networkId}/edges/selected")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get selected edges", notes="Gets the selected edges in the network specified by the `networkId` parameter. The results are presented as a list of SUIDs.")
	public Collection<Long> getSelectedEdges(
			@ApiParam(value="SUID of the network containing the edges") @PathParam("networkId") Long networkId) 
	{
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		final List<CyEdge> selectedEdges = CyTableUtil.getEdgesInState(network, CyNetwork.SELECTED, true);
		final List<Long> selectedEdgeIds = selectedEdges.stream()
				.map(edge -> edge.getSUID())
				.collect(Collectors.toList());

		return selectedEdgeIds;
	}

	@PUT
	@Path("/{networkId}/edges/selected")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Set selected edges", notes="Sets as selected the edges specified by the `suids` and `networkId` parameters.\n\nReturns a list of selected SUIDs.")
	public Collection<Long> setSelectedEdges(
			@ApiParam(value="SUID of the network containing the edges") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Array of edge SUIDs to select") Collection<Double> suids) {
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		final CyTable table = network.getDefaultEdgeTable();
		//Clear selection first.
		return setSelected(network, table, suids);
	}

	@GET
	@Path("/{networkId}/edges")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get edges", notes="Returns a list of all edges in the network specified by the `networkId` parameter as SUIDs.\n\n" + EDGE_QUERY_DESCRIPTION)
	public Collection<Long> getEdges(
			@ApiParam(value="SUID of the network containing the edges") @PathParam("networkId") Long networkId, 
			@ApiParam(value=COLUMN_DESCRIPTION, required=false) @QueryParam("column") String column,
			@ApiParam(value=QUERY_STRING_DESCRIPTION, required=false) @QueryParam("query") String query) {
		return getByQuery(NETWORK_NOT_FOUND_ERROR, INTERNAL_METHOD_ERROR, INVALID_PARAMETER_ERROR, networkId, "edges", column, query);
	}


	@GET
	@Path("/{networkId}/nodes/{nodeId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a node", notes="Returns a node with its associated row data.", response=NodeModel.class)
	public String getNode(
			@ApiParam(value="SUID of the network containing the node") @PathParam("networkId") Long networkId, 
			@ApiParam(value="SUID of the node") @PathParam("nodeId") Long nodeId) {
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		final CyNode node = network.getNode(nodeId);
		if (node == null) {
			//throw new NotFoundException("Could not find node with SUID: " + nodeId);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					NODE_NOT_FOUND_ERROR, 
					"Could not find Node with SUID: " + nodeId, 
					getResourceLogger(), null);
		}
		return getGraphObject(SERIALIZATION_ERROR, network, node);
	}

	@GET
	@Path("/{networkId}/edges/{edgeId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get an edge", notes="Returns an edge with its associated row data.", response=EdgeModel.class)
	public String getEdge(
			@ApiParam(value="SUID of the network containing the edge") @PathParam("networkId") Long networkId, 
			@ApiParam(value="SUID of the edge") @PathParam("edgeId") Long edgeId) {
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		final CyEdge edge = network.getEdge(edgeId);
		if (edge == null) {
			//throw new NotFoundException("Could not find edge with SUID: " + edgeId);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					EDGE_NOT_FOUND_ERROR, 
					"Could not find Edge with SUID: " + edgeId, 
					getResourceLogger(), null);
		}
		return getGraphObject(SERIALIZATION_ERROR, network, edge);
	}

	@GET
	@Path("/{networkId}/edges/{edgeId}/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get source/target node of an edge", notes="Returns the SUID of the source or target node of the edge specified by the `edgeId` and `networkId` parameters.\n\nReturn values can be in one of two formats, depending on the value specified in the `type` parameter:\n\n"
			+ "```\n"
			+ "{\n"
			+ "   \"source\": 101\n"
			+ "}\n"
			+ "```\n\n"
			+ "```\n"
			+ "{\n"
			+ "   \"target\": 102\n"
			+ "}\n"
			+ "```\n\n",
			response=Object.class)
	public String getEdgeComponent(
			@ApiParam(value="SUID of the network containing the edge") @PathParam("networkId") Long networkId, 
			@ApiParam(value="SUID of the edge") @PathParam("edgeId") Long edgeId,
			@ApiParam(value="The node type to return", allowableValues="source,target") @PathParam("type") String type) {
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		final CyEdge edge = network.getEdge(edgeId);

		if (edge == null) {
			//throw getError("Could not find edge: " + edgeId, new RuntimeException(), Response.Status.NOT_FOUND);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					EDGE_NOT_FOUND_ERROR, 
					"Could not find Edge. SUID: " + edgeId, 
					getResourceLogger(), null);
		}

		Long nodeSUID = null;
		if (type.equals(JsonTags.SOURCE)) {
			nodeSUID = edge.getSource().getSUID();
		} else if (type.equals(JsonTags.TARGET)) {
			nodeSUID = edge.getTarget().getSUID();
		} else {
			//throw getError("Invalid parameter for edge: " + type, new IllegalArgumentException(), Response.Status.INTERNAL_SERVER_ERROR);
			//The above was incorrectly treating a path param as a query; it should return 404
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					NODE_TYPE_NOT_FOUND_ERROR, 
					"Invalid parameter for edge: " + type, 
					getResourceLogger(), null);
		}
		return getNumberObjectString(SERIALIZATION_ERROR, type, nodeSUID);
	}

	@GET
	@Path("/{networkId}/edges/{edgeId}/isDirected")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get edge directionality",
	notes = "Returns true if the edge specified by the `edgeId` and `networkId` parameters is directed.")
	public Boolean getEdgeDirected(
			@ApiParam(value="SUID of the network containing the edge")@PathParam("networkId") Long networkId, 
			@ApiParam("SUID of the edge") @PathParam("edgeId") Long edgeId) {
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		CyEdge edge = network.getEdge(edgeId);
		if (edge == null) {
			//throw getError("Could not find edge: " + edgeId, new RuntimeException(), Response.Status.NOT_FOUND);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					EDGE_NOT_FOUND_ERROR, 
					"Could not find Edge. SUID: " + edgeId, 
					getResourceLogger(), null);
		}
		return edge.isDirected();
	}

	@GET
	@Path("/{networkId}/nodes/{nodeId}/adjEdges")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get adjacent edges for a node",
	notes = "Returns a list of connected edges as SUIDs for the node specified by the `nodeId` and `networkId` parameters.")
	public Collection<Long> getAdjEdges(@ApiParam(value="SUID of the network containing the node")@PathParam("networkId") Long networkId, @ApiParam(value="SUID of the node")@PathParam("nodeId") Long nodeId) {
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		final CyNode node = getNode(NODE_NOT_FOUND_ERROR, network, nodeId);
		final List<CyEdge> edges = network.getAdjacentEdgeList(node, Type.ANY);
		return getGraphObjectArray(edges);
	}

	@GET
	@Path("/{networkId}/nodes/{nodeId}/pointer")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get network pointer (nested network SUID)",
	notes = "If the node specified by the `nodeId` and `networkId` parameters has an associated nested network, returns the SUID of the nested network.",
	response = NetworkSUIDModel.class)
	public NetworkSUIDModel getNetworkPointer(
			@ApiParam("SUID of the network containing the node") @PathParam("networkId") Long networkId, 
			@ApiParam("SUID of the node") @PathParam("nodeId") Long nodeId) {
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		final CyNode node = getNode(NODE_NOT_FOUND_ERROR, network, nodeId);
		final CyNetwork pointer = node.getNetworkPointer();
		if (pointer == null) {
			//throw getError("Could not find network pointer.", new RuntimeException(), Response.Status.NOT_FOUND);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					NETWORK_POINTER_NOT_FOUND_ERROR, 
					"Could not find Network pointer", 
					getResourceLogger(), null);
		}

		return new NetworkSUIDModel(pointer.getSUID());
	}


	@GET
	@Path("/{networkId}/nodes/{nodeId}/neighbors")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get neighbors of the node",
	notes = "Returns the neighbors of the node specified by the `nodeId` and `networkId` parameters as a list of SUIDs.")
	public Collection<Long> getNeighbours(
			@ApiParam(value="SUID of the network containing the node.") @PathParam("networkId") Long networkId, 
			@ApiParam("SUID of the node")@PathParam("nodeId") Long nodeId) {
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		final CyNode node = getNode(NODE_NOT_FOUND_ERROR, network, nodeId);
		final List<CyNode> nodes = network.getNeighborList(node, Type.ANY);

		return getGraphObjectArray(nodes);
	}

	/**
	 * 
	 * Get SUIDs for given collection of graph objects.
	 * 
	 * @param objects
	 * @return
	 */
	private final Collection<Long> getGraphObjectArray(final Collection<? extends CyIdentifiable> objects) {
		return objects.stream()
				.map(CyIdentifiable::getSUID)
				.collect(Collectors.toList());
	}

	@POST
	@Path("/{networkId}/nodes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Add node(s) to a network",
	notes="Adds new nodes to the network specified by the `networkId` parameter. The `name` column will be populated by the contents of the message body.")
	@ApiImplicitParams(
			@ApiImplicitParam(value="Array of new node names", dataType="[Ljava.lang.String;", paramType="body", required=true)
			)
	@ApiResponses ( value= {
			@ApiResponse(code=201, message="", response=SUIDNameModel.class, responseContainer="List") ,
			@ApiResponse(code=412, message="") }
			)
	public Response createNode(
			@ApiParam(value="SUID of the network containing the node.") @PathParam("networkId") Long networkId, 
			@ApiParam(hidden=true) final InputStream is) {
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		final ObjectMapper objMapper = new ObjectMapper();
		JsonNode rootNode = null;
		try {
			rootNode = objMapper.readValue(is, JsonNode.class);
		} catch (IOException e) {
			//throw getError("Could not JSON root node.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					INVALID_PARAMETER_ERROR, 
					"Could not parse input JSON", 
					getResourceLogger(), e);
		}

		// Single or multiple
		if (rootNode.isArray()) {
			final JsonFactory factory = new JsonFactory();

			String result = null;
			try {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				JsonGenerator generator = null;
				generator = factory.createGenerator(stream);
				generator.writeStartArray();
				for (final JsonNode node : rootNode) {
					final String nodeName = node.textValue();
					final CyNode newNode = network.addNode();
					network.getRow(newNode).set(CyNetwork.NAME, nodeName);
					generator.writeStartObject();
					generator.writeStringField(CyNetwork.NAME, nodeName);
					generator.writeNumberField(CyIdentifiable.SUID, newNode.getSUID());
					generator.writeEndObject();
				}
				generator.writeEndArray();
				generator.close();
				result = stream.toString("UTF-8");
				stream.close();
				updateViews(network);
			} catch (IOException e) {
				//throw getError("Could not create node list.", e, Response.Status.INTERNAL_SERVER_ERROR);
				throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						getResourceURI(), 
						SERIALIZATION_ERROR, 
						"Could not create node list.", 
						getResourceLogger(), e);
			}

			return Response.status(Response.Status.CREATED).entity(result).build();
		} else {
			//throw getError("Need to post as array.", new IllegalArgumentException(),
			//		Response.Status.PRECONDITION_FAILED);
			throw this.getCIWebApplicationException(Status.PRECONDITION_FAILED.getStatusCode(), 
					getResourceURI(), 
					INVALID_PARAMETER_ERROR, 
					"Message body was not an array.", 
					getResourceLogger(), null);
		}
	}

	@POST
	@Path("/{networkId}/edges")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Add edge(s) to existing network",
	notes="Add new edge(s) to the network.  Body should include an array of new node names.\n\n"
			+ "Returns and array of objects with fields itentifying the SUIDs of the new edges along with source and target SUIDs.",
			response=CreatedCyEdgeModel.class, 
			responseContainer="List")
	@ApiImplicitParams(
			@ApiImplicitParam(value="Array of new edges", dataType="[Lorg.cytoscape.rest.internal.model.CreateCyEdgeParameterModel;", paramType="body", required=true)
			)
	public Response createEdge(
			@ApiParam(value="SUID of the network to add edges to.") @PathParam("networkId") Long networkId,
			@ApiParam(hidden=true) final InputStream is
			) {
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		final ObjectMapper objMapper = new ObjectMapper();

		JsonNode rootNode = null;
		try {
			rootNode = objMapper.readValue(is, JsonNode.class);
		} catch (IOException e) {
			//throw getError("Could not find root node in the given JSON..", e, Response.Status.PRECONDITION_FAILED);
			throw this.getCIWebApplicationException(Status.PRECONDITION_FAILED.getStatusCode(), 
					getResourceURI(), 
					INVALID_PARAMETER_ERROR, 
					"Could not find root node in the given JSON.", 
					getResourceLogger(), e);
		}

		// Single or multiple
		if (rootNode.isArray()) {
			final JsonFactory factory = new JsonFactory();

			String result = null;
			try {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				JsonGenerator generator = null;
				generator = factory.createGenerator(stream);
				generator.writeStartArray();
				for (final JsonNode node : rootNode) {
					JsonNode source = node.get(JsonTags.SOURCE);
					JsonNode target = node.get(JsonTags.TARGET);
					JsonNode interaction = node.get(CyEdge.INTERACTION);
					JsonNode isDirected = node.get(JsonTags.DIRECTED);
					if (source == null || target == null) {
						continue;
					}

					final Long sourceSUID = source.asLong();
					final Long targetSUID = target.asLong();
					final CyNode sourceNode = network.getNode(sourceSUID);
					final CyNode targetNode = network.getNode(targetSUID);

					final CyEdge edge;
					if (isDirected != null) {
						edge = network.addEdge(sourceNode, targetNode, isDirected.asBoolean());
					} else {
						edge = network.addEdge(sourceNode, targetNode, true);
					}

					final String sourceName = network.getRow(sourceNode).get(CyNetwork.NAME, String.class);
					final String targetName = network.getRow(targetNode).get(CyNetwork.NAME, String.class);

					final String interactionString;
					if (interaction != null) {
						interactionString = interaction.textValue();
					} else {
						interactionString = "-";
					}

					network.getRow(edge).set(CyEdge.INTERACTION, interactionString);
					network.getRow(edge).set(CyNetwork.NAME, sourceName + " (" + interactionString + ") " + targetName);

					generator.writeStartObject();
					generator.writeNumberField(CyIdentifiable.SUID, edge.getSUID());
					generator.writeNumberField(JsonTags.SOURCE, sourceSUID);
					generator.writeNumberField(JsonTags.TARGET, targetSUID);
					generator.writeEndObject();
				}
				generator.writeEndArray();
				generator.close();
				result = stream.toString("UTF-8");
				stream.close();
				updateViews(network);
			} catch (Exception e) {
				e.printStackTrace();
				//throw getError("Could not create edge.", e, Response.Status.INTERNAL_SERVER_ERROR);
				throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						getResourceURI(), 
						INTERNAL_METHOD_ERROR, 
						"Could not create edge.", 
						getResourceLogger(), e);
			}
			return Response.status(Response.Status.CREATED).entity(result).build();
		} else {
			//throw getError("Need to POST as array.", new IllegalArgumentException(),
			//		Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					INVALID_PARAMETER_ERROR, 
					"Need to POST as array.", 
					getResourceLogger(), null);
		}
	}

	// //////////////// Delete //////////////////////////////////

	@DELETE
	@ApiOperation(value="Delete all networks in current session", notes="Delete all networks in the current session.")
	public Response deleteAllNetworks() {
		this.networkManager.getNetworkSet().stream()
		.forEach(network->this.networkManager.destroyNetwork(network));

		return Response.ok().build();
	}

	@DELETE
	@Path("/{networkId}")
	@ApiOperation(value="Delete a network", notes="Deletes the network specified by the `networkId` parameter.")
	public Response deleteNetwork(
			@ApiParam(value="SUID of the network to delete") @PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		this.networkManager.destroyNetwork(network);
		return Response.ok().build();
	}

	@DELETE
	@Path("/{networkId}/nodes")
	@ApiOperation(value="Delete all nodes in a network", notes="Delete all the nodes from the network specified by the `networkId` parameter.")
	public Response deleteAllNodes(
			@ApiParam(value="SUID of the network to delete nodes from") @PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		network.removeNodes(network.getNodeList());
		updateViews(network);
		return Response.ok().build();
	}

	@DELETE
	@Path("/{networkId}/edges")
	@ApiOperation(value="Delete all edges in a network", notes="Delete all the edges from the network specified by the `networkId` parameter.")
	public Response deleteAllEdges(
			@ApiParam(value="SUID of the network to delete edges from") @PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		network.removeEdges(network.getEdgeList());
		updateViews(network);

		return Response.ok().build();
	}


	@DELETE
	@Path("/{networkId}/nodes/{nodeId}")
	@ApiOperation(value="Delete a node in the network", notes="Deletes the node specified by the `nodeId` and `networkId` parameters.")
	public Response deleteNode(
			@ApiParam(value="SUID of the network containing the node.") @PathParam("networkId") Long networkId, 
			@ApiParam(value="SUID of the node") @PathParam("nodeId") Long nodeId) {
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		final CyNode node = network.getNode(nodeId);
		if (node == null) {
			//throw new NotFoundException("Node does not exist.");
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					NODE_NOT_FOUND_ERROR, 
					"Could not find Node with SUID: " + nodeId, 
					getResourceLogger(), null);
		}
		final List<CyNode> nodes = new ArrayList<CyNode>();
		nodes.add(node);
		network.removeNodes(nodes);
		updateViews(network);

		return Response.ok().build();
	}

	@DELETE
	@Path("/{networkId}/edges/{edgeId}")
	@ApiOperation(value="Delete an edge in the network.", notes="Deletes the edge specified by the `edgeId` and `networkId` parameters.")
	public Response deleteEdge(
			@ApiParam(value="SUID of the network containing the edge.") @PathParam("networkId") Long networkId, 
			@ApiParam(value="SUID of the edge") @PathParam("edgeId") Long edgeId) {
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		final CyEdge edge = network.getEdge(edgeId);
		if (edge == null) {
			//throw new NotFoundException("Edge does not exist.");
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					EDGE_NOT_FOUND_ERROR, 
					"Could not find Edge with SUID: " + edgeId, 
					getResourceLogger(), null);
		}
		final List<CyEdge> edges = new ArrayList<CyEdge>();
		edges.add(edge);
		network.removeEdges(edges);
		updateViews(network);

		return Response.ok().build();
	}

	/**
	 * Update view of each network
	 * 
	 * @param network
	 */
	private final void updateViews(final CyNetwork network) {
		final Collection<CyNetworkView> views = networkViewManager.getNetworkViews(network);
		for (final CyNetworkView view : views) {
			view.updateView();
		}
	}

	// ///////////////////// Object Creation ////////////////////

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Create a new network from a file or URL",
		notes="Creates a new network in the current session from a file or URL source.\n\n"
			+ "Depending on the setting of the `format` parameter the source can be in one of several formats:\n\n"
			+ "| Format   | Details    |\n"
			+ "| -------- | -------    |\n"
			+ "| edgeList | [SIF]("+CyRESTConstants.SIF_FILE_FORMAT_LINK+") format |\n"
			+ "| cx       | [CX]("+CyRESTConstants.CX_FILE_FORMAT_LINK+") format |\n"
			+ "| json     | [Cytoscape.js]("+CyRESTConstants.CYTOSCAPE_JS_FILE_FORMAT_LINK+") format |\n"
			+ "If the `source` parameter is left unspecified, the message body should contain data in the format specified by the `format` parameter.\n\n"
			+ "\n"
			+ "If the `source` parameter is specified as \"url\", the message body should be a list of URLs, formatted as below:\n\n"
			+ "```\n"
			+ "[\n"
			+ "  {\n"
			+ "    \"source_location\": \"http://somewhere.com/graph.js\",\n" 
			+ "	   \"source_method\": \"GET\",\n" 
			+ "	   \"ndex_uuid\": \"12345\"\n"
			+ "  }\n"
			+ "  ...\n"
			+ "]\n"
			+ "```\n"
			+ "The `source_location` field specifies the URL from which to get data, and the `source_method` field specifies the HTTP method to use. All entries should be in the format specified by the `format` parameter. All the fields in each entry will be copied to columns in the default network table row for the new network.",
		response=NetworkSUIDModel.class)
	public String createNetwork(
			@ApiParam(value="The name of the network collection to add new networks to. If the collection does not exist, it will be created.") @QueryParam("collection") String collection,
			@ApiParam(value="Set this to `url` to treat the message body as a list of urls.", allowableValues="url,", required=false) @QueryParam("source") String source, 
			@ApiParam(value="The format of the source data." , allowableValues="edgelist,json,cx") @QueryParam("format") String format, 
			@ApiParam(value="Name of the new network. This is only used if the network name cannot be set directly in source data.") @QueryParam("title") String title,
			@ApiParam(value="Source data. This is either the data to be loaded, or a list of URLs from which to load data." ) final InputStream is,
			@Context HttpHeaders headers) {

		applicationManager.setCurrentNetworkView(null);
		applicationManager.setCurrentNetwork(null);

		// 1. If source is URL, load from the array of URL
		if (source != null && source.equals(JsonTags.URL)) {
			try {
				return loadNetworks(format, collection, is);
			} catch (Exception e) {

				//throw getError("Could not load networks from given locations.", e,
				//		Response.Status.INTERNAL_SERVER_ERROR);
				throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						getResourceURI(), 
						URL_ERROR, 
						"Could not load networks from given locations.", 
						getResourceLogger(), e);
			}
		}

		// Check user agent if available
		final List<String> agent = headers.getRequestHeader("user-agent");
		String userAgent = "";
		if (agent != null) {
			userAgent = agent.get(0);
		}

		String collectionName = null;
		if (collection != null) {
			collectionName = collection;
		}

		final TaskIterator it;
		if (format != null && format.trim().equals(JsonTags.FORMAT_EDGELIST)) {
			it = edgeListReaderFactory.createTaskIterator(is, collection);
		} else if (format != null && format.equalsIgnoreCase(CX_FORMAT)) {
			// Special case: load as CX
			InputStreamTaskFactory readerFactory = tfManager.getInputStreamTaskFactory(CX_READER_ID);
			it = readerFactory.createTaskIterator(is, collection);
			
		} else {
			
			InputStreamTaskFactory cytoscapeJsReaderFactory = (InputStreamTaskFactory) this.cytoscapeJsReaderFactory.getService();
			if (cytoscapeJsReaderFactory == null)
			{
				//throw getError("Cytoscape js reader factory is unavailable.", new IllegalStateException(), Response.Status.SERVICE_UNAVAILABLE);
				throw this.getCIWebApplicationException(Status.SERVICE_UNAVAILABLE.getStatusCode(), 
						getResourceURI(), 
						SERIALIZATION_ERROR, 
						"No Cytoscape js reader available", 
						getResourceLogger(), null);
			}
			it = cytoscapeJsReaderFactory.createTaskIterator(is, collection);
			
			
		}

		final CyNetworkReader reader = (CyNetworkReader) it.next();

		try {
			reader.run(new HeadlessTaskMonitor());
		} catch (Exception e) {
			//throw getError("Could not parse the given network JSON.", e, Response.Status.PRECONDITION_FAILED);
			throw this.getCIWebApplicationException(Status.PRECONDITION_FAILED.getStatusCode(), 
					getResourceURI(), 
					INVALID_PARAMETER_ERROR, 
					"Could not parse the given network JSON: " + e.getMessage(), 
					getResourceLogger(), e);
		}

		final CyNetwork[] networks = reader.getNetworks();
		final CyNetwork newNetwork = networks[0];

		if(title!= null && title.isEmpty() == false) {
			try {
				newNetwork.getTable(CyNetwork.class, CyNetwork.LOCAL_ATTRS).getRow(newNetwork.getSUID()).set(CyNetwork.NAME, title);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		addNetwork(networks, reader, collectionName);

		try {
			is.close();
		} catch (IOException e) {
			//throw getError("Could not close the network input stream.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					INTERNAL_METHOD_ERROR, 
					"Could not close the network input stream.", 
					getResourceLogger(), e);
		}

		// Return SUID-to-Original map
		return getNumberObjectString(SERIALIZATION_ERROR, JsonTags.NETWORK_SUID, newNetwork.getSUID());
	}

	@POST
	@Path("/{networkId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Create a subnetwork from selected nodes and edges",
	notes="Creates new sub-network from current selection, with the name specified by the `title` parameter.\n\nReturns the SUID of the new sub-network.",
			response=NetworkSUIDModel.class
			)
	public String createNetworkFromSelected(
			@ApiParam(value="SUID of the network containing the selected nodes and edges") @PathParam("networkId") Long networkId,
			@ApiParam(value="Name for the new sub-network") @QueryParam("title") String title,
			@ApiParam(hidden=true) final InputStream is, //This isn't actually used anywhere in the code. -dotasek
			@Context HttpHeaders headers) {

		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		final TaskIterator itr = newNetworkSelectedNodesAndEdgesTaskFactory.createTaskIterator(network);

		// TODO: This is very hackey... We need a method to get the new network
		// SUID.
		AbstractNetworkCollectionTask viewTask = null;

		while (itr.hasNext()) {
			final Task task = itr.next();
			try {
				task.run(new HeadlessTaskMonitor());
				if (task instanceof AbstractNetworkCollectionTask) {
					viewTask = (AbstractNetworkCollectionTask) task;
				} else {
				}
			} catch (Exception e) {
				//throw getError("Could not create sub network from selection.", e, Response.Status.INTERNAL_SERVER_ERROR);
				throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						getResourceURI(), 
						INTERNAL_METHOD_ERROR, 
						"Could not create sub network from selection.", 
						getResourceLogger(), e);
			}
		}
		try {
			is.close();
		} catch (IOException e) {
			//throw getError("Could not close the stream.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					SERIALIZATION_ERROR, 
					"Could not close input stream.", 
					getResourceLogger(), e);
		}

		if (viewTask != null) {
			try {
				Method method = viewTask.getClass().getMethod("getResults", new Class[]{Class.class});
				
				final Collection<?> result = (Collection<?>) method.invoke(viewTask, Collection.class);
				if (result.size() == 1) {
					final CyNetwork newSubNetwork = ((CyNetworkView) result.iterator().next()).getModel();
					final Long suid = newSubNetwork.getSUID();
					if(title != null) {
						newSubNetwork.getRow(newSubNetwork).set(CyNetwork.NAME, title);
					}
					return getNumberObjectString(SERIALIZATION_ERROR, JsonTags.NETWORK_SUID, suid);
				} else {
					//throw getError("viewTask returned no networks.", new IllegalStateException(), Response.Status.INTERNAL_SERVER_ERROR);
					throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
							getResourceURI(), 
							INTERNAL_METHOD_ERROR, 
							"viewTask returned no networks.", 
							getResourceLogger(), null);
				}
			} catch (NoSuchMethodException e) {
				//throw getError("no method 'getResults(Class)' in viewTask.", new IllegalStateException(), Response.Status.INTERNAL_SERVER_ERROR);
				throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						getResourceURI(), 
						INTERNAL_METHOD_ERROR, 
						"no method 'getResults(Class)' in viewTask.", 
						getResourceLogger(), e);
			} catch (SecurityException e) {
				//throw getError("security exeception accessing 'getResults(Class)' in viewTask.", new IllegalStateException(), Response.Status.INTERNAL_SERVER_ERROR);
				throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						getResourceURI(), 
						INTERNAL_METHOD_ERROR, 
						"security exeception accessing 'getResults(Class)' in viewTask.", 
						getResourceLogger(), e);
			} catch (IllegalAccessException e) {
				//throw getError("IllegalAccessException accessing 'getResults(Class)' in viewTask.", new IllegalStateException(), Response.Status.INTERNAL_SERVER_ERROR);
				throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						getResourceURI(), 
						INTERNAL_METHOD_ERROR, 
						"IllegalAccessException accessing 'getResults(Class)' in viewTask", 
						getResourceLogger(), e);
			} catch (IllegalArgumentException e) {
				//throw getError("IllegalArgumentException accessing 'getResults(Class)' in viewTask.", new IllegalStateException(), Response.Status.INTERNAL_SERVER_ERROR);
				throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						getResourceURI(), 
						INTERNAL_METHOD_ERROR, 
						"IllegalArgumentException accessing 'getResults(Class)' in viewTask", 
						getResourceLogger(), e);
			} catch (InvocationTargetException e) {
				//throw getError("InvocationTargetException accessing 'getResults(Class)' in viewTask.", new IllegalStateException(), Response.Status.INTERNAL_SERVER_ERROR);
				throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						getResourceURI(), 
						INTERNAL_METHOD_ERROR, 
						"InvocationTargetException accessing 'getResults(Class)' in viewTask.", 
						getResourceLogger(), e);
			}
		} else {
			System.err.println("viewTask was null");
		}

		//throw getError("Could not get new network SUID.", new IllegalStateException(), Response.Status.INTERNAL_SERVER_ERROR);
		throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
				getResourceURI(), 
				INTERNAL_METHOD_ERROR, 
				"Could not get new network SUID", 
				getResourceLogger(), null);
	}

	private final Map<String, Map<String, Object>> getNetworkProps(JsonNode root) {

		final Map<String, Map<String, Object>> result=new HashMap<>();

		for (final JsonNode node : root) {
			String sourceUrl = null; 
			if(node.isObject()) {
				Iterator<String> names = node.fieldNames();
				final Map<String, Object> networkPropMap = new HashMap<>();
				while(names.hasNext()) {
					final String name = names.next();
					if(name.equals("source_location")) {
						sourceUrl = node.get(name).asText();
					}
					networkPropMap.put(name, node.get(name).asText());
				}
				if(sourceUrl == null) {
					throw new IllegalArgumentException("Missing source");
				} else {
					result.put(sourceUrl, networkPropMap);
				}
			} else {
				sourceUrl = node.asText();
				result.put(sourceUrl, null);
			}
		}
		return result;
	}

	private final String loadNetworks(final String format, final String collection, final InputStream is)
			throws Exception {
		applicationManager.setCurrentNetworkView(null);
		applicationManager.setCurrentNetwork(null);

		String collectionName = collection;

		final ObjectMapper objMapper = new ObjectMapper();
		final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
		final Map<String, Map<String, Object>> netProps = getNetworkProps(rootNode);
		final Map<String, Long[]> results = new HashMap<String, Long[]>();
		final Map<Long, CyRootNetwork> sub2roots = new HashMap<>();

		// Input should be array of URLs.
		final Map<CyNetworkView, VisualStyle> styleMap = new HashMap<>();

		for (final String url : netProps.keySet()) {
			final Map<CyNetworkView, VisualStyle> subMap = loadNetworkFromUrl(format, url, collectionName, results, netProps, sub2roots);
			styleMap.putAll(subMap);
		}
		is.close();

		// Apply correct styles.
		styleMap.keySet().stream()
		.forEach(view->postProcess(view, styleMap.get(view)));
		return generateNetworkLoadResults(results);
	}

	private final Map<CyNetworkView, VisualStyle> loadNetworkFromUrl(final String format, 
			final String sourceUrl, final String collectionName,
			final Map<String, Long[]> results, 
			final Map<String, Map<String, Object>> netProps, 
			final Map<Long, CyRootNetwork> sub2roots) throws IOException {

		//System.out.println("******* Loading: " + sourceUrl);

		final List<CyNetwork> cxNetworks = new ArrayList<>();

		TaskIterator itr = null;
		if (format != null && format.equalsIgnoreCase(CX_FORMAT)) {
			// Special case: load as CX
			InputStreamTaskFactory readerFactory = tfManager.getInputStreamTaskFactory(CX_READER_ID);
			final URL source = new URL(sourceUrl);
			itr = readerFactory.createTaskIterator(source.openStream(), "cx file");
		} else {
			itr = loadNetworkURLTaskFactory.loadCyNetworks(new URL(sourceUrl));
		}

		CyNetworkReader reader = null;

		while (itr.hasNext()) {
			final Task task = itr.next();
			try {
				if (task instanceof CyNetworkReader) {
					reader = (CyNetworkReader) task;
					if(reader instanceof AbstractCyNetworkReader) {
						// Set root name
						AbstractCyNetworkReader ar = (AbstractCyNetworkReader) reader;
						final List<String> rootNames = ar.getRootNetworkList().getPossibleValues();
						if(collectionName != null && rootNames.contains(collectionName)) {
							ar.getRootNetworkList().setSelectedValue(collectionName);
						}
					}
					reader.run(new HeadlessTaskMonitor());
				} else {
					//System.out.println("\n\n******************* EXTRA TASK*********** " + task.toString());
					task.run(new HeadlessTaskMonitor());
				}
			} catch (Exception e) {
				throw new IOException("Could not execute network reader.", e);
			}
		}

		// Extract results
		final CyNetwork[] networks = reader.getNetworks();
		Map<CyNetworkView, VisualStyle> styleMap = new HashMap<CyNetworkView, VisualStyle>();
		if(networks == null || networks.length == 0) {
			return styleMap;
		}

		cxNetworks.addAll(Arrays.asList(networks));

		final Long[] suids = new Long[networks.length];
		int counter = 0;
		for (final CyNetwork network : networks) {
			sub2roots.put(network.getSUID(), cyRootNetworkManager.getRootNetwork(network));

			//System.out.println("******!! Network: " + network.getRow(network).get(CyNetwork.NAME, String.class));

			suids[counter] = network.getSUID();

			// Add props
			final Map<String, Object> propMap = netProps.get(sourceUrl);

			if (propMap != null) {
				propMap.keySet().stream().forEach(key -> setNetworkProps(key, propMap.get(key), network));
			} else {
				setNetworkProps("source_location", sourceUrl, network);
			}
			counter++;
		}
		results.put(sourceUrl, suids);

		// Special case: CX
		if (format != null && format.equalsIgnoreCase(CX_FORMAT)) {
			final CyRootNetwork rootNetwork = ((CySubNetwork) cxNetworks.get(0)).getRootNetwork();
			final String cxCollectionName = rootNetwork.getRow(rootNetwork).get(CyNetwork.NAME, String.class);

			final CyNetwork[] cxArray = cxNetworks.toArray(new CyNetwork[0]);
			styleMap = addNetwork(cxArray, reader, cxCollectionName, false);
		}

		if(collectionName != null) {
			final CyRootNetwork rootNetwork = ((CySubNetwork) networks[0]).getRootNetwork();
			rootNetwork.getRow(rootNetwork).set(CyNetwork.NAME, collectionName);			
		}

		return styleMap;
	}

	private void setNetworkProps(String key, Object value, CyNetwork network) {
		final CyColumn col = network.getDefaultNetworkTable().getColumn(key);
		if(col == null) {
			network.getDefaultNetworkTable().createColumn(key, String.class, true);
		}

		network.getRow(network).set(key, value.toString());
	}

	private final String generateNetworkLoadResults(final Map<String, Long[]> results) {
		final JsonFactory factory = new JsonFactory();

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = null;
		try {
			generator = factory.createGenerator(stream);

			generator.writeStartArray();

			for (final String url : results.keySet()) {
				generator.writeStartObject();

				generator.writeStringField("source", url);
				generator.writeArrayFieldStart(JsonTags.NETWORK_SUID);
				for (final Long suid : results.get(url)) {
					generator.writeNumber(suid);
				}
				generator.writeEndArray();
				generator.writeEndObject();
			}
			generator.writeEndArray();

			generator.close();
			result = stream.toString("UTF-8");
			stream.close();
		} catch (IOException e) {
			//throw getError("Could not create object count.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					SERIALIZATION_ERROR, 
					"Could not create object count.", 
					getResourceLogger(), e);
		}

		return result;
	}


	private final void addNetwork(final CyNetwork[] networks, final CyNetworkReader reader, final String collectionName) {
		addNetwork(networks, reader, collectionName, true);
	}
	/**
	 * Add network to the manager
	 * 
	 * @param networks
	 * @param reader
	 * @param collectionName
	 */
	private final Map<CyNetworkView, VisualStyle> addNetwork(final CyNetwork[] networks, final CyNetworkReader reader, final String collectionName,
			final Boolean applyStyle) {

		final List<CyNetworkView> results = new ArrayList<CyNetworkView>();
		final Map<CyNetworkView, VisualStyle> styleMap = new HashMap<>();

		final Set<CyRootNetwork> rootNetworks = new HashSet<CyRootNetwork>();

		for (final CyNetwork net : networkManager.getNetworkSet()) {
			final CyRootNetwork rootNet = cyRootNetworkManager.getRootNetwork(net);
			rootNetworks.add(rootNet);
		}

		for (final CyNetwork network : networks) {

			// Set network name
			String networkName = network.getRow(network).get(CyNetwork.NAME, String.class);
			if (networkName == null || networkName.trim().length() == 0) {
				if (networkName == null)
					networkName = collectionName;

				network.getRow(network).set(CyNetwork.NAME, networkName);
			}
			networkManager.addNetwork(network);

			final int numGraphObjects = network.getNodeCount() + network.getEdgeCount();
			int viewThreshold = 200000;
			if (numGraphObjects < viewThreshold) {

				final CyNetworkView view = reader.buildCyNetworkView(network);
				VisualStyle style = vmm.getVisualStyle(view);
				if (style == null) {
					style = vmm.getDefaultVisualStyle();
				}


				styleMap.put(view, style);
				networkViewManager.addNetworkView(view);
				results.add(view);
			} 

		}

		// If this is a subnetwork, and there is only one subnetwork in the
		// root, check the name of the root network
		// If there is no name yet for the root network, set it the same as its
		// base subnetwork
		if (networks.length == 1) {
			if (networks[0] instanceof CySubNetwork) {
				CySubNetwork subnet = (CySubNetwork) networks[0];
				final CyRootNetwork rootNet = subnet.getRootNetwork();
				String rootNetName = rootNet.getRow(rootNet).get(CyNetwork.NAME, String.class);
				rootNet.getRow(rootNet).set(CyNetwork.NAME, collectionName);
				if (rootNetName == null || rootNetName.trim().length() == 0) {
					// The root network does not have a name yet, set it the same
					// as the base subnetwork
					rootNet.getRow(rootNet).set(CyNetwork.NAME, collectionName);
				}
			}
		}
		return styleMap;
	}

	private final void postProcess(final CyNetworkView view, final VisualStyle style) {
		vmm.setVisualStyle(style, view);
		style.apply(view);
		view.fitContent();
		view.updateView();
	}

}