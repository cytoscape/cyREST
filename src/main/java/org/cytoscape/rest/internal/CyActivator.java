package org.cytoscape.rest.internal;

import java.util.Map;
import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.io.BasicCyFileFilter;
import org.cytoscape.io.DataCategory;
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
import org.cytoscape.rest.internal.reader.EdgeListReaderFactory;
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
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskMonitor;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CyActivator extends AbstractCyActivator {

	private static final Logger logger = LoggerFactory.getLogger(CyActivator.class);

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

	private GrizzlyServerManager grizzlyServerManager = null;

	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {

		final MappingFactoryManager mappingFactoryManager = new MappingFactoryManager();
		registerServiceListener(bc, mappingFactoryManager, "addFactory", "removeFactory",
				VisualMappingFunctionFactory.class);

		@SuppressWarnings("unchecked")
		final CyProperty<Properties> cyPropertyServiceRef = getService(bc, CyProperty.class,
				"(cyPropertyName=cytoscape3.props)");
		final TaskMonitor headlessTaskMonitor = new HeadlessTaskMonitor();
		final CyNetworkFactory netFact = getService(bc, CyNetworkFactory.class);
		final CyNetworkViewFactory netViewFact = getService(bc, CyNetworkViewFactory.class);
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
		final CyTableFactory tableFactory = getService(bc, CyTableFactory.class);
		final StreamUtil streamUtil = getService(bc, StreamUtil.class);

		// Task factories
		final NewNetworkSelectedNodesAndEdgesTaskFactory networkSelectedNodesAndEdgesTaskFactory = getService(bc,
				NewNetworkSelectedNodesAndEdgesTaskFactory.class);
		final CyNetworkViewWriterFactory cytoscapeJsWriterFactory = getService(bc, CyNetworkViewWriterFactory.class,
				"(id=cytoscapejsNetworkWriterFactory)");
		final InputStreamTaskFactory cytoscapeJsReaderFactory = getService(bc, InputStreamTaskFactory.class,
				"(id=cytoscapejsNetworkReaderFactory)");
		final LoadNetworkURLTaskFactory loadNetworkURLTaskFactory = getService(bc, LoadNetworkURLTaskFactory.class);

		final NetworkTaskFactory fitContent = getService(bc, NetworkTaskFactory.class, "(title=Fit Content)");
		final NetworkTaskFactory edgeBundler = getService(bc, NetworkTaskFactory.class, "(title=All Nodes and Edges)");

		final RenderingEngineManager renderingEngineManager = getService(bc,RenderingEngineManager.class);

		final WriterListener writerListsner = new WriterListener();
		registerServiceListener(bc, writerListsner, "registerFactory", "unregisterFactory", VizmapWriterFactory.class);

		final TaskFactoryManager taskFactoryManagerManager = new TaskFactoryManagerImpl();

		// Get all compatible tasks
		registerServiceListener(bc, taskFactoryManagerManager, "addTaskFactory", "removeTaskFactory", TaskFactory.class);
		registerServiceListener(bc, taskFactoryManagerManager, "addNetworkTaskFactory", "removeNetworkTaskFactory",
				NetworkTaskFactory.class);
		registerServiceListener(bc, taskFactoryManagerManager, "addNetworkCollectionTaskFactory",
				"removeNetworkCollectionTaskFactory", NetworkCollectionTaskFactory.class);

		// Extra readers and writers
		final BasicCyFileFilter elFilter = new BasicCyFileFilter(new String[] { "el" },
				new String[] { "text/edgelist" }, "Edgelist files", DataCategory.NETWORK, streamUtil);
		final EdgeListReaderFactory edgeListReaderFactory = new EdgeListReaderFactory(elFilter, netViewFact, netFact,
				netMan, cyRootNetworkManager);
		final Properties edgeListReaderFactoryProps = new Properties();
		edgeListReaderFactoryProps.setProperty("ID", "edgeListReaderFactory");
		registerService(bc, edgeListReaderFactory, InputStreamTaskFactory.class, edgeListReaderFactoryProps);

		// Start REST Server
		final CyBinder binder = new CyBinder(netMan, netViewMan, netFact, taskFactoryManagerManager,
				applicationManager, visMan, cytoscapeJsWriterFactory, cytoscapeJsReaderFactory, layoutManager,
				writerListsner, headlessTaskMonitor, tableManager, vsFactory, mappingFactoryManager, groupFactory,
				groupManager, cyRootNetworkManager, loadNetworkURLTaskFactory, cyPropertyServiceRef,
				networkSelectedNodesAndEdgesTaskFactory, edgeListReaderFactory, netViewFact, tableFactory, fitContent,
				new EdgeBundlerImpl(edgeBundler), renderingEngineManager);
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
		logger.info("Shutting down REST server...");
		if (grizzlyServerManager != null) {
			grizzlyServerManager.stopServer();
		}
	}

	class EdgeBundlerImpl implements EdgeBundler {

		private final NetworkTaskFactory bundler;

		public EdgeBundlerImpl(final NetworkTaskFactory tf) {
			this.bundler = tf;
		}

		@Override
		public NetworkTaskFactory getBundlerTF() {
			return bundler;
		}

	}
}