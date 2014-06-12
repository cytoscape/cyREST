package org.cytoscape.rest.internal;

//import org.cytoscape.rest.internal.net.server.CytoBridgePostResponder;
import java.util.Map;
import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.io.write.VizmapWriterFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.rest.TaskFactoryManager;
import org.cytoscape.rest.internal.task.CyBinder;
import org.cytoscape.rest.internal.task.GrizzlyServerManager;
import org.cytoscape.rest.internal.task.HeadlessTaskMonitor;
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
import org.cytoscape.work.TaskMonitor;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CyActivator extends AbstractCyActivator {

	private static final Logger logger = LoggerFactory.getLogger(CyActivator.class);

	private GrizzlyServerManager grizzlyServerManager = null;

	public CyActivator() {
		super();
	}
	
	public class WriterListener {
		
		private VizmapWriterFactory jsFactory;
		
		@SuppressWarnings("rawtypes")
		public void registerFactory(final VizmapWriterFactory factory, final Map props) {
			System.out.println("########### W = " + factory.getClass().getSimpleName());
			if(factory != null && factory.getClass().getSimpleName().equals("CytoscapeJsVisualStyleWriterFactory")) {
				this.jsFactory = factory;
			}
		}
		
		@SuppressWarnings("rawtypes")
		public void unregisterFactory(final VizmapWriterFactory factory, final Map props) {
	
		}
		
		public VizmapWriterFactory getFactory() {
			return this.jsFactory;
		}
	}

	public void start(BundleContext bc) {

		final TaskMonitor headlessTaskMonitor = new HeadlessTaskMonitor();
		// Importing Services:
		StreamUtil streamUtil = getService(bc, StreamUtil.class);
		CyNetworkFactory netFact = getService(bc, CyNetworkFactory.class);
		CyNetworkManager netMan = getService(bc, CyNetworkManager.class);
		CyNetworkViewFactory netViewFact = getService(bc, CyNetworkViewFactory.class);
		CyNetworkViewManager netViewMan = getService(bc, CyNetworkViewManager.class);
		VisualMappingManager visMan = getService(bc, VisualMappingManager.class);
		CyApplicationManager applicationManager = getService(bc, CyApplicationManager.class);
		CyLayoutAlgorithmManager layoutManager = getService(bc, CyLayoutAlgorithmManager.class);

		
		CyTableFactory tabFact = getService(bc, CyTableFactory.class);
		CyTableManager tableManager = getService(bc, CyTableManager.class);

		CyNetworkViewWriterFactory cytoscapeJsWriterFactory = getService(bc, CyNetworkViewWriterFactory.class,
				"(id=cytoscapejsNetworkWriterFactory)");

		InputStreamTaskFactory cytoscapeJsReaderFactory = getService(bc, InputStreamTaskFactory.class,
				"(id=cytoscapejsNetworkReaderFactory)");
		
		WriterListener writerListsner = new WriterListener();
		registerServiceListener(bc, writerListsner, "registerFactory", "unregisterFactory", VizmapWriterFactory.class);

		System.out.println("Writer = " + cytoscapeJsWriterFactory);
		System.out.println("Reader = " + cytoscapeJsReaderFactory);
		
		final TaskManager<?,?> tm = getService(bc, TaskManager.class);

		@SuppressWarnings("unchecked")
		final CyProperty<Properties> cyPropertyServiceRef = getService(bc, CyProperty.class,
				"(cyPropertyName=cytoscape3.props)");

		NodeViewListener listen = new NodeViewListener(visMan, layoutManager, tm, cyPropertyServiceRef);
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

		// Start REST Server
		final CyBinder binder = new CyBinder(netMan, netViewMan, netFact, taskFactoryManagerManager,
				applicationManager, visMan, cytoscapeJsWriterFactory, cytoscapeJsReaderFactory, layoutManager, writerListsner, 
				headlessTaskMonitor, tableManager);
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
		if (grizzlyServerManager != null) {
			grizzlyServerManager.stopServer();
		}
	}
}