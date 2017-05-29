package org.cytoscape.rest.internal.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.cytoscape.rest.internal.datamapper.MapperUtil;
import org.cytoscape.rest.internal.model.Count;
import org.cytoscape.rest.internal.model.NetworkSUID;
import org.cytoscape.rest.internal.model.NodeNameSUID;
import org.cytoscape.rest.internal.task.HeadlessTaskMonitor;
import org.cytoscape.task.AbstractNetworkCollectionTask;
import org.cytoscape.task.select.SelectFirstNeighborsTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;

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

@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.NETWORKS_TAG})
@Singleton
@Path("/v1/networks")
public class NetworkResource extends AbstractResource {

	private static final String CX_READER_ID = "cytoscapeCxNetworkReaderFactory";
	private static final String CX_FORMAT = "cx";

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
	response = Count.class)
	public Response getNetworkCount() {
		final String result = getNumberObjectString(JsonTags.COUNT, networkManager.getNetworkSet().size());
		return Response.ok(result).build();
	}


	@GET
	@Path("/{networkId}/nodes/count")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get number of nodes in the network",
	notes = "Returns the number of nodes in the network with given SUID.",
	response = Count.class)
	public Response getNodeCount(@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId) {
		final String result = getNumberObjectString(JsonTags.COUNT, getCyNetwork(networkId).getNodeCount());
		return Response.ok(result).build();
	}

	@GET
	@Path("/{networkId}/edges/count")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get number of edges in the network",
	notes = "Returns the number of edges in the network with given SUID.",
	response = Count.class)
	public Response getEdgeCount(@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId) {
		final String result = getNumberObjectString(JsonTags.COUNT, getCyNetwork(networkId).getEdgeCount());
		return Response.ok(result).build();
	}

	private final Collection<Long> getByQuery(final Long id, final String objType, final String column,
			final String query) {
		final CyNetwork network = getCyNetwork(id);
		CyTable table = null;

		List<? extends CyIdentifiable> graphObjects;
		if (objType.equals("nodes")) {
			table = network.getDefaultNodeTable();
			graphObjects = network.getNodeList();
		} else if (objType.equals("edges")) {
			table = network.getDefaultEdgeTable();
			graphObjects = network.getEdgeList();
		} else {
			throw getError("Invalid graph object type: " + objType, new IllegalArgumentException(),
					Response.Status.INTERNAL_SERVER_ERROR);
		}

		if (query == null && column == null) {
			// Simply return rows
			return graphObjects.stream()
					.map(obj->obj.getSUID())
					.collect(Collectors.toList());
		} else if (query == null || column == null) {
			throw getError("Missing query parameter.", new IllegalArgumentException(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} else {
			Object rawQuery = MapperUtil.getRawValue(query, table.getColumn(column).getType());
			final Collection<CyRow> rows = table.getMatchingRows(column, rawQuery);
			final Set<Long> selectedSuid = rows.stream()
					.map(row->row.get(CyIdentifiable.SUID, Long.class))
					.collect(Collectors.toSet());

			final Set<Long> allSuid = graphObjects.stream()
					.map(obj->obj.getSUID())
					.collect(Collectors.toSet());
			// Return intersection
			allSuid.retainAll(selectedSuid);
			return allSuid;
		}

	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@ApiOperation(value="Get SUID list of networks", 
	notes="Returns a list of matching networks. If a query and column are indicated, a matching network "
			+ "is defined as a network whose network table contains a value matching the query in the "
			+ "given column. If no query and column is given, all network SUIDs are returned.")
	public Collection<Long> getNetworksAsSUID(
			@ApiParam(value="Network table column name to be queried against", required=false) @QueryParam("column") String column, 
			@ApiParam(value="A value to be matched in the given column in the network table.", required=false) @QueryParam("query") String query) {
		Collection<CyNetwork> networks = new HashSet<>();

		if (column == null && query == null) {
			networks = networkManager.getNetworkSet();
		} else {
			if(column == null || column.length() == 0) {
				throw getError("Column name parameter is missing.", new IllegalArgumentException(), Response.Status.INTERNAL_SERVER_ERROR);
			}
			if(query == null || query.length() == 0) {
				throw getError("Query parameter is missing.", new IllegalArgumentException(), Response.Status.INTERNAL_SERVER_ERROR);
			}

			try {
				networks = getNetworksByQuery(query, column);
			} catch(Exception e) {
				throw getError("Could not get networks.", e, Response.Status.INTERNAL_SERVER_ERROR);
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
			notes="Returns Network with all associated tables in "
					+ "[Cytoscape.js](http://cytoscape.github.io/cytoscape.js/) format"
			)
	public String getNetwork(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId) {
		return getNetworkString(getCyNetwork(networkId));
	}

	@GET
	@Path("/{networkId}/nodes")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value="Get matching nodes",
			notes="Returns a list of node SUIDs that match the query string.  If no parameter is given, returns all node SUIDs"
			)
	public Collection<Long> getNodes(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Node table column name to be used for search", required=false) @QueryParam("column") String column,
			@ApiParam(value="Search query string", required=false) @QueryParam("query") String query) {
		return getByQuery(networkId, "nodes", column, query);
	}

	@GET
	@Path("/{networkId}/nodes/selected")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get selected nodes as SUID list")
	public Collection<Long> getSelectedNodes(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId
			) {
		final CyNetwork network = getCyNetwork(networkId);
		final List<CyNode> selectedNodes = CyTableUtil.getNodesInState(network, CyNetwork.SELECTED, true);
		final List<Long> selectedNodeIds = selectedNodes.stream()
				.map(node -> node.getSUID())
				.collect(Collectors.toList());

		return selectedNodeIds;
	}

	@PUT
	@Path("/{networkId}/nodes/selected")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Set selected nodes")
	public Collection<Long> setSelectedNodes(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Array of edge SUIDs") Collection<Double> suids) {
		final CyNetwork network = getCyNetwork(networkId);
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
				throw getError("SUID " + suid + " cannot be found in table.", new IllegalArgumentException(), Response.Status.INTERNAL_SERVER_ERROR);
			}
		}
		return output;
	}

	@GET
	@Path("/{networkId}/nodes/selected/neighbors")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get all neighbors of selected nodes as SUID list", notes="Returns the neighbors of the "
			+ "selected nodes as a list.  Note that this does not includes original nodes")
	public Collection<Long> getNeighborsSelected(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId
			) {
		final CyNetwork network = getCyNetwork(networkId);
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
	@ApiOperation(value="Get all selected edges as SUID list")
	public Collection<Long> getSelectedEdges(
			@ApiParam(value="Network SUID")@PathParam("networkId") Long networkId) 
	{
		final CyNetwork network = getCyNetwork(networkId);
		final List<CyEdge> selectedEdges = CyTableUtil.getEdgesInState(network, CyNetwork.SELECTED, true);
		final List<Long> selectedEdgeIds = selectedEdges.stream()
				.map(edge -> edge.getSUID())
				.collect(Collectors.toList());

		return selectedEdgeIds;
	}

	@PUT
	@Path("/{networkId}/edges/selected")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Set selected edges")
	public Collection<Long> setSelectedEdges(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Array of edge SUIDs") Collection<Double> suids) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = network.getDefaultEdgeTable();
		//Clear selection first.
		return setSelected(network, table, suids);
	}

	@GET
	@Path("/{networkId}/edges")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get matching edges", notes="Returns a list of matched edge SUIDs. "
			+ "If no parameter is given, returns all edge SUIDs.")
	public Collection<Long> getEdges(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Edge table column name to be used for search.", required=false) @QueryParam("column") String column,
			@ApiParam(value="Search query", required=false) @QueryParam("query") String query) {
		return getByQuery(networkId, "edges", column, query);
	}


	@GET
	@Path("/{networkId}/nodes/{nodeId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a node", notes="Returns a node with associated row data.")
	public String getNode(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Node SUID") @PathParam("nodeId") Long nodeId) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyNode node = network.getNode(nodeId);
		if (node == null) {
			throw new NotFoundException("Could not find node with SUID: " + nodeId);
		}
		return getGraphObject(network, node);
	}

	@GET
	@Path("/{networkId}/edges/{edgeId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get an edge", notes="Returns an edge with associated row data.")
	public String getEdge(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Edge SUID") @PathParam("edgeId") Long edgeId) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyEdge edge = network.getEdge(edgeId);
		if (edge == null) {
			throw new NotFoundException("Could not find edge with SUID: " + edgeId);
		}
		return getGraphObject(network, edge);
	}

	@GET
	@Path("/{networkId}/edges/{edgeId}/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get source/target node of an edge", notes="Returns the SUID of the source/target node")
	public String getEdgeComponent(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Edge SUID") @PathParam("edgeId") Long edgeId,
			@ApiParam(value="Node Type", allowableValues="source,target") @PathParam("type") String type) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyEdge edge = network.getEdge(edgeId);

		if (edge == null) {
			throw getError("Could not find edge: " + edgeId, new RuntimeException(), Response.Status.NOT_FOUND);
		}

		Long nodeSUID = null;
		if (type.equals(JsonTags.SOURCE)) {
			nodeSUID = edge.getSource().getSUID();
		} else if (type.equals(JsonTags.TARGET)) {
			nodeSUID = edge.getTarget().getSUID();
		} else {
			throw getError("Invalid parameter for edge: " + type, new IllegalArgumentException(), Response.Status.INTERNAL_SERVER_ERROR);
		}
		return getNumberObjectString(type, nodeSUID);
	}

	@GET
	@Path("/{networkId}/edges/{edgeId}/isDirected")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get edge directionality",
	notes = "Returns true if the edge is directed.")
	public Boolean getEdgeDirected(
			@ApiParam(value="Network SUID")@PathParam("networkId") Long networkId, 
			@ApiParam("Edge SUID") @PathParam("edgeId") Long edgeId) {
		final CyNetwork network = getCyNetwork(networkId);
		CyEdge edge = network.getEdge(edgeId);
		if (edge == null) {
			throw getError("Could not find edge: " + edgeId, new RuntimeException(), Response.Status.NOT_FOUND);
		}
		return edge.isDirected();
	}

	@GET
	@Path("/{networkId}/nodes/{nodeId}/adjEdges")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get adjacent edges for a node",
	notes = "Returns a list of connected edges as SUIDs.")
	public Collection<Long> getAdjEdges(@ApiParam(value="Network SUID")@PathParam("networkId") Long networkId, @ApiParam(value="Node SUID")@PathParam("nodeId") Long nodeId) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyNode node = getNode(network, nodeId);
		final List<CyEdge> edges = network.getAdjacentEdgeList(node, Type.ANY);
		return getGraphObjectArray(edges);
	}

	@GET
	@Path("/{networkId}/nodes/{nodeId}/pointer")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get network pointer (nested network SUID)",
	notes = "Returns the nested network SUID.",
	response = NetworkSUID.class)
	public NetworkSUID getNetworkPointer(
			@ApiParam("Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam("Target Node SUID") @PathParam("nodeId") Long nodeId) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyNode node = getNode(network, nodeId);
		final CyNetwork pointer = node.getNetworkPointer();
		if (pointer == null) {
			throw getError("Could not find network pointer.", new RuntimeException(), Response.Status.NOT_FOUND);
		}

		return new NetworkSUID(pointer.getSUID());
	}


	@GET
	@Path("/{networkId}/nodes/{nodeId}/neighbors")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get first neighbors of the node",
	notes = "Returns the neighbors of the node as a list of SUIDs.")
	public Collection<Long> getNeighbours(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam("Node SUID")@PathParam("nodeId") Long nodeId) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyNode node = getNode(network, nodeId);
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
	@ApiOperation(value="Add node(s) to existing network",
	notes="The \"name\" column will be populated by the contents of the message body")
	@ApiImplicitParams(
			@ApiImplicitParam(value="Array of new Node names", dataType="[Ljava.lang.String;", paramType="body", required=true)
			)
	@ApiResponses ( value= {
			@ApiResponse(code=201, message="", response=NodeNameSUID.class, responseContainer="List") ,
			@ApiResponse(code=412, message="") }
			)
	public Response createNode(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(hidden=true) final InputStream is) {
		final CyNetwork network = getCyNetwork(networkId);
		final ObjectMapper objMapper = new ObjectMapper();
		JsonNode rootNode = null;
		try {
			rootNode = objMapper.readValue(is, JsonNode.class);
		} catch (IOException e) {
			throw getError("Could not JSON root node.", e, Response.Status.INTERNAL_SERVER_ERROR);
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
			} catch (Exception e) {
				throw getError("Could not create node list.", e, Response.Status.INTERNAL_SERVER_ERROR);
			}

			return Response.status(Response.Status.CREATED).entity(result).build();
		} else {
			throw getError("Need to post as array.", new IllegalArgumentException(),
					Response.Status.PRECONDITION_FAILED);
		}
	}

	@POST
	@Path("/{networkId}/edges")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Add edge(s) to existing network",
	notes="Add new edge(s) to the network.  Body should include an array of new node names.\n"
			+ "```\n"
			+"[\n"
			+"  {\n"
			+"    \"source\": SOURCE_NODE_SUID,\n"
			+"    \"target\": TARGET_NODE_SUID,\n"
			+"    \"directed\": (Optional boolean value.  Default is True),\n"
			+"    \"interaction\": (Optional.  Will be used for Interaction column.  Default value is '-')\n"
			+"  }...\n"
			+ "]\n"
			+ "```\n"
			+ "Returns the SUIDs of the new edges with source and target SUIDs."
			)
	public Response createEdge(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, final InputStream is
		) {
		final CyNetwork network = getCyNetwork(networkId);
		final ObjectMapper objMapper = new ObjectMapper();

		JsonNode rootNode = null;
		try {
			rootNode = objMapper.readValue(is, JsonNode.class);
		} catch (IOException e) {
			throw getError("Could not find root node in the given JSON..", e, Response.Status.PRECONDITION_FAILED);
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
				throw getError("Could not create edge.", e, Response.Status.INTERNAL_SERVER_ERROR);
			}
			return Response.status(Response.Status.CREATED).entity(result).build();
		} else {
			throw getError("Need to POST as array.", new IllegalArgumentException(),
					Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	// //////////////// Delete //////////////////////////////////

	@DELETE
	@Path("/")
	@ApiOperation(value="Delete all networks in current session")
	public Response deleteAllNetworks() {
		this.networkManager.getNetworkSet().stream()
		.forEach(network->this.networkManager.destroyNetwork(network));

		return Response.ok().build();
	}

	@DELETE
	@Path("/{networkId}")
	@ApiOperation(value="Delete a network")
	public Response deleteNetwork(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		this.networkManager.destroyNetwork(network);
		return Response.ok().build();
	}

	@DELETE
	@Path("/{networkId}/nodes")
	@ApiOperation(value="Delete all nodes in the network")
	public Response deleteAllNodes(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		network.removeNodes(network.getNodeList());
		updateViews(network);

		return Response.ok().build();
	}

	@DELETE
	@Path("/{networkId}/edges")
	@ApiOperation(value="Delete all edges in the network")
	public Response deleteAllEdges(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		network.removeEdges(network.getEdgeList());
		updateViews(network);

		return Response.ok().build();
	}


	@DELETE
	@Path("/{networkId}/nodes/{nodeId}")
	@ApiOperation(value="Delete a node in the network")
	public Response deleteNode(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Node SUID") @PathParam("nodeId") Long nodeId) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyNode node = network.getNode(nodeId);
		if (node == null) {
			throw new NotFoundException("Node does not exist.");
		}
		final List<CyNode> nodes = new ArrayList<CyNode>();
		nodes.add(node);
		network.removeNodes(nodes);
		updateViews(network);

		return Response.ok().build();
	}

	@DELETE
	@Path("/{networkId}/edges/{edgeId}")
	@ApiOperation(value="Delete an edge in the network.")
	public Response deleteEdge(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Edge SUID") @PathParam("edgeId") Long edgeId) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyEdge edge = network.getEdge(edgeId);
		if (edge == null) {
			throw new NotFoundException("Edge does not exist.");
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
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Create a new network from Cytoscape.js JSON or Edgelist")
	public String createNetwork(
			@ApiParam(value="Name of new network collection") @QueryParam("collection") String collection,
			@ApiParam(value="\"url\"", required=false) @QueryParam("source") String source, 
			@ApiParam(value="format" , allowableValues="edgelist,json") @QueryParam("format") String format, 
			@ApiParam(value="Title of the new network") @QueryParam("title") String title,
			final InputStream is,
			@Context HttpHeaders headers) {

		applicationManager.setCurrentNetworkView(null);
		applicationManager.setCurrentNetwork(null);

		// 1. If source is URL, load from the array of URL
		if (source != null && source.equals(JsonTags.URL)) {
			try {
				return loadNetworks(format, collection, is);
			} catch (Exception e) {

				e.printStackTrace();

				throw getError("Could not load networks from given locations.", e,
						Response.Status.INTERNAL_SERVER_ERROR);
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
		} else {
			InputStreamTaskFactory cytoscapeJsReaderFactory = (InputStreamTaskFactory) this.cytoscapeJsReaderFactory.getService();
			if (cytoscapeJsReaderFactory == null)
			{
				throw getError("Cytoscape js reader factory is unavailable.", new IllegalStateException(), Response.Status.SERVICE_UNAVAILABLE);
			}
			it = cytoscapeJsReaderFactory.createTaskIterator(is, collection);
		}

		final CyNetworkReader reader = (CyNetworkReader) it.next();

		try {
			reader.run(new HeadlessTaskMonitor());
		} catch (Exception e) {
			e.printStackTrace();
			throw getError("Could not parse the given network JSON.", e, Response.Status.PRECONDITION_FAILED);
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
			throw getError("Could not close the network input stream.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}

		// Return SUID-to-Original map
		return getNumberObjectString(JsonTags.NETWORK_SUID, newNetwork.getSUID());
	}

	@POST
	@Path("/{networkId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Create a subnetwork from selected nodes and edges",
			notes="If body is empty, it simply creates new network from current selection. Otherwise, select from the "
					+ "list of SUID.\n\nReturns the SUID of the new Network."
			)
	public String createNetworkFromSelected(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId,
			@ApiParam(value="Title") @QueryParam("title") String title,
			final InputStream is,
			@Context HttpHeaders headers) {

		final CyNetwork network = getCyNetwork(networkId);
		final TaskIterator itr = newNetworkSelectedNodesAndEdgesTaskFactory.createTaskIterator(network);

		// TODO: This is very hackey... We need a method to get the new network
		// SUID.
		AbstractNetworkCollectionTask viewTask = null;

		while (itr.hasNext()) {
			final Task task = itr.next();
			try {
				task.run(new HeadlessTaskMonitor());
				if (task instanceof AbstractNetworkCollectionTask && task instanceof ObservableTask) {
					viewTask = (AbstractNetworkCollectionTask) task;
				} else {
				}
			} catch (Exception e) {
				throw getError("Could not create sub network from selection.", e, Response.Status.INTERNAL_SERVER_ERROR);
			}
		}
		try {
			is.close();
		} catch (IOException e) {
			throw getError("Could not close the stream.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}

		if (viewTask != null) {
			final Collection<?> result = ((ObservableTask) viewTask).getResults(Collection.class);
			if (result.size() == 1) {
				final CyNetwork newSubNetwork = ((CyNetworkView) result.iterator().next()).getModel();
				final Long suid = newSubNetwork.getSUID();
				if(title != null) {
					newSubNetwork.getRow(newSubNetwork).set(CyNetwork.NAME, title);
				}
				return getNumberObjectString(JsonTags.NETWORK_SUID, suid);
			}
		}

		throw getError("Could not get new network SUID.", new IllegalStateException(), Response.Status.INTERNAL_SERVER_ERROR);
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

		System.out.println("******* Loading: " + sourceUrl);

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
					System.out.println("\n\n******************* EXTRA TASK*********** " + task.toString());
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

			System.out.println("******!! Network: " + network.getRow(network).get(CyNetwork.NAME, String.class));

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
			throw getError("Could not create object count.", e, Response.Status.INTERNAL_SERVER_ERROR);
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