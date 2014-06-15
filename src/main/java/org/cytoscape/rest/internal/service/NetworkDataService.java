package org.cytoscape.rest.internal.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.cytoscape.io.read.CyNetworkReader;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.rest.internal.task.RestTaskManager;
import org.cytoscape.task.NetworkCollectionTaskFactory;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
@Path("/v1")
// API version
public class NetworkDataService extends AbstractDataService {

	private final static Logger logger = LoggerFactory.getLogger(NetworkDataService.class);

	// Preset types
	private static final String DEF_COLLECTION_PREFIX = "Posted: ";

	public NetworkDataService() {
		super();
	}


	@GET
	@Path("/" + NETWORKS + "/count")
	@Produces(MediaType.APPLICATION_JSON)
	public String getNetworkCount() {
		return getNumberObjectString("networkCount", networkManager.getNetworkSet().size());
	}


	@GET
	@Path("/" + NETWORKS + "/{id}/nodes/count")
	@Produces(MediaType.APPLICATION_JSON)
	public String getNodeCount(@PathParam("id") Long id) {
		return getNumberObjectString("nodeCount", getCyNetwork(id).getNodeCount());
	}


	@GET
	@Path("/" + NETWORKS + "/{id}/edges/count")
	@Produces(MediaType.APPLICATION_JSON)
	public String getEdgeCount(@PathParam("id") Long id) {
		return getNumberObjectString("edgeCount", getCyNetwork(id).getEdgeCount());
	}



	private String getByQuery(final Long id, final String objType, final String column, final String query) {
		final CyNetwork network = getCyNetwork(id);
		CyTable table = null;
		if (objType.equals("nodes")) {
			table = network.getDefaultNodeTable();
		} else if (objType.equals("edges")) {
			table = network.getDefaultEdgeTable();
		} else {
			throw new WebApplicationException("Invalid graph object type: " + objType, 500);
		}

		final Collection<CyRow> rows;
		if (query == null && column == null) {
			rows = table.getAllRows();
		} else if(query == null || column == null) {
			throw new WebApplicationException("Query parameters are incomplete.", 500);
		} else {
			Object rawQuery = getRawValue(query, table.getColumn(column).getType());
			rows = table.getMatchingRows(column, rawQuery);
		}

		final JsonFactory factory = new JsonFactory();
		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = null;

		try {
			generator = factory.createGenerator(stream);
			generator.writeStartArray();
			for (final CyRow row : rows) {
				generator.writeNumber(row.get(CyIdentifiable.SUID, Long.class));
			}
			generator.writeEndArray();
			generator.close();
			result = stream.toString();
			stream.close();
		} catch (Exception e) {
			throw new WebApplicationException(e, 500);
		}
		return result;
	}

	private final Object getRawValue(final String queryString, Class<?> type) {
		Object raw = queryString;

		if (type == Boolean.class) {
			raw = Boolean.parseBoolean(queryString);
		} else if (type == Double.class) {
			raw = Double.parseDouble(queryString);
		} else if (type == Integer.class) {
			raw = Integer.parseInt(queryString);
		} else if (type == Long.class) {
			raw = Long.parseLong(queryString);
		} else if (type == Float.class) {
			raw = Float.parseFloat(queryString);
		}
		return raw;
	}

	@GET
	@Path("/" + NETWORKS)
	@Produces(MediaType.APPLICATION_JSON)
	public String getNetworks(@QueryParam("column") String column, @QueryParam("query") String query) {
		if(column == null && query == null) {
			return getNetworks(networkManager.getNetworkSet());
		} else {
			return getNetworksByQuery(query, column);
		}
	}


	private final String getNetworksByQuery(final String query, final String column) {
		final Set<CyNetwork> networks = networkManager.getNetworkSet();
		final Set<CyNetwork> matchedNetworks = new HashSet<CyNetwork>();
		
		for(final CyNetwork network:networks) {
			final CyTable table=network.getDefaultNetworkTable();
			final Object rawQuery = getRawValue(query, table.getColumn(column).getType());
			final Collection<CyRow> rows = table.getMatchingRows(column, rawQuery);
			if(rows.isEmpty() == false) {
				matchedNetworks.add(network);
			}
		}
		return getNetworks(matchedNetworks);
	}


	private final String getNetworks(final Set<CyNetwork> networks) {
		StringBuilder result = new StringBuilder();
		result.append("[");

		for (final CyNetwork network : networks) {
			result.append(getNetworkString(network));
			result.append(",");
		}
		String jsonString = result.toString();
		jsonString = jsonString.substring(0, jsonString.length() - 1);

		return jsonString + "]";
	}


	@GET
	@Path("/" + NETWORKS + "/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getNetwork(@PathParam("id") Long id) {
		return getNetworkString(getCyNetwork(id));
	}


	/**
	 * Get edges as array of SUID
	 * 
	 * @param id
	 * @return
	 */
	@GET
	@Path("/" + NETWORKS + "/{id}/nodes")
	@Produces(MediaType.APPLICATION_JSON)
	public String getNodes(@PathParam("id") Long id, @QueryParam("column") String column,
			@QueryParam("query") String query) {
		return getByQuery(id, "nodes", column, query);
	}

	@GET
	@Path("/" + NETWORKS + "/{id}/edges")
	@Produces(MediaType.APPLICATION_JSON)
	public String getEdges(@PathParam("id") Long id, @QueryParam("column") String column,
			@QueryParam("query") String query) {
		return getByQuery(id, "edges", column, query);
	}



	/**
	 * Returns a node or an edge.
	 * 
	 * @param id
	 * @param objType
	 * @param objId
	 * @return
	 */
	@GET
	@Path("/" + NETWORKS + "/{id}/{objType}/{objId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getGraphObject(@PathParam("id") Long id, @PathParam("objType") String objType,
			@PathParam("objId") Long suid) {
		final CyNetwork network = getCyNetwork(id);

		CyIdentifiable obj = null;
		try {
			if (objType.equals("nodes")) {
				obj = network.getNode(suid);
			} else if (objType.equals("edges")) {
				obj = network.getEdge(suid);
			} else {
				throw new WebApplicationException("Invalid graph object type: " + objType, 500);
			}
		} catch (Exception e) {
			throw new WebApplicationException(e, 500);
		}

		if (obj == null) {
			throw new WebApplicationException("Could not find object: " + suid, 404);
		}
		return getGraphObject(network, obj);
	}

	@GET
	@Path("/" + NETWORKS + "/{id}/edges/{objId}/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getEdgeComponent(@PathParam("id") Long id, @PathParam("objId") Long suid,
			@PathParam("type") String type) {
		final CyNetwork network = getCyNetwork(id);
		CyEdge edge = network.getEdge(suid);

		if (edge == null) {
			throw new WebApplicationException("Could not find edge: " + suid, 404);
		}

		Long nodeSUID = null;
		if (type.equals("source")) {
			nodeSUID = edge.getSource().getSUID();
		} else if (type.equals("target")) {
			nodeSUID = edge.getTarget().getSUID();
		} else {
			throw new WebApplicationException("Invalid parameter for edge: " + type, 500);
		}

		return "{\"SUID\": " + nodeSUID + "}";
	}

	@GET
	@Path("/" + NETWORKS + "/{id}/edges/{objId}/directed")
	@Produces(MediaType.APPLICATION_JSON)
	public String getEdgeDirected(@PathParam("id") Long id, @PathParam("objId") Long suid) {
		final CyNetwork network = getCyNetwork(id);
		CyEdge edge = network.getEdge(suid);
		if (edge == null) {
			throw new WebApplicationException("Could not find edge: " + suid, 404);
		}
		return "{\"directed\": " + edge.isDirected() + "}";
	}

	private final String getGraphObjectArray(final CyNetwork network, final Class<? extends CyIdentifiable> type) {
		final JsonFactory factory = new JsonFactory();

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = null;
		try {
			generator = factory.createGenerator(stream);
			generator.writeStartArray();

			final List<? extends CyIdentifiable> graphObjects;
			if (type == CyNode.class) {
				graphObjects = network.getNodeList();
			} else if (type == CyEdge.class) {
				graphObjects = network.getEdgeList();
			} else {
				throw new WebApplicationException(500);
			}
			for (final CyIdentifiable obj : graphObjects) {
				final Long suid = obj.getSUID();
				generator.writeNumber(suid);
			}
			generator.writeEndArray();
			generator.close();
			result = stream.toString();
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Could not create stream.", e);
			throw new WebApplicationException(500);
		}
		return result;
	}

	@GET
	@Path("/" + NETWORKS + "/{id}/nodes/{objId}/adjedges")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAdjEdges(@PathParam("id") Long id, @PathParam("objId") Long objId) {
		final CyNetwork network = getCyNetwork(id);
		final CyNode node = getNode(network, objId);
		final List<CyEdge> edges = network.getAdjacentEdgeList(node, Type.ANY);
		return getGraphObjectArray(edges);
	}

	@GET
	@Path("/" + NETWORKS + "/{id}/nodes/{objId}/pointer")
	@Produces(MediaType.APPLICATION_JSON)
	public String getNetworkPointer(@PathParam("id") Long id, @PathParam("objId") Long objId) {
		final CyNetwork network = getCyNetwork(id);
		final CyNode node = getNode(network, objId);
		final CyNetwork pointer = node.getNetworkPointer();
		return getNumberObjectString("netwoirkPointer", pointer.getSUID());
	}

	@GET
	@Path("/" + NETWORKS + "/{id}/nodes/{objId}/neignbours")
	@Produces(MediaType.APPLICATION_JSON)
	public String getNeighbours(@PathParam("id") Long id, @PathParam("objId") Long objId) {
		final CyNetwork network = getCyNetwork(id);
		final CyNode node = getNode(network, objId);
		final List<CyNode> nodes = network.getNeighborList(node, Type.ANY);
		return getGraphObjectArray(nodes);
	}

	/**
	 * Get array of objects.
	 * 
	 * @param objects
	 * @return
	 */
	private final String getGraphObjectArray(Collection<? extends CyIdentifiable> objects) {
		final JsonFactory factory = new JsonFactory();

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = null;
		try {
			generator = factory.createGenerator(stream);
			generator.writeStartArray();
			for (final CyIdentifiable obj : objects) {
				final Long suid = obj.getSUID();
				generator.writeNumber(suid);
			}
			generator.writeEndArray();
			generator.close();
			result = stream.toString();
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Could not create stream.", e);
			throw new WebApplicationException("Could not create stream.", 500);
		}
		return result;
	}

	/**
	 * Add a new node to existing network
	 * 
	 * @param id
	 *            network SUID
	 * @param is
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/" + NETWORKS + "/{id}/nodes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createNode(@PathParam("id") Long id, final InputStream is) throws Exception {
		final CyNetwork network = getCyNetwork(id);
		final ObjectMapper objMapper = new ObjectMapper();
		final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);

		// Single or multiple
		if (rootNode.isArray()) {
			final JsonFactory factory = new JsonFactory();

			String result = null;
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
			result = stream.toString();
			stream.close();
			updateViews(network);
			return result;
		} else {
			throw new WebApplicationException("Need to POST as array.", 500);
		}
	}

	@POST
	@Path("/" + NETWORKS + "/{id}/edges")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createEdge(@PathParam("id") Long id, final InputStream is) throws Exception {
		final CyNetwork network = getCyNetwork(id);
		final ObjectMapper objMapper = new ObjectMapper();
		final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);

		// Single or multiple
		if (rootNode.isArray()) {
			final JsonFactory factory = new JsonFactory();

			String result = null;
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			JsonGenerator generator = null;
			generator = factory.createGenerator(stream);
			generator.writeStartArray();
			for (final JsonNode node : rootNode) {
				JsonNode source = node.get("source");
				JsonNode target = node.get("target");
				JsonNode interaction = node.get(CyEdge.INTERACTION);
				JsonNode isDirected = node.get("directed");
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
				generator.writeNumberField("source", sourceSUID);
				generator.writeNumberField("target", targetSUID);
				generator.writeEndObject();
			}
			generator.writeEndArray();
			generator.close();
			result = stream.toString();
			stream.close();
			updateViews(network);
			return result;
		} else {
			throw new WebApplicationException("Need to POST as array.", 500);
		}
	}

	/**
	 * TODO: Ask everyone (which is more natural?)
	 * 
	 * Merge nodes and edges
	 * 
	 * @param id
	 */
	@PUT
	@Path("/" + NETWORKS + "/{id}/")
	public void updateNetwork(@PathParam("id") Long id) {
		final CyNetwork network = getCyNetwork(id);
	}

	// //////////////// Delete //////////////////////////////////

	@DELETE
	@Path("/" + NETWORKS)
	public void deleteAllNetworks() {
		final Set<CyNetwork> allNetworks = this.networkManager.getNetworkSet();
		for (final CyNetwork network : allNetworks) {
			this.networkManager.destroyNetwork(network);
		}
	}

	@DELETE
	@Path("/" + NETWORKS + "/{id}/")
	public void deleteNetwork(@PathParam("id") Long id) {
		final CyNetwork network = getCyNetwork(id);
		this.networkManager.destroyNetwork(network);
	}

	@DELETE
	@Path("/" + NETWORKS + "/{id}/nodes")
	public void deleteAllNodes(@PathParam("id") Long id) {
		final CyNetwork network = getCyNetwork(id);
		network.removeNodes(network.getNodeList());
		updateViews(network);
	}

	@DELETE
	@Path("/" + NETWORKS + "/{id}/edges")
	public void deleteAllEdges(@PathParam("id") Long id) {
		final CyNetwork network = getCyNetwork(id);
		network.removeEdges(network.getEdgeList());
		updateViews(network);
	}

	@DELETE
	@Path("/" + NETWORKS + "/{networkId}/nodes/{nodeId}")
	public void deleteNode(@PathParam("networkId") Long networkId, @PathParam("nodeId") Long nodeId) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyNode node = network.getNode(nodeId);
		if (node == null) {
			throw new WebApplicationException("Node does not exist.", 404);
		}
		final List<CyNode> nodes = new ArrayList<CyNode>();
		nodes.add(node);
		network.removeNodes(nodes);
		updateViews(network);
	}

	@DELETE
	@Path("/" + NETWORKS + "/{networkId}/edges/{edgeId}")
	public void deleteEdge(@PathParam("networkId") Long networkId, @PathParam("edgeId") Long edgeId) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyEdge edge = network.getEdge(edgeId);
		if (edge == null) {
			throw new WebApplicationException("Edge does not exist.", 404);
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
	 * Create network from Cytoscape.js style JSON.
	 * 
	 * @param collection
	 *            Name of network collection.
	 * @param is
	 * @throws Exception
	 */
	@POST
	@Path("/" + NETWORKS)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createNetwork(@DefaultValue(DEF_COLLECTION_PREFIX) @QueryParam("collection") String collection,
			final InputStream is, @Context HttpHeaders headers) throws Exception {

		// Check user agent if available
		final List<String> agent = headers.getRequestHeader("user-agent");
		String userAgent = "";
		if (agent != null) {
			userAgent = agent.get(0);
		}

		final TaskIterator it = cytoscapeJsReaderFactory.createTaskIterator(is, "test123");
		final CyNetworkReader reader = (CyNetworkReader) it.next();

		String collectionName = collection;
		if (collection.equals(DEF_COLLECTION_PREFIX)) {
			collectionName = collectionName + userAgent;
		}

		reader.run(null);

		CyNetwork[] networks = reader.getNetworks();
		CyNetwork newNetwork = networks[0];
		System.out.println("###LEN = " + networks.length);
		System.out.println("###read5 edges = " + newNetwork.getEdgeCount());
		System.out.println("###read5 nodes = " + newNetwork.getNodeCount());
		addNetwork(networks, reader, collectionName);
		is.close();

		// Return SUID-to-Original map
		return getNumberObjectString("networkSUID", newNetwork.getSUID());
	}

	private final String getNetworkString(final CyNetwork network) {
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		CyWriter writer = cytoscapeJsWriterFactory.createWriter(stream, network);
		String jsonString = null;
		try {
			writer.run(null);
			jsonString = stream.toString();
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonString;
	}

	private final void addNetwork(final CyNetwork[] networks, final CyNetworkReader reader, final String collectionName) {
		final VisualStyle style = vmm.getCurrentVisualStyle();
		final List<CyNetworkView> results = new ArrayList<CyNetworkView>();

		for (final CyNetwork network : networks) {
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
			System.out.println("####### root: " + collectionName);
			if (networks[0] instanceof CySubNetwork) {
				CySubNetwork subnet = (CySubNetwork) networks[0];
				final CyRootNetwork rootNet = subnet.getRootNetwork();
				String rootNetName = rootNet.getRow(rootNet).get(CyNetwork.NAME, String.class);
				rootNet.getRow(rootNet).set(CyNetwork.NAME, collectionName);
				// if (rootNetName == null || rootNetName.trim().length() == 0){
				// // The root network does not have a name yet, set it the same
				// as the base subnetwork
				// rootNet.getRow(rootNet).set(CyNetwork.NAME, collectionName);
				// }
			}
		}

		// // Make sure rootNetwork has a name
		// for (CyNetwork network : networks) {
		// if (network instanceof CySubNetwork){
		// CySubNetwork subNet = (CySubNetwork) network;
		// CyRootNetwork rootNet = subNet.getRootNetwork();
		//
		// String networkName = rootNet.getRow(rootNet).get(CyNetwork.NAME,
		// String.class);
		// if(networkName == null || networkName.trim().length() == 0) {
		// networkName = name;
		// if(networkName == null)
		// networkName = "? (Name is missing)";
		//
		// rootNet.getRow(rootNet).set(CyNetwork.NAME,
		// namingUtil.getSuggestedNetworkTitle(networkName));
		// }
		// }
		// }
	}

	private CyNetwork findNetwork(final String idType, final String id) {
		final CyNetwork network;
		if (idType.equals("suid")) {
			try {
				final Long suid = Long.parseLong(id);
				network = networkManager.getNetwork(suid);
			} catch (NumberFormatException nfe) {
				throw new WebApplicationException(400);
			}

		} else if (idType.equals("title")) {
			network = getNetworkByTitle(id);
		} else {
			throw new WebApplicationException(400);
		}

		// Could not find network by the identifier.
		if (network == null)
			throw new WebApplicationException(404);

		return network;
	}

	@GET
	@Path("/execute/{taskId}/{suid}")
	@Produces(MediaType.APPLICATION_JSON)
	public String runNetworkTask(@PathParam("suid") String suid, @PathParam("taskId") String taskName) {
		if (taskName != null) {
			NetworkTaskFactory tf = tfManager.getNetworkTaskFactory(taskName);
			System.out.println("TF from Map: " + tf);

			if (tf != null) {
				TaskIterator ti;

				System.out.println("## Got network tf: " + taskName);
				NetworkTaskFactory ntf = (NetworkTaskFactory) tf;
				final Long networkSUID = parseSUID(suid);
				final CyNetwork network = networkManager.getNetwork(networkSUID);
				ti = ntf.createTaskIterator(network);

				TaskManager tm = new RestTaskManager();
				tm.execute(ti);
				System.out.println("Finished Task: " + taskName);
			}
			return "{ \"currentNetwork\": " + applicationManager.getCurrentNetwork().getSUID() + "}";
		} else {
			return "Could not execute task.";
		}
	}

	@GET
	@Path("/execute/networks/{taskId}/{suid}")
	@Produces(MediaType.APPLICATION_JSON)
	public String runNetworkCollectionTask(@PathParam("suid") String suid, @PathParam("taskId") String taskName) {
		if (taskName != null) {
			NetworkCollectionTaskFactory tf = tfManager.getNetworkCollectionTaskFactory(taskName);
			System.out.println("Got TF: " + taskName);
			if (tf != null) {
				TaskIterator ti;

				System.out.println("Got network collection tf: " + taskName);
				NetworkCollectionTaskFactory ntf = (NetworkCollectionTaskFactory) tf;
				final Long networkSUID = parseSUID(suid);
				final CyNetwork network = networkManager.getNetwork(networkSUID);
				List<CyNetwork> networks = new ArrayList<CyNetwork>();
				networks.add(network);
				ti = ntf.createTaskIterator(networks);

				TaskManager tm = new RestTaskManager();
				tm.execute(ti);
				System.out.println("Finished Task: " + taskName);
			}
			return "OK";
		} else {
			return "Could not execute task.";
		}
	}

	@GET
	@Path("/execute/{taskId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String runStatelessTask(@PathParam("taskId") String taskName) {
		if (taskName != null) {
			TaskFactory tf = tfManager.getTaskFactory(taskName);
			if (tf != null) {
				TaskIterator ti = tf.createTaskIterator();

				TaskManager tm = new RestTaskManager();
				tm.execute(ti);
				System.out.println("Finished Task: " + taskName);
			}
			return "OK";
		} else {
			return "Could not execute task.";
		}
	}

	private final Long parseSUID(final String stringID) {
		Long networkSUID = null;
		try {
			networkSUID = Long.parseLong(stringID);
		} catch (NumberFormatException ex) {
			logger.warn("Invalid SUID: " + stringID, ex);
			throw new IllegalArgumentException("Could not parse SUID: " + stringID);
		}

		return networkSUID;
	}

	private final CyNetwork getNetworkByTitle(final String title) {
		final Set<CyNetwork> networks = networkManager.getNetworkSet();

		for (final CyNetwork network : networks) {
			final String networkTitle = network.getRow(network).get(CyNetwork.NAME, String.class);
			if (networkTitle == null)
				continue;
			if (networkTitle.equals(title))
				return network;
		}

		// Not found
		return null;
	}
}
