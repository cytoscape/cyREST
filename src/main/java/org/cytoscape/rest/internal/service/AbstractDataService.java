package org.cytoscape.rest.internal.service;

import java.io.IOException;

import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.rest.TaskFactoryManager;
import org.cytoscape.rest.internal.serializer.GraphObjectSerializer;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;



public abstract class AbstractDataService {

	@Context
	protected CyApplicationManager applicationManager;

	@Context
	protected CyNetworkManager networkManager;

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

	protected final GraphObjectSerializer serializer;
	

	public AbstractDataService() {
		this.serializer = new GraphObjectSerializer();
	}

	protected final CyNetwork getNetwork(final Long id) {
		final CyNetwork network = networkManager.getNetwork(id);
		if (network == null) {
			throw new WebApplicationException("Could not find network.", 404);
		}
		return network;
	}

	protected final CyNode getNode(final CyNetwork network, final Long nodeId) {
		final CyNode node = network.getNode(nodeId);
		if (node == null) {
			throw new WebApplicationException("Could not find object: " + nodeId, 404);
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
}