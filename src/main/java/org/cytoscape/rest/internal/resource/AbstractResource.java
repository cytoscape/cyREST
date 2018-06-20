package org.cytoscape.rest.internal.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.ci.CIErrorFactory;
import org.cytoscape.ci.CIExceptionFactory;
import org.cytoscape.ci.CIResponseFactory;
import org.cytoscape.ci.model.CIError;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.rest.internal.TaskFactoryManager;
import org.cytoscape.rest.internal.CyActivator.WriterListener;
import org.cytoscape.rest.internal.CyNetworkViewWriterFactoryManager;
import org.cytoscape.rest.internal.CyRESTConstants;
import org.cytoscape.rest.internal.datamapper.MapperUtil;
import org.cytoscape.rest.internal.reader.EdgeListReaderFactory;
import org.cytoscape.rest.internal.serializer.GraphObjectSerializer;
import org.cytoscape.rest.internal.task.CyRESTPort;
import org.cytoscape.rest.internal.task.CytoscapeJsReaderFactory;
import org.cytoscape.rest.internal.task.CytoscapeJsWriterFactory;
import org.cytoscape.rest.internal.task.HeadlessTaskMonitor;
import org.cytoscape.task.create.NewNetworkSelectedNodesAndEdgesTaskFactory;
import org.cytoscape.task.read.LoadNetworkURLTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.inject.Inject;

/**
 * Prepare services to be injected.
 * 
 */
public abstract class AbstractResource {

	// TODO: do we need this level of version granularity?
	protected static final String API_VERSION = "v1";
	protected static final String ERROR_TAG = "\"error\":";

	protected static final String NETWORK_QUERY_DESCRIPTION = "If the `column` and `query` parameters are specified, the results will be limited to rows in the network table where the value in the column specified by the `column` parameter matches the value specified by the `query` parameter.";
	protected static final String NODE_QUERY_DESCRIPTION = "If the `column` and `query` parameters are specified, the results will be limited to rows in the node table where the value in the column specified by the `column` parameter matches the value specified by the `query` parameter.";
	protected static final String EDGE_QUERY_DESCRIPTION = "If the `column` and `query` parameters are specified, the results will be limited to rows in the edge table where the value in the column specified by the `column` parameter matches the value specified by the `query` parameter.";
	
	protected static final String COLUMN_DESCRIPTION = "The name of the column that will be queried for matches.";
	protected static final String QUERY_STRING_DESCRIPTION = "The value to be matched.";

	public abstract String getResourceURI();
	
	public abstract Logger getResourceLogger();

	@Inject
	@NotNull
	protected CyApplicationManager applicationManager;

	@Inject
	protected CyNetworkManager networkManager;

	@Inject
	protected CyRootNetworkManager cyRootNetworkManager;

	@Inject
	protected CyTableManager tableManager;

	@Inject
	protected CyNetworkViewManager networkViewManager;

	@Inject
	protected CyNetworkFactory networkFactory;

	@Inject
	protected CyNetworkViewFactory networkViewFactory;

	@Inject
	protected TaskFactoryManager tfManager;

	@Inject
	@CytoscapeJsReaderFactory
	protected ServiceTracker cytoscapeJsReaderFactory;

	@Inject
	@NotNull
	protected VisualMappingManager vmm;

	@Inject
	@CytoscapeJsWriterFactory
	protected ServiceTracker cytoscapeJsWriterFactory;
	
	@Inject
	protected CyNetworkViewWriterFactoryManager viewWriterFactoryManager;

	@Inject
	protected WriterListener vizmapWriterFactoryListener;

	@Inject
	protected LoadNetworkURLTaskFactory loadNetworkURLTaskFactory;

	@Inject
	protected CyProperty<Properties> props;

	@Inject
	protected NewNetworkSelectedNodesAndEdgesTaskFactory newNetworkSelectedNodesAndEdgesTaskFactory;

	@Inject
	protected EdgeListReaderFactory edgeListReaderFactory;
	
	@Inject
	@CyRESTPort
	protected String cyRESTPort;
	
	@Inject
	protected CIResponseFactory ciResponseFactory;
	
	@Inject
	protected CIErrorFactory ciErrorFactory;
	
	@Inject
	protected CIExceptionFactory ciExceptionFactory;
	
	protected final GraphObjectSerializer serializer;

	public AbstractResource() {
		this.serializer = new GraphObjectSerializer();
	}

	protected final WebApplicationException getCIWebApplicationException(int status, String resourceURI, int code, String message, Logger logger, Exception e) {
		String errorURI = CyRESTConstants.CY_REST_CI_ROOT + ":" + resourceURI + ":" + CyRESTConstants.CY_REST_CI_ERROR_ROOT + ":"+ code;
		CIError ciError = ciErrorFactory.getCIError(status, errorURI, message);
		
		if (logger != null) {
			if (e != null)
			{
				logger.error(message, e);
			}
			else
			{
				logger.error(message);
			}
		}
		return ciExceptionFactory.getCIException(status, new CIError[]{ciError});
	}
	
	protected final CyNetwork getCyNetwork(int networkNotFoundErrorCode, final Long id) {
		if (id == null) {
			//throw new NotFoundException("SUID is null.");
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					networkNotFoundErrorCode, 
					"Could not find Network: SUID is null.", 
					getResourceLogger(), null);
		}

		final CyNetwork network = networkManager.getNetwork(id);
		if (network == null) {
			//throw new NotFoundException("Could not find Network with SUID: " + id);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					networkNotFoundErrorCode, 
					"Could not find Network with SUID: " + id, 
					getResourceLogger(), null);
		}
		return network;
	}

	protected final Collection<CyNetworkView> getCyNetworkViews(int networkNotFoundErrorCode, int noViewsForNetworkErrorCode, final Long id) {
		final Collection<CyNetworkView> views = networkViewManager.getNetworkViews(getCyNetwork(networkNotFoundErrorCode, id));
		if (views.isEmpty()) {
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					noViewsForNetworkErrorCode, 
					"No Views Available for Network with SUID: " + id, 
					getResourceLogger(), null);
			//throw new NotFoundException("No view is available for network with SUID: " + id);
		}
		return views;
	}

	protected final CyNode getNode(int nodeNotFoundErrorCode, final CyNetwork network, final Long nodeId) {
		final CyNode node = network.getNode(nodeId);
		if (node == null) {
			//throw new NotFoundException("Could not find node with SUID: " + nodeId);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					nodeNotFoundErrorCode, 
					"Could not find Node with SUID: " + nodeId, 
					getResourceLogger(), null);
		}
		return node;
	}

	protected final String getGraphObject(int serializationErrorCode, final CyNetwork network, final CyIdentifiable obj) {
		final CyRow row = network.getRow(obj);

		try {
			return this.serializer.serializeGraphObject(obj, row);
		} catch (IOException e) {
			//throw getError("Could not serialize graph object with SUID: " + obj.getSUID(), e,
			//		Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					serializationErrorCode, 
					"Could not serialize Graph Object with SUID: " + obj.getSUID(), 
					getResourceLogger(), e);
		}
	}

	protected final Collection<Long> getByQuery(int couldNotFindNetworkErrorCode, int invalidObjectTypeErrorCode, int invalidParameterErrorCode, final Long id, final String objType, final String column,
			final String query) {
		final CyNetwork network = getCyNetwork(couldNotFindNetworkErrorCode, id);
		CyTable table = null;

		List<? extends CyIdentifiable> graphObjects;
		if (objType.equals("nodes")) {
			table = network.getDefaultNodeTable();
			graphObjects = network.getNodeList();
		} else if (objType.equals("edges")) {
			table = network.getDefaultEdgeTable();
			graphObjects = network.getEdgeList();
		} else {
			//throw getError("Invalid graph object type: " + objType, new IllegalArgumentException(),
			//		Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					invalidObjectTypeErrorCode, 
					"Invalid Graph Object Type: " + objType, 
					getResourceLogger(), null);
		}

		if (query == null && column == null) {
			// Simply return rows
			return graphObjects.stream()
					.map(obj->obj.getSUID())
					.collect(Collectors.toList());
		} else if (query == null || column == null) {
			//throw getError("Missing query parameter.", new IllegalArgumentException(),
			//		Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					invalidParameterErrorCode, 
					"Missing Query Parameter; query and column cannot be null", 
					getResourceLogger(), null);
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
	
	protected final String getNames(final Collection<String> names) throws IOException {
		final JsonFactory factory = new JsonFactory();

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = null;
		generator = factory.createGenerator(stream);
		generator.writeStartArray();

		for (final String name : names) {
			generator.writeString(name);
		}

		generator.writeEndArray();
		generator.close();
		result = stream.toString("UTF-8");
		stream.close();
		return result;
	}

	protected final String getNumberObjectString(int serializationErrorCode, final String fieldName, final Number value) {
		final JsonFactory factory = new JsonFactory();

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = null;
		try {
			generator = factory.createGenerator(stream);
			generator.writeStartObject();
			generator.writeFieldName(fieldName);
			generator.writeNumber(value.longValue());
			generator.writeEndObject();
			generator.close();
			result = stream.toString("UTF-8");
			stream.close();
		} catch (IOException e) {
			//throw getError("Could not serialize number: " + value, e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					serializationErrorCode, 
					"Could not serialize number: " + value, 
					getResourceLogger(), e);
		}

		return result;
	}
	
	protected final Set<CyNetwork> getNetworksByQuery(int invalidParameterErrorCode, final String query, final String column) throws WebApplicationException {
		
		if (column == null && query == null) {
			
		} else {
			if (column == null || column.length() == 0) {
				//throw getError("Column name parameter is missing.",
				//		new IllegalArgumentException(),
				//		Response.Status.INTERNAL_SERVER_ERROR);
				throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						getResourceURI(), 
						invalidParameterErrorCode, 
						"Column parameter is missing.", 
						getResourceLogger(), null);
			}
			if (query == null || query.length() == 0) {
				//throw getError("Query parameter is missing.",
				//		new IllegalArgumentException(),
				//		Response.Status.INTERNAL_SERVER_ERROR);
				throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						getResourceURI(), 
						invalidParameterErrorCode, 
						"Query parameter is missing.", 
						getResourceLogger(), null);
			}
			
		}
		
		final Set<CyNetwork> networks = networkManager.getNetworkSet();
		final Set<CyNetwork> matchedNetworks = new HashSet<>();

		for (final CyNetwork network : networks) {
			// First, check shared table
			CyTable table = network.getDefaultNetworkTable();
			if(table.getColumn(column) == null) {
				table = network.getTable(CyNetwork.class, CyNetwork.LOCAL_ATTRS);
			}
			// If column does not exists in the default table, check local one:
			if(table.getColumn(column) == null) {
				// Still not found?  Give up and continue.
				// TODO: should we check hidden tables, too?
				continue;
			}
			final Object rawQuery = MapperUtil.getRawValue(query, table.getColumn(column).getType());
			final Collection<CyRow> rows = table.getMatchingRows(column, rawQuery);
			if (rows.isEmpty() == false) {
				matchedNetworks.add(network);
			}
		}
		return matchedNetworks;
	}
	
	
	protected final String getNetworkString(int serviceUnavailableErrorCode, int serializationErrorCode, final CyNetwork network) {
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		CyNetworkViewWriterFactory cytoscapeJsWriterFactory = (CyNetworkViewWriterFactory) this.cytoscapeJsWriterFactory.getService();
		if (cytoscapeJsWriterFactory == null)
		{
			//throw getError("Cytoscape js writer factory is unavailable.", new IllegalStateException(), Response.Status.SERVICE_UNAVAILABLE);
			throw this.getCIWebApplicationException(Status.SERVICE_UNAVAILABLE.getStatusCode(), 
					getResourceURI(), 
					serviceUnavailableErrorCode, 
					"No Cytoscape js writer available", 
					getResourceLogger(), null);
		}
		CyWriter writer = cytoscapeJsWriterFactory.createWriter(stream, network);
		String jsonString = null;
		try {
			writer.run(new HeadlessTaskMonitor());
			jsonString = stream.toString("UTF-8");
			stream.close();
		} catch (Exception e) {
			//throw getError("Could not serialize network into JSON.", e, Response.Status.PRECONDITION_FAILED);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					serializationErrorCode, 
					"Could not serialize network into JSON.", 
					getResourceLogger(), e);
		}
		
		return jsonString;
	}
	
	protected final VisualLexicon getLexicon(int noVisualLexiconErrorCode) {
		final Set<VisualLexicon> lexicon = vmm.getAllVisualLexicon();
		if (lexicon.isEmpty()) {
			//throw getError("Could not find Visual Lexicon.", new IllegalStateException(), Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					noVisualLexiconErrorCode, 
					"Could not find Visual Lexicon", 
					getResourceLogger(), null);
		}
		
		// TODO: What happens if we have multiple renderer?
		return lexicon.iterator().next();
	}
	
}