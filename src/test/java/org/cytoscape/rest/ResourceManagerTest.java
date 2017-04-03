package org.cytoscape.rest;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
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
import org.cytoscape.rest.internal.CyNetworkViewWriterFactoryManager;
import org.cytoscape.rest.internal.EdgeBundler;
import org.cytoscape.rest.internal.GraphicsWriterManager;
import org.cytoscape.rest.internal.MappingFactoryManager;
import org.cytoscape.rest.internal.TaskFactoryManager;
import org.cytoscape.rest.internal.CyActivator.LevelOfDetails;
import org.cytoscape.rest.internal.CyActivator.WriterListener;
import org.cytoscape.rest.internal.reader.EdgeListReaderFactory;
import org.cytoscape.rest.internal.task.CoreServiceModule;
import org.cytoscape.rest.internal.task.ResourceManager;
import org.cytoscape.session.CySessionManager;
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
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskMonitor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

public class ResourceManagerTest 
{
	private ResourceManager resourceManager;

	private BundleContext context;
	
	ArgumentCaptor<Object> serviceInstanceCaptor;

	public class TestModule extends AbstractModule {
		private final Object testFloat = 0.13f;
		
		private CoreServiceModule coreServiceModule;
		
		public TestModule(CoreServiceModule coreServiceModule)
		{
			this.coreServiceModule = coreServiceModule;
		}
		
		@Override
		protected void configure() {
			install(coreServiceModule);
			bind(Object.class).toInstance(testFloat);
		}
	}
	
	@Before
	public void setup() throws InvalidSyntaxException
	{
		final Class<?>[] testClasses = {TestClass.class}; 

		final CyNetworkManager networkManager = mock(CyNetworkManager.class);
		final CyNetworkViewManager networkViewManager= mock(CyNetworkViewManager.class);
		final CyNetworkFactory networkFactory= mock(CyNetworkFactory.class); 
		final TaskFactoryManager tfManager = mock(TaskFactoryManager.class);
		final CyApplicationManager applicationManager = mock(CyApplicationManager.class);
		final VisualMappingManager vmm = mock(VisualMappingManager.class);
		final ServiceTracker cytoscapeJsWriterFactory = mock(ServiceTracker.class);
		final ServiceTracker cytoscapeJsReaderFactory = mock(ServiceTracker.class);
		final CyLayoutAlgorithmManager layoutManager = mock(CyLayoutAlgorithmManager.class);
		final WriterListener vizmapWriterFactoryListener = mock(WriterListener.class);
		final TaskMonitor headlessMonitor = mock(TaskMonitor.class);
		final CyTableManager tableManager = mock(CyTableManager.class);
		final VisualStyleFactory vsFactory = mock(VisualStyleFactory.class);
		final MappingFactoryManager mappingFactoryManager = mock(MappingFactoryManager.class);
		final CyGroupFactory groupFactory = mock(CyGroupFactory.class);
		final CyGroupManager groupManager = mock(CyGroupManager.class);
		final CyRootNetworkManager cyRootNetworkManager = mock(CyRootNetworkManager.class);
		final LoadNetworkURLTaskFactory loadNetworkURLTaskFactory = mock(LoadNetworkURLTaskFactory.class);
		final CyProperty<Properties> props = mock(CyProperty.class);
		final NewNetworkSelectedNodesAndEdgesTaskFactory newNetworkSelectedNodesAndEdgesTaskFactory = mock(NewNetworkSelectedNodesAndEdgesTaskFactory.class);
		final EdgeListReaderFactory edgelistReaderFactory = mock(EdgeListReaderFactory.class);
		final CyNetworkViewFactory networkViewFactory = mock(CyNetworkViewFactory.class);
		final CyTableFactory tableFactory = mock(CyTableFactory.class);
		final NetworkTaskFactory fitContent = mock(NetworkTaskFactory.class);
		final EdgeBundler edgeBundler = mock(EdgeBundler.class);
		final RenderingEngineManager renderingEngineManager = mock(RenderingEngineManager.class);
		final CySessionManager sessionManager = mock(CySessionManager.class);
		final SaveSessionAsTaskFactory saveSessionAsTaskFactory = mock(SaveSessionAsTaskFactory.class);
		final OpenSessionTaskFactory openSessionTaskFactory = mock(OpenSessionTaskFactory.class);
		final NewSessionTaskFactory newSessionTaskFactory = mock(NewSessionTaskFactory.class);
		final CySwingApplication desktop = mock(CySwingApplication.class);
		final LevelOfDetails toggleLod = mock(LevelOfDetails.class);
		final SelectFirstNeighborsTaskFactory selectFirstNeighborsTaskFactory = mock(SelectFirstNeighborsTaskFactory.class);
		final GraphicsWriterManager graphicsWriterManager = mock(GraphicsWriterManager.class);
		final ExportNetworkViewTaskFactory exportNetworkViewTaskFactory = mock(ExportNetworkViewTaskFactory.class);
		final AvailableCommands available = mock(AvailableCommands.class);
		final CommandExecutorTaskFactory ceTaskFactory = mock(CommandExecutorTaskFactory.class);
		final SynchronousTaskManager<?> synchronousTaskManager = mock(SynchronousTaskManager.class);
		final CyNetworkViewWriterFactoryManager viewFactoryManager = mock(CyNetworkViewWriterFactoryManager.class);
		final String cyRESTPort = "1234"; 
		final String logLocation = "nowhere";

		CoreServiceModule coreServiceModule = new CoreServiceModule(networkManager, networkViewManager, networkFactory, tfManager, applicationManager, vmm, cytoscapeJsWriterFactory, cytoscapeJsReaderFactory, layoutManager, vizmapWriterFactoryListener, headlessMonitor, tableManager, vsFactory, mappingFactoryManager, groupFactory, groupManager, cyRootNetworkManager, loadNetworkURLTaskFactory, props, newNetworkSelectedNodesAndEdgesTaskFactory, edgelistReaderFactory, networkViewFactory, tableFactory, fitContent, edgeBundler, renderingEngineManager, sessionManager, saveSessionAsTaskFactory, openSessionTaskFactory, newSessionTaskFactory, desktop, toggleLod, selectFirstNeighborsTaskFactory, graphicsWriterManager, exportNetworkViewTaskFactory, available, ceTaskFactory, synchronousTaskManager, viewFactoryManager, cyRESTPort, logLocation);

		final Map<Class<?>, Module> shimMap = new HashMap<Class<?>, Module>();
		shimMap.put(TestShimClass.class, new TestModule(coreServiceModule));
		
		context = mock(BundleContext.class);
		when(context.createFilter(anyString())).thenReturn(mock(Filter.class));
		
		serviceInstanceCaptor = ArgumentCaptor.forClass(Object.class);
		
		when(context.registerService(anyString(), serviceInstanceCaptor.capture(), anyObject())).thenReturn(mock(ServiceRegistration.class));
		resourceManager = new ResourceManager(context, testClasses, coreServiceModule, shimMap);
	}

	@Test
	public void testRegisterResourceServices() throws Exception 
	{
		resourceManager.registerResourceServices();
		List<Object> instances = serviceInstanceCaptor.getAllValues();
		assertEquals(TestClass.class, instances.get(0).getClass());
		TestClass testClassInstance = (TestClass) instances.get(0);
		assertEquals("1234", testClassInstance.cyRestPort);
		verify(context).registerService(TestClass.class.getName(), new TestClass(), new Properties());
	}
	
	@Test
	public void testRegisterShimResourceServices() throws Exception
	{
		resourceManager.registerResourceServices();
		List<Object> instances = serviceInstanceCaptor.getAllValues();
		assertEquals(TestShimClass.class, instances.get(1).getClass());
		TestShimClass testShimClassInstance = (TestShimClass) instances.get(1);
		assertEquals(new Float(0.13f), testShimClassInstance.injected);
		verify(context).registerService(TestShimClass.class.getName(), new TestShimClass(), new Properties());
	}

	@Test
	public void testUnRegisterResourceServices() throws Exception
	{
		resourceManager.registerResourceServices();
		resourceManager.unregisterResourceServices();
		//TODO verify that services were unregistered.
	}
}
