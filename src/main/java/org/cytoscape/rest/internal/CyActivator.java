package org.cytoscape.rest.internal;

import java.util.Map;
import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.io.write.VizmapWriterFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.rest.TaskFactoryManager;
import org.cytoscape.rest.internal.task.CyBinder;
import org.cytoscape.rest.internal.task.GrizzlyServerManager;
import org.cytoscape.rest.internal.task.HeadlessTaskMonitor;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.NetworkCollectionTaskFactory;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.task.create.NewNetworkSelectedNodesAndEdgesTaskFactory;
import org.cytoscape.task.read.LoadNetworkURLTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.events.AddedNodeViewsListener;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
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
			if (factory != null && factory.getClass().getSimpleName().equals("CytoscapeJsVisualStyleWriterFactory")) {
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

		final MappingFactoryManager mappingFactoryManager = new MappingFactoryManager();
		registerServiceListener(bc, mappingFactoryManager, "addFactory", "removeFactory",
				VisualMappingFunctionFactory.class);

		@SuppressWarnings("unchecked")
		final CyProperty<Properties> cyPropertyServiceRef = getService(bc, CyProperty.class, "(cyPropertyName=cytoscape3.props)");
		final TaskMonitor headlessTaskMonitor = new HeadlessTaskMonitor();
		final CyNetworkFactory netFact = getService(bc, CyNetworkFactory.class);
		final CyNetworkManager netMan = getService(bc, CyNetworkManager.class);
		final CyRootNetworkManager cyRootNetworkManager = getService(bc, CyRootNetworkManager.class);
		final CyNetworkViewManager netViewMan = getService(bc, CyNetworkViewManager.class);
		final VisualMappingManager visMan = getService(bc, VisualMappingManager.class);
		final CyApplicationManager applicationManager = getService(bc, CyApplicationManager.class);
		final CyLayoutAlgorithmManager layoutManager = getService(bc, CyLayoutAlgorithmManager.class);
		final VisualStyleFactory vsFactory = getService(bc, VisualStyleFactory.class);
		final CyGroupFactory groupFactory = getService(bc, CyGroupFactory.class);
		final CyGroupManager groupManager = getService(bc, CyGroupManager.class);
		final CyTableManager tableManager = getService(bc, CyTableManager.class);
		final TaskManager<?, ?> tm = getService(bc, TaskManager.class);

		// Task factories
		final NewNetworkSelectedNodesAndEdgesTaskFactory networkSelectedNodesAndEdgesTaskFactory = getService(bc, NewNetworkSelectedNodesAndEdgesTaskFactory.class);
		final CyNetworkViewWriterFactory cytoscapeJsWriterFactory = getService(bc, CyNetworkViewWriterFactory.class, "(id=cytoscapejsNetworkWriterFactory)");
		final InputStreamTaskFactory cytoscapeJsReaderFactory = getService(bc, InputStreamTaskFactory.class, "(id=cytoscapejsNetworkReaderFactory)");
		final LoadNetworkURLTaskFactory loadNetworkURLTaskFactory = getService(bc, LoadNetworkURLTaskFactory.class);

		final WriterListener writerListsner = new WriterListener();
		registerServiceListener(bc, writerListsner, "registerFactory", "unregisterFactory", VizmapWriterFactory.class);

		final NodeViewListener listen = new NodeViewListener(visMan, layoutManager, tm, cyPropertyServiceRef);
		registerService(bc, listen, AddedNodeViewsListener.class, new Properties());

		final TaskFactoryManager taskFactoryManagerManager = new TaskFactoryManagerImpl();
		
		// Get all compatible tasks
		registerServiceListener(bc, taskFactoryManagerManager, "addTaskFactory", "removeTaskFactory", TaskFactory.class);
		registerServiceListener(bc, taskFactoryManagerManager, "addNetworkTaskFactory", "removeNetworkTaskFactory", NetworkTaskFactory.class);
		registerServiceListener(bc, taskFactoryManagerManager, "addNetworkCollectionTaskFactory", "removeNetworkCollectionTaskFactory", NetworkCollectionTaskFactory.class);

		// Start REST Server
		final CyBinder binder = new CyBinder(netMan, netViewMan, netFact, taskFactoryManagerManager,
				applicationManager, visMan, cytoscapeJsWriterFactory, cytoscapeJsReaderFactory, layoutManager,
				writerListsner, headlessTaskMonitor, tableManager, vsFactory, mappingFactoryManager, groupFactory,
				groupManager, cyRootNetworkManager, loadNetworkURLTaskFactory, cyPropertyServiceRef, networkSelectedNodesAndEdgesTaskFactory);
		this.grizzlyServerManager = new GrizzlyServerManager(binder, cyPropertyServiceRef);
		try {
			this.grizzlyServerManager.startServer();
		} catch (Exception e) {
			logger.error("Could not start server!", e);
			e.printStackTrace();
		}
	}


	@Override
	public void shutDown() {
		logger.info("Shutting down server...");
		if (grizzlyServerManager != null) {
			grizzlyServerManager.stopServer();
		}
	}

}