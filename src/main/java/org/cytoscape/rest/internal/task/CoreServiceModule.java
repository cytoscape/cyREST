package org.cytoscape.rest.internal.task;

import java.net.URI;
import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.ci.CIErrorFactory;
import org.cytoscape.ci.CIExceptionFactory;
import org.cytoscape.ci.CIResponseFactory;
import org.cytoscape.command.AvailableCommands;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.rest.internal.BundleResourceProvider;
import org.cytoscape.rest.internal.CyActivator.LevelOfDetails;
import org.cytoscape.rest.internal.CyActivator.WriterListener;
import org.cytoscape.rest.internal.CyNetworkViewWriterFactoryManager;
import org.cytoscape.rest.internal.EdgeBundler;
import org.cytoscape.rest.internal.GraphicsWriterManager;
import org.cytoscape.rest.internal.MappingFactoryManager;
import org.cytoscape.rest.internal.TaskFactoryManager;
import org.cytoscape.rest.internal.reader.EdgeListReaderFactory;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.task.NetworkViewTaskFactory;
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
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskMonitor;
import org.osgi.util.tracker.ServiceTracker;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

public class CoreServiceModule extends AbstractModule {

	private final TaskMonitor headlessMonitor;

	private final CyNetworkManager networkManager;
	private final CyNetworkViewManager networkViewManager;
	private final CySessionManager sessionManager;
	private final CyNetworkFactory networkFactory;
	private final CyNetworkViewFactory networkViewFactory;
	private final TaskFactoryManager tfManager;
	private final CyApplicationManager applicationManager;
	private final VisualMappingManager vmm;
	private final CyLayoutAlgorithmManager layoutManager;
	private final CyTableManager tableManager;
	private final CyTableFactory tableFactory;

	private final WriterListener vizmapWriterFactoryListener;
	private final VisualStyleFactory vsFactory;

	private final MappingFactoryManager mappingFactoryManager;
	private final CyGroupFactory groupFactory;
	private final CyGroupManager groupManager;
	private final CyRootNetworkManager cyRootNetworkManager;
	
	private final LoadNetworkURLTaskFactory loadNetworkURLTaskFactory;

	private final CyProperty<Properties> props;
	
	// TFs
	private final NewNetworkSelectedNodesAndEdgesTaskFactory newNetworkSelectedNodesAndEdgesTaskFactory;
	private final EdgeListReaderFactory edgelistReaderFactory;
	private final ServiceTracker cytoscapeJsWriterFactory;
	private final ServiceTracker cytoscapeJsReaderFactory;
	
	private final AutomationAppTracker automationAppTracker;
	
	private final NetworkViewTaskFactory fitContent;
	private final LevelOfDetails toggleLod;
	private final EdgeBundler edgeBundler;
	private final RenderingEngineManager renderingEngineManager;
	private final SaveSessionAsTaskFactory saveSessionAsTaskFactory;
	private final OpenSessionTaskFactory openSessionTaskFactory;
	private final NewSessionTaskFactory newSessionTaskFactory;
	private final CySwingApplication desktop;
	private final SelectFirstNeighborsTaskFactory selectFirstNeighborsTaskFactory;
	private final ExportNetworkViewTaskFactory exportNetworkViewTaskFactory;
	
	private final GraphicsWriterManager graphicsWriterManager;
	
	// For Command API
	private final AvailableCommands available;
	private final CommandExecutorTaskFactory ceTaskFactory;
	private final SynchronousTaskManager<Object> synchronousTaskManager;
	
	private final CyNetworkViewWriterFactoryManager viewFactoryManager;
	
	private final BundleResourceProvider bundleResourceProvider;
	
	private final URI logLocation;
	private final String cyRESTPort;
	
	private final CIResponseFactory ciResponseFactory;
	private final CIErrorFactory ciErrorFactory;
	private final CIExceptionFactory ciExceptionFactory;
	

	public CoreServiceModule(final CyNetworkManager networkManager, final CyNetworkViewManager networkViewManager,
			final CyNetworkFactory networkFactory, final TaskFactoryManager tfManager,
			final CyApplicationManager applicationManager, final VisualMappingManager vmm,
			final ServiceTracker cytoscapeJsWriterFactory,
			final ServiceTracker cytoscapeJsReaderFactory, 
			final AutomationAppTracker automationAppTracker, final CyLayoutAlgorithmManager layoutManager,
			final WriterListener vizmapWriterFactoryListener, final TaskMonitor headlessMonitor,
			final CyTableManager tableManager, final VisualStyleFactory vsFactory,
			final MappingFactoryManager mappingFactoryManager, final CyGroupFactory groupFactory,
			final CyGroupManager groupManager, final CyRootNetworkManager cyRootNetworkManager,
			final LoadNetworkURLTaskFactory loadNetworkURLTaskFactory, final CyProperty<Properties> props,
			final NewNetworkSelectedNodesAndEdgesTaskFactory newNetworkSelectedNodesAndEdgesTaskFactory, 
			final EdgeListReaderFactory edgelistReaderFactory, final CyNetworkViewFactory networkViewFactory,
			final CyTableFactory tableFactory, final NetworkViewTaskFactory fitContent, final EdgeBundler edgeBundler,
			final RenderingEngineManager renderingEngineManager, final CySessionManager sessionManager,
			final SaveSessionAsTaskFactory saveSessionAsTaskFactory, final OpenSessionTaskFactory openSessionTaskFactory,
			final NewSessionTaskFactory newSessionTaskFactory, final CySwingApplication desktop,
			final LevelOfDetails toggleLod, final SelectFirstNeighborsTaskFactory selectFirstNeighborsTaskFactory,
			final GraphicsWriterManager graphicsWriterManager, final ExportNetworkViewTaskFactory exportNetworkViewTaskFactory,
			final AvailableCommands available, final CommandExecutorTaskFactory ceTaskFactory, 
			final SynchronousTaskManager<?> synchronousTaskManager, final CyNetworkViewWriterFactoryManager viewFactoryManager,
			final BundleResourceProvider bundleResourceProvider,
			final String cyRESTPort, final URI logLocation,
			final CIResponseFactory ciResponseFactory,
			final CIErrorFactory ciErrorFactory,
			final CIExceptionFactory ciExceptionFactory) {
	
		this.networkManager = networkManager;
		this.networkViewManager = networkViewManager;
		this.networkFactory = networkFactory;
		this.tfManager = tfManager;
		this.applicationManager = applicationManager;
		this.vmm = vmm;
		this.cytoscapeJsReaderFactory = cytoscapeJsReaderFactory;
		this.cytoscapeJsWriterFactory = cytoscapeJsWriterFactory;
		this.automationAppTracker = automationAppTracker;
		this.layoutManager = layoutManager;
		this.vizmapWriterFactoryListener = vizmapWriterFactoryListener;
		this.headlessMonitor = headlessMonitor;
		this.tableManager = tableManager;
		this.vsFactory = vsFactory;
		this.mappingFactoryManager = mappingFactoryManager;
		this.groupFactory = groupFactory;
		this.groupManager = groupManager;
		this.cyRootNetworkManager = cyRootNetworkManager;
		this.loadNetworkURLTaskFactory = loadNetworkURLTaskFactory;
		this.props = props;
		this.newNetworkSelectedNodesAndEdgesTaskFactory = newNetworkSelectedNodesAndEdgesTaskFactory;
		this.edgelistReaderFactory = edgelistReaderFactory;
		this.networkViewFactory = networkViewFactory;
		this.tableFactory = tableFactory;
		this.fitContent = fitContent;
		this.edgeBundler = edgeBundler;
		this.renderingEngineManager = renderingEngineManager;
		this.sessionManager = sessionManager;
		this.saveSessionAsTaskFactory = saveSessionAsTaskFactory;
		this.openSessionTaskFactory = openSessionTaskFactory;
		this.newSessionTaskFactory = newSessionTaskFactory;
		this.desktop = desktop;
		this.toggleLod = toggleLod;
		this.selectFirstNeighborsTaskFactory = selectFirstNeighborsTaskFactory;
		this.graphicsWriterManager = graphicsWriterManager;
		this.exportNetworkViewTaskFactory = exportNetworkViewTaskFactory;
		this.available = available;
		this.ceTaskFactory = ceTaskFactory;
		this.synchronousTaskManager = (SynchronousTaskManager<Object>) synchronousTaskManager;
		this.viewFactoryManager = viewFactoryManager;
		this.bundleResourceProvider = bundleResourceProvider;
		this.cyRESTPort = cyRESTPort;
		this.logLocation = logLocation;
		this.ciResponseFactory = ciResponseFactory;
		this.ciErrorFactory = ciErrorFactory;
		this.ciExceptionFactory = ciExceptionFactory;
	}


	@Override
	protected void configure() {
		bind(CyNetworkManager.class).toInstance(networkManager);
		bind(CyNetworkViewManager.class).toInstance(networkViewManager);
		bind(CyNetworkFactory.class).toInstance(networkFactory);
		bind(TaskFactoryManager.class).toInstance(tfManager);
		bind(VisualMappingManager.class).toInstance(vmm);
		bind(CyApplicationManager.class).toInstance(applicationManager);
		bind(ServiceTracker.class).annotatedWith(CytoscapeJsReaderFactory.class).toInstance(cytoscapeJsReaderFactory);
		bind(ServiceTracker.class).annotatedWith(CytoscapeJsWriterFactory.class).toInstance(cytoscapeJsWriterFactory);
		bind(AutomationAppTracker.class).toInstance(automationAppTracker);
		bind(CyLayoutAlgorithmManager.class).toInstance(layoutManager);
		bind(WriterListener.class).toInstance(vizmapWriterFactoryListener);
		bind(TaskMonitor.class).toInstance(headlessMonitor);
		bind(CyTableManager.class).toInstance(tableManager);
		bind(VisualStyleFactory.class).toInstance(vsFactory);
		bind(MappingFactoryManager.class).toInstance(mappingFactoryManager);
		bind(CyGroupFactory.class).toInstance(groupFactory);
		bind(CyGroupManager.class).toInstance(groupManager);
		bind(CyRootNetworkManager.class).toInstance(cyRootNetworkManager);
		bind(LoadNetworkURLTaskFactory.class).toInstance(loadNetworkURLTaskFactory);
		//bind(CyProperty.class).toInstance(props);
		bind(new TypeLiteral<CyProperty<Properties>>(){}).toInstance(props);
		bind(NewNetworkSelectedNodesAndEdgesTaskFactory.class).toInstance(newNetworkSelectedNodesAndEdgesTaskFactory);
		bind(EdgeListReaderFactory.class).toInstance(edgelistReaderFactory);
		bind(CyNetworkViewFactory.class).toInstance(networkViewFactory);
		bind(CyTableFactory.class).toInstance(tableFactory);
		bind(NetworkViewTaskFactory.class).toInstance(fitContent);
		bind(EdgeBundler.class).toInstance(edgeBundler);
		bind(RenderingEngineManager.class).toInstance(renderingEngineManager);
		bind(CySessionManager.class).toInstance(sessionManager);
		bind(SaveSessionAsTaskFactory.class).toInstance(saveSessionAsTaskFactory);
		bind(OpenSessionTaskFactory.class).toInstance(openSessionTaskFactory);
		bind(NewSessionTaskFactory.class).toInstance(newSessionTaskFactory);
		bind(CySwingApplication.class).toInstance(desktop);
		bind(LevelOfDetails.class).toInstance(toggleLod);
		bind(SelectFirstNeighborsTaskFactory.class).toInstance(selectFirstNeighborsTaskFactory);
		bind(GraphicsWriterManager.class).toInstance(graphicsWriterManager);
		bind(ExportNetworkViewTaskFactory.class).toInstance(exportNetworkViewTaskFactory);
		bind(CyNetworkViewWriterFactoryManager.class).toInstance(viewFactoryManager);
		
		// For Command API
		bind(AvailableCommands.class).toInstance(available);
		bind(CommandExecutorTaskFactory.class).toInstance(ceTaskFactory);
		bind(new TypeLiteral<SynchronousTaskManager<Object>>(){}).toInstance(synchronousTaskManager);
		
		bind(BundleResourceProvider.class).toInstance(bundleResourceProvider);
		
		bind(String.class).annotatedWith(CyRESTPort.class).toInstance(cyRESTPort);
		bind(URI.class).annotatedWith(LogLocation.class).toInstance(logLocation);
		
		bind(CIResponseFactory.class).toInstance(ciResponseFactory);
		bind(CIErrorFactory.class).toInstance(ciErrorFactory);
		bind(CIExceptionFactory.class).toInstance(ciExceptionFactory);
	}
}