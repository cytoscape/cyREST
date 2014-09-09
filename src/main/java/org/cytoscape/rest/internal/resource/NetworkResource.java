package org.cytoscape.rest.internal.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.io.read.AbstractCyNetworkReader;
import org.cytoscape.io.read.CyNetworkReader;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.rest.internal.datamapper.MapperUtil;
import org.cytoscape.rest.internal.task.HeadlessTaskMonitor;
import org.cytoscape.task.AbstractNetworkCollectionTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmino.miredot.annotations.ReturnType;

@Singleton
@Path("/v1/networks")
public class NetworkResource extends AbstractResource {

	// Preset types
	private static final String DEF_COLLECTION_PREFIX = "Posted: ";

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
	public String getNetworkCount() {
		return getNumberObjectString(JsonTags.COUNT, networkManager.getNetworkSet().size());
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
	public String getNodeCount(@PathParam("networkId") Long networkId) {
		return getNumberObjectString(JsonTags.COUNT, getCyNetwork(networkId).getNodeCount());
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
	public String getEdgeCount(@PathParam("networkId") Long networkId) {
		return getNumberObjectString(JsonTags.COUNT, getCyNetwork(networkId).getEdgeCount());
	}

	private final Collection<Long> getByQuery(final Long id, final String objType, final String column,
			final String query) {
		final CyNetwork network = getCyNetwork(id);
		CyTable table = null;
		if (objType.equals("nodes")) {
			table = network.getDefaultNodeTable();
		} else if (objType.equals("edges")) {
			table = network.getDefaultEdgeTable();
		} else {
			throw getError("Invalid graph object type: " + objType, new IllegalArgumentException(),
					Response.Status.INTERNAL_SERVER_ERROR);
		}

		final Collection<CyRow> rows;
		if (query == null && column == null) {
			rows = table.getAllRows();
		} else if (query == null || column == null) {
			throw getError("Missing query parameter.", new IllegalArgumentException(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} else {
			Object rawQuery = MapperUtil.getRawValue(query, table.getColumn(column).getType());
			rows = table.getMatchingRows(column, rawQuery);
		}

		final Collection<Long> suids = new ArrayList<Long>();
		for (final CyRow row : rows) {
			suids.add(row.get(CyIdentifiable.SUID, Long.class));
		}

		return suids;
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
		Collection<CyNetwork> networks;
		
		if (column == null && query == null) {
			networks = networkManager.getNetworkSet();
		} else {
			if(column == null || column.length() == 0) {
				throw getError("Column name parameter is missing.", new IllegalArgumentException(), Response.Status.INTERNAL_SERVER_ERROR);
			}
			if(query == null || query.length() == 0) {
				throw getError("Query parameter is missing.", new IllegalArgumentException(), Response.Status.INTERNAL_SERVER_ERROR);
			}
			networks = getNetworksByQuery(query, column);
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
	@ReturnType("org.cytoscape.rest.internal.model.CyNetworkWrapper")
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
	@ReturnType("org.cytoscape.rest.internal.model.Node")
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
	@ReturnType("org.cytoscape.rest.internal.model.Edge")
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
	public String getNetworkPointer(@PathParam("networkId") Long networkId, @PathParam("nodeId") Long nodeId) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyNode node = getNode(network, nodeId);
		final CyNetwork pointer = node.getNetworkPointer();
		if (pointer == null) {
			throw getError("Could not find network pointer.", new RuntimeException(), Response.Status.NOT_FOUND);
		}
		return getNumberObjectString(JsonTags.NETWORK_SUID, pointer.getSUID());
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
	public Collection<Long> getNeighbours(@PathParam("networkId") Long networkId, @PathParam("nodeId") Long nodeId) {
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
		final Collection<Long> suids = new ArrayList<Long>();
		for (final CyIdentifiable obj : objects) {
			suids.add(obj.getSUID());
		}
		return suids;
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
	public String createNode(@PathParam("networkId") Long networkId, final InputStream is) {
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
			return result;
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
	 * 		"interaction": (Optional.  Will be used for Interaction column.)
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
	public String createEdge(@PathParam("networkId") Long networkId, final InputStream is) {
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
					if (interaction != null) {
						network.getRow(edge).set(CyEdge.INTERACTION, interaction.textValue());
					}

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
				throw getError("Could not create edge.", e, Response.Status.INTERNAL_SERVER_ERROR);
			}
			return result;
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
	public void deleteAllNetworks() {
		final Set<CyNetwork> allNetworks = this.networkManager.getNetworkSet();
		for (final CyNetwork network : allNetworks) {
			this.networkManager.destroyNetwork(network);
		}
	}

	/**
	 * @summary Delete a network
	 * 
	 * @param networkId Network SUID
	 */
	@DELETE
	@Path("/{networkId}")
	public void deleteNetwork(@PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		this.networkManager.destroyNetwork(network);
	}

	/**
	 * @summary Delete all nodes in the network
	 * 
	 * @param networkId
	 *            Network SUID.
	 */
	@DELETE
	@Path("/{networkId}/nodes")
	public void deleteAllNodes(@PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		network.removeNodes(network.getNodeList());
		updateViews(network);
	}

	/**
	 * @summary Delete all edges in the network
	 * 
	 * @param networkId
	 *            Network SUID
	 */
	@DELETE
	@Path("/{networkId}/edges")
	public void deleteAllEdges(@PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		network.removeEdges(network.getEdgeList());
		updateViews(network);
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
	public void deleteNode(@PathParam("networkId") Long networkId, @PathParam("nodeId") Long nodeId) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyNode node = network.getNode(nodeId);
		if (node == null) {
			throw new NotFoundException("Node does not exist.");
		}
		final List<CyNode> nodes = new ArrayList<CyNode>();
		nodes.add(node);
		network.removeNodes(nodes);
		updateViews(network);
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
	public void deleteEdge(@PathParam("networkId") Long networkId, @PathParam("edgeId") Long edgeId) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyEdge edge = network.getEdge(edgeId);
		if (edge == null) {
			throw new NotFoundException("Edge does not exist.");
		}
		final List<CyEdge> edges = new ArrayList<CyEdge>();
		edges.add(edge);
		network.removeEdges(edges);
		updateViews(network);
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
	 * @param source Optional.  "url"
	 * @param format "edgelist" or "json" 
	 * 
	 * @return SUID of the new network
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createNetwork(@DefaultValue(DEF_COLLECTION_PREFIX) @QueryParam("collection") String collection,
			@QueryParam("source") String source, @QueryParam("format") String format, final InputStream is,
			@Context HttpHeaders headers) {

		// 1. If source is URL, load from the array of URL
		if (source != null && source.equals(JsonTags.URL)) {
			try {
				return loadNetwork(collection, is);
			} catch (IOException e) {
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

		final TaskIterator it;
		if (format != null && format.trim().equals(JsonTags.FORMAT_EDGELIST)) {
			it = edgeListReaderFactory.createTaskIterator(is, collection);
		} else {
			it = cytoscapeJsReaderFactory.createTaskIterator(is, collection);
		}

		final CyNetworkReader reader = (CyNetworkReader) it.next();

		final String collectionName;
		if (collection == null) {
			collectionName = DEF_COLLECTION_PREFIX + userAgent;
		} else {
			collectionName = collection;
		}

		try {
			reader.run(new HeadlessTaskMonitor());
		} catch (Exception e) {
			throw getError("Could not parse the given network JSON.", e, Response.Status.PRECONDITION_FAILED);
		}

		final CyNetwork[] networks = reader.getNetworks();
		final CyNetwork newNetwork = networks[0];
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
	public String createNetworkFromSelected(@PathParam("networkId") Long networkId, final InputStream is,
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
				final Long suid = ((CyNetworkView) result.iterator().next()).getModel().getSUID();
				return getNumberObjectString(JsonTags.NETWORK_SUID, suid);
			}
		}

		throw getError("Could not get new network SUID.", new IllegalStateException(), Response.Status.INTERNAL_SERVER_ERROR);
	}


	private final String loadNetwork(final String collectionName, final InputStream is) throws IOException {
		final ObjectMapper objMapper = new ObjectMapper();
		final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);

		final Map<String, Long[]> results = new HashMap<String, Long[]>();
		// Input should be array of URLs.
		for (final JsonNode node : rootNode) {
			final String sourceUrl = node.asText();
			TaskIterator itr = loadNetworkURLTaskFactory.loadCyNetworks(new URL(sourceUrl));
			CyNetworkReader currentReader = null;

			while (itr.hasNext()) {
				final Task task = itr.next();
				try {
					if (task instanceof CyNetworkReader) {
						currentReader = (CyNetworkReader) task;
						if(currentReader instanceof AbstractCyNetworkReader && collectionName != null) {
							((AbstractCyNetworkReader)currentReader).getRootNetworkList().setSelectedValue(collectionName);
						}
						currentReader.run(new HeadlessTaskMonitor());
					} else {
						task.run(new HeadlessTaskMonitor());
					}
				} catch (Exception e) {
					throw new IOException("Could not execute network reader.", e);
				}
			}

			final CyNetwork[] networks = currentReader.getNetworks();
			final Long[] suids = new Long[networks.length];
			int counter = 0;
			for (CyNetwork network : networks) {
				if(collectionName != null) {
					final CyRootNetwork rootNetwork = ((CySubNetwork)network).getRootNetwork();
					rootNetwork.getRow(rootNetwork).set(CyNetwork.NAME, collectionName);
				}
				suids[counter] = network.getSUID();
				counter++;
			}
			results.put(sourceUrl, suids);
		}

		is.close();
		return generateNetworkLoadResults(results);
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


	/**
	 * Add network to the manager
	 * 
	 * @param networks
	 * @param reader
	 * @param collectionName
	 */
	private final void addNetwork(final CyNetwork[] networks, final CyNetworkReader reader, final String collectionName) {

		final VisualStyle style = vmm.getCurrentVisualStyle();
		final List<CyNetworkView> results = new ArrayList<CyNetworkView>();

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
			int viewThreshold = 10000;
			if (numGraphObjects < viewThreshold) {
				final CyNetworkView view = reader.buildCyNetworkView(network);
				networkViewManager.addNetworkView(view);
				vmm.setVisualStyle(style, view);
				style.apply(view);

				if (!view.isSet(BasicVisualLexicon.NETWORK_CENTER_X_LOCATION)
						&& !view.isSet(BasicVisualLexicon.NETWORK_CENTER_Y_LOCATION)
						&& !view.isSet(BasicVisualLexicon.NETWORK_CENTER_Z_LOCATION))
					view.fitContent();
				results.add(view);
			} else {
				// results.add(nullNetworkViewFactory.createNetworkView(network));
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
	}
}