package org.cytoscape.rest.internal.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.rest.TaskFactoryManager;
import org.cytoscape.rest.internal.CyActivator.WriterListener;
import org.cytoscape.rest.internal.reader.EdgeListReaderFactory;
import org.cytoscape.rest.internal.serializer.GraphObjectSerializer;
import org.cytoscape.task.create.NewNetworkSelectedNodesAndEdgesTaskFactory;
import org.cytoscape.task.read.LoadNetworkURLTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;


/**
 * Prepare services to be injected.
 *
 */
public abstract class AbstractDataService {

	// TODO: do we need this level of version granularity?
	protected static final String API_VERSION = "v1";
	
	protected static final String ERROR_TAG = "\"error\":";
	

	// Utilities to build error messages.
	
	protected final WebApplicationException getError(final String errorMessage, final Status status) {
		return new NotFoundException(Response.status(status)
				.entity("{" + ERROR_TAG + "\"" + errorMessage + "\"}").build());
	}
	
	protected final WebApplicationException getError(final Exception ex, final Status status) {
		return new NotFoundException(Response.status(status).entity(ex).build());
	}


	@Context
	protected CyApplicationManager applicationManager;

	@Context
	protected CyNetworkManager networkManager;

	@Context
	protected CyRootNetworkManager cyRootNetworkManager;
	
	@Context
	protected CyTableManager tableManager;
	
	@Context
	protected CyNetworkViewManager networkViewManager;

	@Context
	protected CyNetworkFactory networkFactory;

	@Context
	protected TaskFactoryManager tfManager;

	@Context
	protected InputStreamTaskFactory cytoscapeJsReaderFactory;

	@Context
	protected VisualMappingManager vmm;

	@Context
	protected CyNetworkViewWriterFactory cytoscapeJsWriterFactory;
	
	@Context
	protected WriterListener vizmapWriterFactoryListener;
	
	@Context
	protected LoadNetworkURLTaskFactory loadNetworkURLTaskFactory;
	
	@Context
	protected CyProperty props;

	@Context
	protected NewNetworkSelectedNodesAndEdgesTaskFactory newNetworkSelectedNodesAndEdgesTaskFactory;

	@Context
	protected EdgeListReaderFactory edgeListReaderFactory;


	protected final GraphObjectSerializer serializer;


	public AbstractDataService() {
		this.serializer = new GraphObjectSerializer();
	}


	protected final CyNetwork getCyNetwork(final Long id) {
		if(id == null) {
			throw getError("SUID is null.", Response.Status.NOT_FOUND);
		}
		
		final CyNetwork network = networkManager.getNetwork(id);
		if (network == null) {
			throw getError("Could not find network with SUID: " + id, Response.Status.NOT_FOUND);
		}
		return network;
	}


	protected final Collection<CyNetworkView> getCyNetworkViews(final Long id) {
		final Collection<CyNetworkView> views = networkViewManager.getNetworkViews(getCyNetwork(id));
		if (views.isEmpty()) {
			throw new WebApplicationException("Now view available for network: " + id, 404);
		}
		return views;
	}


	protected final CyNode getNode(final CyNetwork network, final Long nodeId) {
		final CyNode node = network.getNode(nodeId);
		if (node == null) {
			throw getError("Could not find node with SUID: " + nodeId, Response.Status.NOT_FOUND);
		}
		return node;
	}


	protected final String getGraphObject(final CyNetwork network, final CyIdentifiable obj) {
		final CyRow row = network.getRow(obj);

		try {
			return this.serializer.serializeGraphObject(obj, row);
		} catch (IOException e) {
			throw new WebApplicationException(e, 500);
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
		result = stream.toString();
		stream.close();
		return result;
	}


	protected final String getNumberObjectString(final String fieldName, final Number value) {
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
			result = stream.toString();
			stream.close();
		} catch (IOException e) {
			throw new WebApplicationException("Could not create object count.", 500);
		}

		return result;
	}
}