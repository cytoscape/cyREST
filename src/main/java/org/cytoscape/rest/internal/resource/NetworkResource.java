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

@Api(tags = {"Networks"})
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

	/**
	 * 
	 * @summary Get number of networks in current session
	 * 
	 * @return Number of networks in current Cytoscape session
	 * 
	 */
	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNetworkCount() {
		final String result = getNumberObjectString(JsonTags.COUNT, networkManager.getNetworkSet().size());
		return Response.ok(result).build();
	}

	/**
	 * 
	 * @summary Get number of nodes in the network
	 * 
	 * @param id
	 *            Network SUID
	 * @return Number of nodes in the network with given SUID
	 */
	@GET
	@Path("/{networkId}/nodes/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNodeCount(@PathParam("networkId") Long networkId) {
		final String result = getNumberObjectString(JsonTags.COUNT, getCyNetwork(networkId).getNodeCount());
		return Response.ok(result).build();
	}

	/**
	 * 
	 * @summary Get number of edges in the network
	 * 
	 * @param networkId
	 *            Network SUID
	 * 
	 * @return number of edges in the network
	 */
	@GET
	@Path("/{networkId}/edges/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEdgeCount(@PathParam("networkId") Long networkId) {
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


	/**
	 * 
	 * Returns list of networks as an array of network SUID.
	 * 
	 * @summary Get SUID list of networks
	 * 
	 * @param column Optional.  Network table column name to be used for search.
	 * @param query Optional.  Search query.
	 * 
	 * @return Matched networks as list of SUIDs.  If no query is given, returns all network SUIDs.
	 * 
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Collection<Long> getNetworksAsSUID(@QueryParam("column") String column, @QueryParam("query") String query) {
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


	/**
	 * 
	 * @summary Get a network in Cytoscape.js format
	 * 
	 * @param networkId
	 *            Network SUID
	 * 
	 * @return Network with all associated tables in Cytoscape.js format.
	 * 
	 */
	@GET
	@Path("/{networkId}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getNetwork(@PathParam("networkId") Long networkId) {
		return getNetworkString(getCyNetwork(networkId));
	}

	/**
	 * @summary Get matching nodes
	 * 
	 * @param networkId Network SUID
	 * @param column Optional.  Node table column name to be used for search.
	 * @param query Optional.  Search query.
	 * 
	 * @return List of matched node SUIDs.  If no parameter is given, returns all node SUIDs.
	 */
	@GET
	@Path("/{networkId}/nodes")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Long> getNodes(@PathParam("networkId") Long networkId, @QueryParam("column") String column,
			@QueryParam("query") String query) {
		return getByQuery(networkId, "nodes", column, query);
	}


	/**
	 * @summary Utility to get selected nodes as SUID list
	 * 
	 * @param networkId Network SUID
	 * 
	 * @return Selected nodes as a list of SUID
	 */
	@GET
	@Path("/{networkId}/nodes/selected")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSelectedNodes(@PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		final List<CyNode> selectedNodes = CyTableUtil.getNodesInState(network, CyNetwork.SELECTED, true);
		final List<Long> selectedNodeIds = selectedNodes.stream()
			.map(node -> node.getSUID())
			.collect(Collectors.toList());
		
		return Response.ok(selectedNodeIds).build();
	}
	
	
	/**
	 * 
	 * The return value does not includes originally selected nodes.
	 * 
	 * @summary Utility to get all neighbors of selected nodes
	 * 
	 * @param networkId Network SUID
	 * 
	 * @return Neighbors as list.  Note that this does not includes original nodes.
	 */
	@GET
	@Path("/{networkId}/nodes/selected/neighbors")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNeighborsSelected(@PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		final List<CyNode> selectedNodes = CyTableUtil.getNodesInState(network, CyNetwork.SELECTED, true);
	
		final Set<Long> res = selectedNodes.stream()
			.map(node -> network.getNeighborList(node, Type.ANY))
			.flatMap(List::stream)
			.map(neighbor -> neighbor.getSUID())
			.collect(Collectors.toSet());
		
		return Response.ok(res).build();
	}


	/**
	 * @summary Utility to get all selected edges
	 * 
	 * @param networkId Network SUID
	 * 
	 * @return Selected edges as a list of SUID
	 */
	@GET
	@Path("/{networkId}/edges/selected")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSelectedEdges(@PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		final List<CyEdge> selectedEdges = CyTableUtil.getEdgesInState(network, CyNetwork.SELECTED, true);
		final List<Long> selectedEdgeIds = selectedEdges.stream()
			.map(edge -> edge.getSUID())
			.collect(Collectors.toList());
		
		return Response.ok(selectedEdgeIds).build();
	}

	/**
	 * @summary Get matching edges
	 * 
	 * @param networkId Network SUID
	 * @param column Optional.  Edge table column name to be used for search.
	 * @param query Optional.  Search query.
	 * 
	 * @return List of matched edge SUIDs.  If no parameter is given, returns all edge SUIDs.
	 */
	@GET
	@Path("/{networkId}/edges")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Long> getEdges(@PathParam("networkId") Long networkId, @QueryParam("column") String column,
			@QueryParam("query") String query) {
		return getByQuery(networkId, "edges", column, query);
	}

	/**
	 * 
	 * @summary Get a node
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param nodeId
	 *            Node SUID
	 * 
	 * @return Node with associated row data.
	 * 
	 */
	@GET
	@Path("/{networkId}/nodes/{nodeId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getNode(@PathParam("networkId") Long networkId, @PathParam("nodeId") Long nodeId) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyNode node = network.getNode(nodeId);
		if (node == null) {
			throw new NotFoundException("Could not find node with SUID: " + nodeId);
		}
		return getGraphObject(network, node);
	}

	/**
	 * 
	 * @summary Get an edge
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param edgeId
	 *            Edge SUID
	 * 
	 * @return Edge with associated row data
	 * 
	 */
	@GET
	@Path("/{networkId}/edges/{edgeId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getEdge(@PathParam("networkId") Long networkId, @PathParam("edgeId") Long edgeId) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyEdge edge = network.getEdge(edgeId);
		if (edge == null) {
			throw new NotFoundException("Could not find edge with SUID: " + edgeId);
		}
		return getGraphObject(network, edge);
	}

	/**
	 * @summary Get source/target node of an edge
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param edgeId
	 *            Edge SUID
	 * @param type
	 *            "source" or "target"
	 * 
	 * @return SUID of the source/target node
	 * 
	 */
	@GET
	@Path("/{networkId}/edges/{edgeId}/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getEdgeComponent(@PathParam("networkId") Long networkId, @PathParam("edgeId") Long edgeId,
			@PathParam("type") String type) {
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

	/**
	 * 
	 * @summary Get edge directionality
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param edgeId
	 *            Target edge SUID
	 * 
	 * @return true if the edge is directed.
	 * 
	 */
	@GET
	@Path("/{networkId}/edges/{edgeId}/isDirected")
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean getEdgeDirected(@PathParam("networkId") Long networkId, @PathParam("edgeId") Long edgeId) {
		final CyNetwork network = getCyNetwork(networkId);
		CyEdge edge = network.getEdge(edgeId);
		if (edge == null) {
			throw getError("Could not find edge: " + edgeId, new RuntimeException(), Response.Status.NOT_FOUND);
		}
		return edge.isDirected();
	}

	/**
	 * 
	 * @summary Get adjacent edges for a node
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param nodeId
	 *            Target node SUID
	 * 
	 * @return List of connected edges (as SUID)
	 * 
	 */
	@GET
	@Path("/{networkId}/nodes/{nodeId}/adjEdges")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Long> getAdjEdges(@PathParam("networkId") Long networkId, @PathParam("nodeId") Long nodeId) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyNode node = getNode(network, nodeId);
		final List<CyEdge> edges = network.getAdjacentEdgeList(node, Type.ANY);
		return getGraphObjectArray(edges);
	}

	/**
	 * @summary Get network pointer (nested network SUID)
	 * 
	 * @param networkId
	 *            Network SUID.
	 * @param nodeId
	 *            target node SUID.
	 * 
	 * @return Nested network SUID
	 */
	@GET
	@Path("/{networkId}/nodes/{nodeId}/pointer")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNetworkPointer(@PathParam("networkId") Long networkId, @PathParam("nodeId") Long nodeId) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyNode node = getNode(network, nodeId);
		final CyNetwork pointer = node.getNetworkPointer();
		if (pointer == null) {
			throw getError("Could not find network pointer.", new RuntimeException(), Response.Status.NOT_FOUND);
		}
		
		return Response.ok(getNumberObjectString(JsonTags.NETWORK_SUID, pointer.getSUID())).build();
	}

	/**
	 * 
	 * @summary Get first neighbors of the node
	 * 
	 * @param networkId
	 *            Target network SUID.
	 * @param nodeId
	 *            Node SUID.
	 * 
	 * @return Neighbors of the node as a list of SUIDs.
	 * 
	 */
	@GET
	@Path("/{networkId}/nodes/{nodeId}/neighbors")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNeighbours(@PathParam("networkId") Long networkId, @PathParam("nodeId") Long nodeId) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyNode node = getNode(network, nodeId);
		final List<CyNode> nodes = network.getNeighborList(node, Type.ANY);
		
		return Response.status(Response.Status.OK).entity(getGraphObjectArray(nodes)).build();
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

	/**
	 * Add new node(s) to the network.  Body should include an array of new node names.
	 * <br/>
	 * 
	 * <pre>
	 * [ "nodeName1", "nodeName2", ... ]
	 * </pre>
	 * 
	 * <br />
	 * Node name will be used for "name" column. 
	 * 
	 * @summary Add node(s) to existing network
	 * 
	 * @param networkId Network SUID
	 * 
	 * @return SUID of the new node(s) with the name.
	 */
	@POST
	@Path("/{networkId}/nodes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createNode(@PathParam("networkId") Long networkId, final InputStream is) {
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
	
	
	/**
	 * Add new edge(s) to the network.  Body should include an array of new node names.
	 * <pre>
	 * [
	 * 	{
	 * 		"source": SOURCE_NODE_SUID,
	 * 		"target": TARGET_NODE_SUID,
	 * 		"directed": (Optional boolean value.  Default is True),
	 * 		"interaction": (Optional.  Will be used for Interaction column.  Default value is '-')
	 * 	} ...
	 * ]
	 * </pre>
	 * 
	 * @summary Add edge(s) to existing network
	 * 
	 * @param networkId Network SUID
	 * 
	 * @return SUIDs of the new edges with source and target SUIDs.
	 * 
	 */
	@POST
	@Path("/{networkId}/edges")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createEdge(@PathParam("networkId") Long networkId, final InputStream is) {
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

	
	/**
	 * 
	 * @summary Delete all networks in current session
	 */
	@DELETE
	@Path("/")
	public Response deleteAllNetworks() {
		this.networkManager.getNetworkSet().stream()
			.forEach(network->this.networkManager.destroyNetwork(network));
		
		return Response.ok().build();
	}

	/**
	 * @summary Delete a network
	 * 
	 * @param networkId Network SUID
	 */
	@DELETE
	@Path("/{networkId}")
	public Response deleteNetwork(@PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		this.networkManager.destroyNetwork(network);
		return Response.ok().build();
	}

	/**
	 * @summary Delete all nodes in the network
	 * 
	 * @param networkId
	 *            Network SUID.
	 */
	@DELETE
	@Path("/{networkId}/nodes")
	public Response deleteAllNodes(@PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		network.removeNodes(network.getNodeList());
		updateViews(network);
		
		return Response.ok().build();
	}

	/**
	 * @summary Delete all edges in the network
	 * 
	 * @param networkId
	 *            Network SUID
	 */
	@DELETE
	@Path("/{networkId}/edges")
	public Response deleteAllEdges(@PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		network.removeEdges(network.getEdgeList());
		updateViews(network);
		
		return Response.ok().build();
	}


	/**
	 * 
	 * @summary Delete a node in the network
	 * 
	 * @param networkId Network SUID
	 * @param nodeId Node SUID
	 * 
	 */
	@DELETE
	@Path("/{networkId}/nodes/{nodeId}")
	public Response deleteNode(@PathParam("networkId") Long networkId, @PathParam("nodeId") Long nodeId) {
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


	/**
	 * 
	 * @summary Delete an edge in the network.
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param edgeId
	 *            SUID of the edge to be deleted
	 */
	@DELETE
	@Path("/{networkId}/edges/{edgeId}")
	public Response deleteEdge(@PathParam("networkId") Long networkId, @PathParam("edgeId") Long edgeId) {
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

	
	/**
	 * @summary Create a new network from Cytoscape.js JSON or Edgelist
	 * 
	 * @param collection Name of new network collection
	 * @param title Title of the new network
	 * @param source Optional.  "url"
	 * @param format "edgelist" or "json" 
	 * 
	 * @return SUID of the new network
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createNetwork(@QueryParam("collection") String collection,
			@QueryParam("source") String source, @QueryParam("format") String format, 
			@QueryParam("title") String title, final InputStream is,
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

	/**
	 * 
	 * If body is empty, it simply creates new network from current selection.
	 * Otherwise, select from the list of SUID.
	 * 
	 * @summary Create a subnetwork from selected nodes and edges
	 * 
	 * @param networkId Network SUID
	 * 
	 * @return SUID of the new network.
	 * 
	 */
	@POST
	@Path("/{networkId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createNetworkFromSelected(
			@PathParam("networkId") Long networkId,
			@QueryParam("title") String title,
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