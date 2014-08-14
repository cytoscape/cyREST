package org.cytoscape.rest.internal.service;

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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
import org.cytoscape.rest.internal.datamapper.MapperUtil;
import org.cytoscape.rest.internal.task.HeadlessTaskMonitor;
import org.cytoscape.task.AbstractNetworkCollectionTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmino.miredot.annotations.ReturnType;

@Singleton
@Path("/v1/networks")
public class NetworkDataService extends AbstractDataService {

	private final static Logger logger = LoggerFactory.getLogger(NetworkDataService.class);

	// Preset types
	private static final String DEF_COLLECTION_PREFIX = "Posted: ";

	public NetworkDataService() {
		super();
	}


	/**
	 * 
	 * Get number of networks in the current session
	 * 
	 * @return Number of networks in current Cytoscape session
	 */
	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Integer getNetworkCount() {
		return networkManager.getNetworkSet().size();
	}


	/**
	 * 
	 * Get number of nodes in the network
	 * 
	 * @param id Network SUID
	 * @return Number of nodes in the network with given SUID
	 */
	@GET
	@Path("/{networkId}/nodes/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Integer getNodeCount(@PathParam("networkId") Long networkId) {
		return getCyNetwork(networkId).getNodeCount();
	}


	/**
	 * 
	 * Get number of edges in the network
	 * 
	 * @param networkId Network SUID
	 * 
	 * @return number of edges in the network
	 */
	@GET
	@Path("/{networkId}/edges/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Integer getEdgeCount(@PathParam("networkId") Long networkId) {
		return getCyNetwork(networkId).getEdgeCount();
	}


	private final Collection<Long> getByQuery(final Long id, final String objType, final String column, final String query) {
		final CyNetwork network = getCyNetwork(id);
		CyTable table = null;
		if (objType.equals("nodes")) {
			table = network.getDefaultNodeTable();
		} else if (objType.equals("edges")) {
			table = network.getDefaultEdgeTable();
		} else {
			logger.error("Invalid object type: " + objType);
			throw new WebApplicationException("Invalid graph object type: " + objType, 500);
		}

		final Collection<CyRow> rows;
		if (query == null && column == null) {
			rows = table.getAllRows();
		} else if(query == null || column == null) {
			logger.error("Missing parameter.");
			throw new WebApplicationException("Query parameters are incomplete.", 500);
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
	 * Get all networks in current session
	 * 
	 * @param column
	 * @param query
	 * @param format
	 * @return
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@ReturnType("java.util.List<org.cytoscape.rest.internal.model.CyNetworkWrapper>")
	public String getNetworks(@QueryParam("column") String column, @QueryParam("query") String query, @QueryParam("format") String format) {
		if(column == null && query == null) {
			return getNetworks(networkManager.getNetworkSet(), format);
		} else {
			return getNetworksByQuery(query, column, format);
		}
	}


	private final String getNetworksByQuery(final String query, final String column, final String format) {
		final Set<CyNetwork> networks = networkManager.getNetworkSet();
		final Set<CyNetwork> matchedNetworks = new HashSet<CyNetwork>();
		
		for(final CyNetwork network:networks) {
			final CyTable table=network.getDefaultNetworkTable();
			final Object rawQuery = MapperUtil.getRawValue(query, table.getColumn(column).getType());
			final Collection<CyRow> rows = table.getMatchingRows(column, rawQuery);
			if(rows.isEmpty() == false) {
				matchedNetworks.add(network);
			}
		}
		return getNetworks(matchedNetworks, format);
	}


	private final String getNetworks(final Set<CyNetwork> networks, final String format) {
		if(networks.isEmpty()) {
			return "[]";
		}

		StringBuilder result = new StringBuilder();
		result.append("[");
		
		

		for (final CyNetwork network : networks) {
			if(format == null) {
				result.append(getNetworkString(network));
			} else if(format.equals("SUID")) {
				result.append(network.getSUID());
			}
			result.append(",");
		}
		String jsonString = result.toString();
		jsonString = jsonString.substring(0, jsonString.length() - 1);

		return jsonString + "]";
	}


	/**
	 * Get a network in Cytoscape.js format
	 * 
	 * @param networkId Network SUID
	 * 
	 * @return Network with all associated table
	 * 
	 */
	@GET
	@Path("/{networkId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ReturnType("org.cytoscape.rest.internal.model.CyNetworkWrapper")
	public String getNetwork(@PathParam("networkId") Long networkId) {
		return getNetworkString(getCyNetwork(networkId));
	}


	/**
	 * Get all nodes as an array of SUIDs
	 * 
	 * @param id
	 * @return
	 */
	@GET
	@Path("/{networkId}/nodes")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Long> getNodes(@PathParam("networkId") Long networkId, @QueryParam("column") String column,
			@QueryParam("query") String query) {
		return getByQuery(networkId, "nodes", column, query);
	}

	/**
	 * 
	 * Get all edges as an array of SUIDs
	 * 
	 * @param id SUID of the network edges belong to.
	 * @param column
	 * @param query
	 * @return
	 */
	@GET
	@Path("/{networkId}/edges")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Long> getEdges(@PathParam("networkId") Long id, @QueryParam("column") String column,
			@QueryParam("query") String query) {
		return getByQuery(id, "edges", column, query);
	}


	/**
	 * 
	 * Get a node
	 * 
	 * @param networkId Network SUID
	 * @param nodeId Node SUID
	 * 
	 * @return Node with associated table data
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
			throw new WebApplicationException("Could not find node with SUID: " + nodeId);
		}
		return getGraphObject(network, node);
	}


	/**
	 * 
	 * Get an edge
	 * 
	 * @param networkId Network SUID
	 * @param edgeId Edge SUID
	 * 
	 * @return Edge with associated table data
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
			throw getError("Could not find edge with SUID: " + edgeId, Response.Status.NOT_FOUND);
		}
		return getGraphObject(network, edge);
	}


	/**
	 * Get source or target node of a edge
	 * 
	 * @param networkId Network SUID
	 * @param edgeId Edge SUID
	 * @param type source or target
	 * 
	 * @return SUID of the source/target node
	 * 
	 */
	@GET
	@Path("/{networkId}/edges/{edgeId}/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	public Long getEdgeComponent(@PathParam("networkId") Long networkId, @PathParam("edgeId") Long edgeId,
			@PathParam("type") String type) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyEdge edge = network.getEdge(edgeId);

		if (edge == null) {
			throw new NotFoundException("Could not find edge: " + edgeId);
		}

		Long nodeSUID = null;
		if (type.equals("source")) {
			nodeSUID = edge.getSource().getSUID();
		} else if (type.equals("target")) {
			nodeSUID = edge.getTarget().getSUID();
		} else {
			throw new WebApplicationException("Invalid parameter for edge: " + type, 500);
		}
		return nodeSUID;
	}

	
	/**
	 * Get the edge is directed or not.
	 * 
	 * @param networkId Network SUID
	 * @param edgeId Target edge SUID
	 * 
	 * @return True if the edge is directed.
	 * 
	 */
	@GET
	@Path("/{networkId}/edges/{edgeId}/isDirected")
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean getEdgeDirected(@PathParam("networkId") Long networkId, @PathParam("edgeId") Long edgeId) {
		final CyNetwork network = getCyNetwork(networkId);
		CyEdge edge = network.getEdge(edgeId);
		if (edge == null) {
			throw new NotFoundException("Could not find edge with SUID: " + edgeId);
		}
		return edge.isDirected();
	}


	/**
	 * 
	 * Get adjacent edges for a node
	 * 
	 * @param networkId Network SUID
	 * @param nodeId Target node SUID
	 * 
	 * @return List of edge SUIDs
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
	 * Get network pointer (nested network SUID)
	 * 
	 * @param networkId Network SUID.
	 * @param nodeId target node SUID.
	 * 
	 * @return Nested network SUID
	 */
	@GET
	@Path("/{networkId}/nodes/{nodeId}/pointer")
	@Produces(MediaType.APPLICATION_JSON)
	public Long getNetworkPointer(@PathParam("networkId") Long networkId, @PathParam("nodeId") Long nodeId) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyNode node = getNode(network, nodeId);
		final CyNetwork pointer = node.getNetworkPointer();
		if(pointer == null) {
			throw new NotFoundException("Could not find network pointer.");
		}
		return pointer.getSUID();
	}
	
	
	/**
	 * 
	 * Get first neighbors of node
	 * 
	 * @param networkId Target network SUID.
	 * @param nodeId Node SUID.
	 * 
	 * @return Neighbors as a list of SUIDs.
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
	 * Get array of SUIDs for given collection of graph objects.
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
	 * Add a new node to existing network
	 * 
	 * @param id
	 *            network SUID
	 * @param is
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/{networkId}/nodes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createNode(@PathParam("networkId") Long networkId, final InputStream is) throws Exception {
		final CyNetwork network = getCyNetwork(networkId);
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
	@Path("/{networkId}/edges")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createEdge(@PathParam("networkId") Long networkId, final InputStream is) throws Exception {
		final CyNetwork network = getCyNetwork(networkId);
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


	// //////////////// Delete //////////////////////////////////

	/**
	 * 
	 * Delete all networks
	 * 
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
	 * Delete a network
	 * 
	 * @param networkId
	 */
	@DELETE
	@Path("/{networkId}")
	public void deleteNetwork(@PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		this.networkManager.destroyNetwork(network);
	}


	/**
	 * Delete all nodes in a network
	 * 
	 * @param networkId target network ID.
	 */
	@DELETE
	@Path("/{networkId}/nodes")
	public void deleteAllNodes(@PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		network.removeNodes(network.getNodeList());
		updateViews(network);
	}

	

	/**
	 * Delete all edges in a network
	 * 
	 * @param networkId target network ID
	 */
	@DELETE
	@Path("/{networkId}/edges")
	public void deleteAllEdges(@PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		network.removeEdges(network.getEdgeList());
		updateViews(network);
	}

	
	/**
	 * Delete a node in a network
	 * 
	 * @param networkId
	 * @param nodeId
	 */
	@DELETE
	@Path("/{networkId}/nodes/{nodeId}")
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

	
	/**
	 * Delete an edge in a network.
	 * 
	 * @param networkId network ID
	 * @param edgeId SUID of an edge to be deleted
	 */
	@DELETE
	@Path("/{networkId}/edges/{edgeId}")
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
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createNetwork(@DefaultValue(DEF_COLLECTION_PREFIX) @QueryParam("collection") String collection,
			@QueryParam("source") String source, @QueryParam("format") String format,
			final InputStream is, @Context HttpHeaders headers) throws Exception {

		
		// 1. If source is URL, load from the array of URL
		if(source != null && source.equals("url")) {
			return loadNetwork(is);
		}
		
		// Check user agent if available
		final List<String> agent = headers.getRequestHeader("user-agent");
		String userAgent = "";
		if (agent != null) {
			userAgent = agent.get(0);
		}

		
		final TaskIterator it;
		if(format != null && format.trim().equals("edgelist")) {
			it = edgeListReaderFactory.createTaskIterator(is, "test123");
		} else {
			it = cytoscapeJsReaderFactory.createTaskIterator(is, "test123");
		}
		
		final CyNetworkReader reader = (CyNetworkReader) it.next();

		String collectionName = collection;
		if (collection.equals(DEF_COLLECTION_PREFIX)) {
			collectionName = collectionName + userAgent;
		}

		reader.run(new HeadlessTaskMonitor());

		CyNetwork[] networks = reader.getNetworks();
		CyNetwork newNetwork = networks[0];
		addNetwork(networks, reader, collectionName);
		is.close();

		// Return SUID-to-Original map
		return getNumberObjectString("networkSUID", newNetwork.getSUID());
	}

	
	/**
	 * Create a subnetwork from selected nodes & edges
	 * 
	 * if body is empty, it simply creates new network from current selection.
	 * Otherwise, select from the list of SUID.
	 * 
	 * @param networkId
	 * @param is
	 * @param headers
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/{networkId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createNetworkFromSelected(@PathParam("networkId") Long networkId,
			final InputStream is, @Context HttpHeaders headers) throws Exception {

		final CyNetwork network = getCyNetwork(networkId);
		final TaskIterator itr = newNetworkSelectedNodesAndEdgesTaskFactory.createTaskIterator(network);
	
		// TODO: This is very hackey... We need a method to get the new network SUID.
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
				e.printStackTrace();
				throw new WebApplicationException("Could not create sub network from selection.", 500);
			}
		}
		is.close();

		
		/**
		 * 
		 */
		if(viewTask != null) {
			final Collection result = ((ObservableTask)viewTask).getResults(Collection.class);
			if(result.size() == 1) {
				final Long suid = ((CyNetworkView)result.iterator().next()).getModel().getSUID();
				return getNumberObjectString("networkSUID", suid);
			}
	 	}
	 	
		throw new WebApplicationException("Could not get new network SUID.", 500);
	}
	
	private final String loadNetwork(final InputStream is) throws IOException {
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
					task.run(new HeadlessTaskMonitor());
					if (task instanceof CyNetworkReader) {
						currentReader = (CyNetworkReader) task;
					} else {
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			final CyNetwork[] networks = currentReader.getNetworks();
			final Long[] suids = new Long[networks.length];
			int counter = 0;
			for(CyNetwork network: networks) {
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
			
			for(final String url: results.keySet()) {
				generator.writeStartObject();

				generator.writeStringField("source", url);
				generator.writeArrayFieldStart("networkSUID");
				for(final Long suid: results.get(url)) {
					generator.writeNumber(suid);
				}
				generator.writeEndArray();
				
				generator.writeEndObject();
			}
			generator.writeEndArray();
			
			generator.close();
			result = stream.toString();
			stream.close();
		} catch (IOException e) {
			throw new WebApplicationException("Could not create object count.", 500);
		}

		return result;
	}


	private final String getNetworkString(final CyNetwork network) {
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		CyWriter writer = cytoscapeJsWriterFactory.createWriter(stream, network);
		String jsonString = null;
		try {
			writer.run(new HeadlessTaskMonitor());
			jsonString = stream.toString();
			stream.close();
		} catch (Exception e) {
			throw getError(e, Response.Status.PRECONDITION_FAILED);
		}
		return jsonString;
	}

	
	/**
	 * Add network to the manager
	 * 
	 * @param networks
	 * @param reader
	 * @param collectionName
	 */
	private final void addNetwork(final CyNetwork[] networks, 
			final CyNetworkReader reader, final String collectionName) {
		
		final VisualStyle style = vmm.getCurrentVisualStyle();
		final List<CyNetworkView> results = new ArrayList<CyNetworkView>();
		
		final Set<CyRootNetwork> rootNetworks = new HashSet<CyRootNetwork>();
		
		for (final CyNetwork net : networkManager.getNetworkSet()){
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
				//results.add(nullNetworkViewFactory.createNetworkView(network));
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