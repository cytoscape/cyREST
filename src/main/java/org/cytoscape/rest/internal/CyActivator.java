package org.cytoscape.rest.internal;

//import org.cytoscape.rest.internal.net.server.CytoBridgePostResponder;
import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.io.BasicCyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.internal.write.json.JSONNetworkWriterFactory;
import org.cytoscape.io.internal.write.json.JSONVisualStyleWriterFactory;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.rest.TaskFactoryManager;
import org.cytoscape.rest.internal.task.CyBinder;
import org.cytoscape.rest.internal.task.GrizzlyServerManager;
import org.cytoscape.rest.internal.translator.CyJacksonModule;
import org.cytoscape.rest.internal.translator.CytoscapejsModule;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.NetworkCollectionTaskFactory;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.events.AddedNodeViewsListener;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CyActivator extends AbstractCyActivator {

	private static final Logger logger = LoggerFactory.getLogger(CyActivator.class);
	
	private GrizzlyServerManager grizzlyServerManager = null; 
	
	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {

		// Importing Services:
		StreamUtil streamUtil = getService(bc, StreamUtil.class);
		CyNetworkFactory netFact = getService(bc, CyNetworkFactory.class);
		CyNetworkManager netMan = getService(bc, CyNetworkManager.class);
		CyNetworkViewFactory netViewFact = getService(bc, CyNetworkViewFactory.class);
		CyNetworkViewManager netViewMan = getService(bc, CyNetworkViewManager.class);
		VisualMappingManager visMan = getService(bc, VisualMappingManager.class);
		CyApplicationManager applicationManager = getService(bc, CyApplicationManager.class);

		CyTableFactory tabFact = getService(bc, CyTableFactory.class);
		CyTableManager tabMan = getService(bc, CyTableManager.class);

		CyLayoutAlgorithmManager layMan = getService(bc, CyLayoutAlgorithmManager.class);
		
		CyNetworkViewWriterFactory cytoscapeJsWriterFactory = 
				getService(bc, CyNetworkViewWriterFactory.class, "(id=cytoscapejsNetworkWriterFactory)"); 

		InputStreamTaskFactory cytoscapeJsReaderFactory = 
				getService(bc, InputStreamTaskFactory.class, "(id=cytoscapejsNetworkReaderFactory)"); 
		
		System.out.println("Writer = " + cytoscapeJsWriterFactory);
		System.out.println("Reader = " + cytoscapeJsReaderFactory);
		
		
		TaskManager tm = getService(bc, TaskManager.class);
		@SuppressWarnings("unchecked")
		final CyProperty<Properties> cyPropertyServiceRef = getService(bc, CyProperty.class,
				"(cyPropertyName=cytoscape3.props)");

		NodeViewListener listen = new NodeViewListener(visMan, layMan, tm, cyPropertyServiceRef);
		registerService(bc, listen, AddedNodeViewsListener.class, new Properties());

		CySwingApplication swingApp = getService(bc, CySwingApplication.class);
		final TaskFactoryManager taskFactoryManagerManager = new TaskFactoryManagerImpl();
		// Get all compatible tasks
		registerServiceListener(bc, taskFactoryManagerManager, "addTaskFactory", "removeTaskFactory", TaskFactory.class);
		registerServiceListener(bc, taskFactoryManagerManager, "addNetworkTaskFactory", "removeNetworkTaskFactory",
				NetworkTaskFactory.class);
		registerServiceListener(bc, taskFactoryManagerManager, "addNetworkCollectionTaskFactory",
				"removeNetworkCollectionTaskFactory", NetworkCollectionTaskFactory.class);

	

		// ///////////////// Writers ////////////////////////////
		final ObjectMapper jsMapper = new ObjectMapper();
		jsMapper.registerModule(new CytoscapejsModule());

		final ObjectMapper graphsonMapper = new ObjectMapper();
		graphsonMapper.registerModule(new CytoscapejsModule());

		final ObjectMapper fullJsonMapper = new ObjectMapper();
		fullJsonMapper.registerModule(new CyJacksonModule());

		final BasicCyFileFilter cytoscapejsFilter = new BasicCyFileFilter(new String[] { "json" },
				new String[] { "application/json" }, "cytoscape.js JSON files", DataCategory.NETWORK, streamUtil);
		final BasicCyFileFilter fullJsonFilter = new BasicCyFileFilter(new String[] { "json" },
				new String[] { "application/json" }, "Cytoscape JSON files", DataCategory.NETWORK, streamUtil);

		final BasicCyFileFilter vizmapJsonFilter = new BasicCyFileFilter(new String[] { "json" },
				new String[] { "application/json" }, "cytoscape.js Visual Style JSON files", DataCategory.VIZMAP,
				streamUtil);

		final JSONNetworkWriterFactory jsonWriterFactory = new JSONNetworkWriterFactory(cytoscapejsFilter, jsMapper);
		registerAllServices(bc, jsonWriterFactory, new Properties());

		final JSONVisualStyleWriterFactory jsonVSWriterFactory = new JSONVisualStyleWriterFactory(vizmapJsonFilter,
				jsMapper);
		registerAllServices(bc, jsonVSWriterFactory, new Properties());
		
		
		// Start REST Server
		final CyBinder binder = new CyBinder(netMan, netFact, taskFactoryManagerManager, applicationManager, cytoscapeJsWriterFactory, cytoscapeJsReaderFactory);
		this.grizzlyServerManager = new GrizzlyServerManager(binder);
		try {
			this.grizzlyServerManager.startServer();
		} catch (Exception e) {
			logger.error("############## Could not start server ###############", e);
			e.printStackTrace();
		}
	}
	
	@Override
	public void shutDown() {
		System.out.println("Shutting down server...");
		if(grizzlyServerManager != null) {
			grizzlyServerManager.stopServer();
		}
	}
}