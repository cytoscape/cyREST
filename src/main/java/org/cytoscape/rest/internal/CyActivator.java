package org.cytoscape.rest.internal;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.ci.CIErrorFactory;
import org.cytoscape.ci.CIExceptionFactory;
import org.cytoscape.ci.CIResponseFactory;
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
import org.cytoscape.rest.internal.resource.apps.clustermaker2.ClusterMaker2Resource;
import org.cytoscape.rest.internal.task.CoreServiceModule;
import org.cytoscape.rest.internal.task.ResourceManager;
import org.cytoscape.rest.internal.task.HeadlessTaskMonitor;
import org.cytoscape.rest.internal.task.OSGiJAXRSManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
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
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Module;

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

	private String cyRESTPort = null;
	private URI logLocation = null;
	private OSGiJAXRSManager osgiJAXRSManager = null;
	private ResourceManager resourceManager = null;

	
	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) throws InvalidSyntaxException {

		this.registerService(bc, new CIResponseFactoryImpl(), CIResponseFactory.class, new Properties());
		this.registerService(bc, new CIExceptionFactoryImpl(), CIExceptionFactory.class, new Properties());
		
		try {
			this.logLocation = this.getLogLocation(bc);
		} catch (IOException e1) {
			this.logLocation = null;
			logger.warn("CyREST is unable to find the Karaf log");
		}
		
		this.registerService(bc, new CIErrorFactoryImpl(this.logLocation), CIErrorFactory.class, new Properties());
		
		serverState = ServerState.STARTING;

		logger.info("Initializing cyREST API server...");
		long start = System.currentTimeMillis();

		osgiJAXRSManager = new OSGiJAXRSManager();

		final ExecutorService service = Executors.newSingleThreadExecutor();
		service.submit(()-> {
			try {
				
				if (suggestRestart(bc)) {
					final CySwingApplication swingApplication = getService(bc, CySwingApplication.class);
					if (swingApplication != null && swingApplication.getJFrame() != null) {
					JOptionPane.showMessageDialog(swingApplication.getJFrame(), "CyREST requires a restart of Cytoscape "
							+ "for changes to take effect.", "Restart required", JOptionPane.WARNING_MESSAGE);
					}
					serverState = ServerState.SUGGEST_RESTART;
					logger.warn("Detected new installation. Restarting Cytoscape is recommended.");
					
				} else {
					this.initDependencies(bc);
					osgiJAXRSManager.installOSGiJAXRSBundles(bc, this.cyRESTPort);
					resourceManager.registerResourceServices();
					serverState = ServerState.STARTED;
					logger.info("cyREST API Server initialized: " + (System.currentTimeMillis() - start) + " msec.");
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
				logger.error("Failed to initialize cyREST server.", e);
				serverState = ServerState.FAILED_INITIALIZATION;
			}
		});
	}

	private final URI getLogLocation(BundleContext bc) throws IOException {
		final String logLocation;

		// Extract Karaf's log file location
		ConfigurationAdmin configurationAdmin = getService(bc, ConfigurationAdmin.class);
		
		if (configurationAdmin != null) {
			Configuration config = configurationAdmin.getConfiguration("org.ops4j.pax.logging");

			Dictionary<?,?> dictionary = config.getProperties();
			Object logObject = dictionary.get("log4j.appender.file.File");
			if (logObject != null && logObject instanceof String) {
				logLocation = (String) logObject;
			}
			else {
				logLocation = null;
			}
		}
		else {
			logLocation = null;
		}
		return (new File(logLocation)).toURI();
	}
	
	private ServerState serverState = ServerState.STOPPED;
	
	public ServerState getServerState() {
		return serverState;
	}
	
	public enum ServerState{
		STARTING,
		STARTED,
		SUGGEST_RESTART,
		FAILED_INITIALIZATION,
		FAILED_STOP,
		STOPPED
	}
	
	private boolean suggestRestart(BundleContext bc) {
		Bundle defaultBundle = bc.getBundle();	
		final CyProperty<Properties> cyProperties = getService(bc, CyProperty.class,
				"(cyPropertyName=cytoscape3.props)");
		
		Object cyRESTVersion = cyProperties.getProperties().get("cyrest.version");
		if (!defaultBundle.getVersion().toString().equals(cyRESTVersion)) {
			logger.info("CyREST [" + defaultBundle.getVersion().toString() + "] discovered previous CyREST Version: " + cyRESTVersion);
			cyProperties.getProperties().put("cyrest.version", defaultBundle.getVersion().toString());
			return true;
		} else {
			return false;
		}
	}
	
	private final void initDependencies(final BundleContext bc) throws Exception {
		
		// OSGi Service listeners
		final MappingFactoryManager mappingFactoryManager = new MappingFactoryManager();
		registerServiceListener(bc, mappingFactoryManager::addFactory, mappingFactoryManager::removeFactory,
				VisualMappingFunctionFactory.class);

		final GraphicsWriterManager graphicsWriterManager = new GraphicsWriterManager();
		registerServiceListener(bc, graphicsWriterManager::addFactory, graphicsWriterManager::removeFactory,
				PresentationWriterFactory.class);

		final CyNetworkViewWriterFactoryManager viewWriterManager = new CyNetworkViewWriterFactoryManager();
		registerServiceListener(bc, viewWriterManager::addFactory, viewWriterManager::removeFactory,
				CyNetworkViewWriterFactory.class);

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

		String restPortNumber = cyPropertyServiceRef.getProperties().getProperty(ResourceManager.PORT_NUMBER_PROP);

		if (clProps.getProperty(ResourceManager.PORT_NUMBER_PROP) != null)
			restPortNumber = clProps.getProperty(ResourceManager.PORT_NUMBER_PROP);

		if(restPortNumber == null) {
			restPortNumber = ResourceManager.DEF_PORT_NUMBER.toString();
		}

		this.cyRESTPort = restPortNumber;
		
		// Set Port number
		cyPropertyServiceRef.getProperties().setProperty(ResourceManager.PORT_NUMBER_PROP, restPortNumber);

		final CyServiceRegistrar serviceRegistrar = getService(bc, CyServiceRegistrar.class);
		
		CyRESTCoreSwaggerAction swaggerCoreAction = new CyRESTCoreSwaggerAction(serviceRegistrar, this.cyRESTPort);
		registerService(bc, swaggerCoreAction, CyAction.class, new Properties());
		
		CyRESTCommandSwaggerAction swaggerCommandAction = new CyRESTCommandSwaggerAction(serviceRegistrar, this.cyRESTPort);
		registerService(bc, swaggerCommandAction, CyAction.class, new Properties());
		
		CyAutomationAction automationAction = new CyAutomationAction(serviceRegistrar);
		registerService(bc, automationAction, CyAction.class, new Properties());
		
		// Task factories
		final NewNetworkSelectedNodesAndEdgesTaskFactory networkSelectedNodesAndEdgesTaskFactory = getService(bc,
				NewNetworkSelectedNodesAndEdgesTaskFactory.class);

		ServiceTracker cytoscapeJsWriterFactory = null;
		ServiceTracker cytoscapeJsReaderFactory = null;
		//CyNetworkViewWriterFactory cxWriterFactory = null;

		cytoscapeJsWriterFactory = new ServiceTracker(bc, bc.createFilter("(&(objectClass=org.cytoscape.io.write.CyNetworkViewWriterFactory)(id=cytoscapejsNetworkWriterFactory))"), null);
		cytoscapeJsWriterFactory.open();
		cytoscapeJsReaderFactory = new ServiceTracker(bc, bc.createFilter("(&(objectClass=org.cytoscape.io.read.InputStreamTaskFactory)(id=cytoscapejsNetworkReaderFactory))"), null);
		cytoscapeJsReaderFactory.open();

		final LoadNetworkURLTaskFactory loadNetworkURLTaskFactory = getService(bc, LoadNetworkURLTaskFactory.class);
		final SelectFirstNeighborsTaskFactory selectFirstNeighborsTaskFactory = getService(bc, SelectFirstNeighborsTaskFactory.class);

		// TODO: need ID for these services.
		final NetworkTaskFactory fitContent = getService(bc, NetworkTaskFactory.class, "(title=Fit Content)");
		final NetworkTaskFactory edgeBundler = getService(bc, NetworkTaskFactory.class, "(title=All Nodes and Edges)");
		final NetworkTaskFactory showDetailsTaskFactory = getService(bc, NetworkTaskFactory.class, 
				"(title=Show/Hide Graphics Details)");

		final RenderingEngineManager renderingEngineManager = getService(bc,RenderingEngineManager.class);

		final WriterListener writerListener = new WriterListener();
		registerServiceListener(bc, writerListener::registerFactory, writerListener::unregisterFactory, VizmapWriterFactory.class);

		final TaskFactoryManager taskFactoryManagerManager = new TaskFactoryManagerImpl();

		// Get all compatible tasks
		registerServiceListener(bc, taskFactoryManagerManager, "addTaskFactory", "removeTaskFactory", TaskFactory.class);
		registerServiceListener(bc, taskFactoryManagerManager, 
				"addInputStreamTaskFactory", "removeInputStreamTaskFactory", InputStreamTaskFactory.class);
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

		BundleResourceProvider bundleResourceProvider = new BundleResourceProvider(bc);

		final Map<Class<?>, Module> shimResources = new HashMap<Class<?>, Module>();
		shimResources.put(ClusterMaker2Resource.class, null);
		
		// Start REST Server
		final CoreServiceModule coreServiceModule = new CoreServiceModule(netMan, netViewMan, netFact, taskFactoryManagerManager,
				applicationManager, visMan, cytoscapeJsWriterFactory, cytoscapeJsReaderFactory, layoutManager,
				writerListener, headlessTaskMonitor, tableManager, vsFactory, mappingFactoryManager, groupFactory,
				groupManager, cyRootNetworkManager, loadNetworkURLTaskFactory, cyPropertyServiceRef,
				networkSelectedNodesAndEdgesTaskFactory, edgeListReaderFactory, netViewFact, tableFactory, fitContent,
				new EdgeBundlerImpl(edgeBundler), renderingEngineManager, sessionManager, 
				saveSessionAsTaskFactory, openSessionTaskFactory, newSessionTaskFactory, desktop, 
				new LevelOfDetails(showDetailsTaskFactory), selectFirstNeighborsTaskFactory, graphicsWriterManager, 
				exportNetworkViewTaskFactory, available, ceTaskFactory, synchronousTaskManager, viewWriterManager, bundleResourceProvider, restPortNumber, logLocation);

		this.resourceManager = new ResourceManager(bc, CyRESTConstants.coreResourceClasses, coreServiceModule, shimResources);
	}


	@Override
	public void shutDown() {
		logger.info("Shutting down REST server...");

		if (resourceManager != null) {
			resourceManager.unregisterResourceServices();
		}
		if (osgiJAXRSManager != null)
		{
			try {
				osgiJAXRSManager.uninstallOSGiJAXRSBundles();
			} catch (BundleException e) {
				this.serverState = ServerState.FAILED_STOP;
				logger.error("Error shutting down REST server", e);
				e.printStackTrace();
			}
		}
		super.shutDown();
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
