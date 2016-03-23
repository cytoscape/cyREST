package org.cytoscape.rest.internal;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.command.AvailableCommands;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.io.BasicCyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.io.write.PresentationWriterFactory;
import org.cytoscape.io.write.VizmapWriterFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.rest.internal.reader.EdgeListReaderFactory;
import org.cytoscape.rest.internal.task.CyBinder;
import org.cytoscape.rest.internal.task.GrizzlyServerManager;
import org.cytoscape.rest.internal.task.HeadlessTaskMonitor;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.task.NetworkCollectionTaskFactory;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.task.create.NewNetworkSelectedNodesAndEdgesTaskFactory;
import org.cytoscape.task.create.NewSessionTaskFactory;
import org.cytoscape.task.read.LoadNetworkURLTaskFactory;
import org.cytoscape.task.read.OpenSessionTaskFactory;
import org.cytoscape.task.select.SelectFirstNeighborsTaskFactory;
import org.cytoscape.task.write.ExportNetworkViewTaskFactory;
import org.cytoscape.task.write.SaveSessionAsTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskMonitor;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CyActivator extends AbstractCyActivator {

	private static final Logger logger = LoggerFactory.getLogger(CyActivator.class);
	private static final Integer MAX_RETRY = 5;
	private static final Integer INTERVAL = 5000; // Seconds

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

		System.out.println("======= cyREST Initialization start ======");
		logger.info("Initializing cyREST API server...");
		long start = System.currentTimeMillis();
	
		// Start Grizzly server in separate thread
		final ExecutorService service = Executors.newSingleThreadExecutor();
		service.submit(()-> {
			try {
				this.initDependencies(bc);
				
				this.grizzlyServerManager.startServer();
			} catch (Exception e) {
				e.printStackTrace();
				logger.warn("Failed to initialize cyREST server.", e);
			}
		});
		logger.info("cyREST dependency import took: " + (System.currentTimeMillis() - start) + " msec.");
	}
	
	
	private final void initDependencies(final BundleContext bc) throws Exception {

		final MappingFactoryManager mappingFactoryManager = new MappingFactoryManager();
		registerServiceListener(bc, mappingFactoryManager, "addFactory", "removeFactory",
				VisualMappingFunctionFactory.class);
		
		final GraphicsWriterManager graphicsWriterManager = new GraphicsWriterManager();
		registerServiceListener(bc, graphicsWriterManager, "addFactory", "removeFactory",
				PresentationWriterFactory.class);

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
		final CySessionManager sessionManager = getService(bc, CySessionManager.class); 
		final SaveSessionAsTaskFactory saveSessionAsTaskFactory = getService(bc, SaveSessionAsTaskFactory.class);
		final OpenSessionTaskFactory openSessionTaskFactory = getService(bc, OpenSessionTaskFactory.class);
		final NewSessionTaskFactory newSessionTaskFactory = getService(bc, NewSessionTaskFactory.class);
		final CySwingApplication desktop = getService(bc, CySwingApplication.class);
		final ExportNetworkViewTaskFactory exportNetworkViewTaskFactory = getService(bc, ExportNetworkViewTaskFactory.class);

		// For commands
		final AvailableCommands available = getService(bc, AvailableCommands.class);
		final CommandExecutorTaskFactory ceTaskFactory = getService(bc, CommandExecutorTaskFactory.class);
		final SynchronousTaskManager<?> synchronousTaskManager = getService(bc, SynchronousTaskManager.class);
		
		// Get any command line arguments. The "-R" is ours
		@SuppressWarnings("unchecked")
		final CyProperty<Properties> commandLineProps = getService(bc, CyProperty.class, "(cyPropertyName=commandline.props)");

		final Properties clProps = commandLineProps.getProperties();
		
		String restPortNumber = cyPropertyServiceRef.getProperties().getProperty(GrizzlyServerManager.PORT_NUMBER_PROP);
		
		if (clProps.getProperty(GrizzlyServerManager.PORT_NUMBER_PROP) != null)
			restPortNumber = clProps.getProperty(GrizzlyServerManager.PORT_NUMBER_PROP);
		
		if(restPortNumber == null) {
			restPortNumber = GrizzlyServerManager.DEF_PORT_NUMBER.toString();
		}
		
		// Set Port number
		cyPropertyServiceRef.getProperties().setProperty(GrizzlyServerManager.PORT_NUMBER_PROP, restPortNumber);

		// Task factories
		final NewNetworkSelectedNodesAndEdgesTaskFactory networkSelectedNodesAndEdgesTaskFactory = getService(bc,
				NewNetworkSelectedNodesAndEdgesTaskFactory.class);
		
			
		CyNetworkViewWriterFactory cytoscapeJsWriterFactory = null;
		InputStreamTaskFactory cytoscapeJsReaderFactory = null;
		
		Boolean jsonDependencyFound = false;
		Integer retryCount = 0;
		while(jsonDependencyFound == false && retryCount <= MAX_RETRY) {
			try {
				cytoscapeJsWriterFactory = getService(bc, CyNetworkViewWriterFactory.class,
						"(id=cytoscapejsNetworkWriterFactory)");
				cytoscapeJsReaderFactory = getService(bc, InputStreamTaskFactory.class,
						"(id=cytoscapejsNetworkReaderFactory)");
				jsonDependencyFound = true;
			} catch(Exception ex) {
				System.out.println("Retry: " + retryCount);
				Thread.sleep(INTERVAL);
			}
			retryCount++;
		}
		
		if(jsonDependencyFound == false) {
			throw new IllegalStateException("Could not find dependency: JSON support services are missing.");
		}
		
		final LoadNetworkURLTaskFactory loadNetworkURLTaskFactory = getService(bc, LoadNetworkURLTaskFactory.class);
		final SelectFirstNeighborsTaskFactory selectFirstNeighborsTaskFactory = getService(bc, SelectFirstNeighborsTaskFactory.class);
		
		// TODO: need ID for these services.
		final NetworkTaskFactory fitContent = getService(bc, NetworkTaskFactory.class, "(title=Fit Content)");
		final NetworkTaskFactory edgeBundler = getService(bc, NetworkTaskFactory.class, "(title=All Nodes and Edges)");
		final NetworkTaskFactory showDetailsTaskFactory = getService(bc, NetworkTaskFactory.class, 
				"(title=Show/Hide Graphics Details)");

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
				new EdgeBundlerImpl(edgeBundler), renderingEngineManager, sessionManager, 
				saveSessionAsTaskFactory, openSessionTaskFactory, newSessionTaskFactory, desktop, 
				new LevelOfDetails(showDetailsTaskFactory), selectFirstNeighborsTaskFactory, graphicsWriterManager, 
				exportNetworkViewTaskFactory, available, ceTaskFactory, synchronousTaskManager);
				this.grizzlyServerManager = new GrizzlyServerManager(binder, cyPropertyServiceRef);
		
		
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
	
	public class LevelOfDetails {

		private final NetworkTaskFactory lod;

		public LevelOfDetails(final NetworkTaskFactory tf) {
			this.lod = tf;
		}

		public NetworkTaskFactory getLodTF() {
			return lod;
		}

	}
}