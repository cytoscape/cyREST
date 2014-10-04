package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import javax.ws.rs.core.Application;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.ding.NetworkViewTestSupport;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.NetworkTestSupport;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.rest.TaskFactoryManager;
import org.cytoscape.rest.internal.CyActivator.WriterListener;
import org.cytoscape.rest.internal.EdgeBundler;
import org.cytoscape.rest.internal.MappingFactoryManager;
import org.cytoscape.rest.internal.reader.EdgeListReaderFactory;
import org.cytoscape.rest.internal.resource.AlgorithmicResource;
import org.cytoscape.rest.internal.resource.GlobalTableResource;
import org.cytoscape.rest.internal.resource.GroupResource;
import org.cytoscape.rest.internal.resource.MiscResource;
import org.cytoscape.rest.internal.resource.NetworkFullResource;
import org.cytoscape.rest.internal.resource.NetworkResource;
import org.cytoscape.rest.internal.resource.NetworkViewResource;
import org.cytoscape.rest.internal.resource.RootResource;
import org.cytoscape.rest.internal.resource.StyleResource;
import org.cytoscape.rest.internal.resource.TableResource;
import org.cytoscape.rest.internal.task.CyBinder;
import org.cytoscape.rest.internal.task.HeadlessTaskMonitor;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.task.create.NewNetworkSelectedNodesAndEdgesTaskFactory;
import org.cytoscape.task.read.LoadNetworkURLTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskMonitor;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.spi.TestContainer;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Test;

public class AlgorithmicResourceTest extends JerseyTest {

	private CyBinder binder;

	public AlgorithmicResourceTest() {
		CyLayoutAlgorithm def = mock(CyLayoutAlgorithm.class);
		Object context = new Object();
		when(def.createLayoutContext()).thenReturn(context);
		when(def.getDefaultLayoutContext()).thenReturn(context);

		Collection<CyLayoutAlgorithm> algorithms = new ArrayList<>();
		algorithms.add(def);
		CyLayoutAlgorithmManager layouts = mock(CyLayoutAlgorithmManager.class);
		when(layouts.getDefaultLayout()).thenReturn(def);
		when(layouts.getAllLayouts()).thenReturn(algorithms);

		NetworkTestSupport nts = new NetworkTestSupport();
		CyNetworkFactory netFactory = nts.getNetworkFactory();
		CyNetworkManager networkManager = nts.getNetworkManager();
		CyRootNetworkManager rootNetworkManager = nts.getRootNetworkFactory();

		CyApplicationManager cyApplicationManager = mock(CyApplicationManager.class);
		NetworkViewTestSupport nvts = new NetworkViewTestSupport();
		CyNetworkViewFactory viewFactory = nvts.getNetworkViewFactory();

		TaskFactoryManager tfm = mock(TaskFactoryManager.class);
		VisualMappingManager vmm = mock(VisualMappingManager.class);
		CyNetworkViewManager viewManager = mock(CyNetworkViewManager.class);

		CyNetworkViewWriterFactory cytoscapeJsWriterFactory = mock(CyNetworkViewWriterFactory.class);
		WriterListener writerListsner = mock(WriterListener.class);
		TaskMonitor headlessTaskMonitor = new HeadlessTaskMonitor();

		CyTableManager tableManager = mock(CyTableManager.class);
		VisualStyleFactory vsFactory = mock(VisualStyleFactory.class);
		MappingFactoryManager mappingFactoryManager = mock(MappingFactoryManager.class);
		CyGroupFactory groupFactory = mock(CyGroupFactory.class);
		CyGroupManager groupManager = mock(CyGroupManager.class);
		LoadNetworkURLTaskFactory loadNetworkURLTaskFactory = mock(LoadNetworkURLTaskFactory.class);
		CyProperty<Properties> cyPropertyServiceRef = mock(CyProperty.class);
		NewNetworkSelectedNodesAndEdgesTaskFactory networkSelectedNodesAndEdgesTaskFactory = mock(NewNetworkSelectedNodesAndEdgesTaskFactory.class);
		EdgeListReaderFactory edgeListReaderFactory = mock(EdgeListReaderFactory.class);
		CyTableFactory tableFactory = mock(CyTableFactory.class);
		NetworkTaskFactory fitContent = mock(NetworkTaskFactory.class);
		EdgeBundler edgeBundler = mock(EdgeBundler.class);
		RenderingEngineManager renderingEngineManager = mock(RenderingEngineManager.class);

		this.binder = new CyBinder(networkManager, viewManager, netFactory,
				tfm, cyApplicationManager, vmm, cytoscapeJsWriterFactory,
				edgeListReaderFactory, layouts, writerListsner,
				headlessTaskMonitor, tableManager, vsFactory,
				mappingFactoryManager, groupFactory, groupManager,
				rootNetworkManager, loadNetworkURLTaskFactory,
				cyPropertyServiceRef, networkSelectedNodesAndEdgesTaskFactory,
				edgeListReaderFactory, viewFactory, tableFactory, fitContent,
				edgeBundler, renderingEngineManager);
	}

	@Override
	protected Application configure() {
		return new ResourceConfig(AlgorithmicResource.class);
	}

	@Test
	public void testLayout() {
		final Collection layouts = target("/v1/apply/layouts").request().get(
				Collection.class);
		assertNotNull(layouts);
		assertEquals(layouts.size(), 1);
	}
	
	@Test
	public void testStyles() {
		final Collection styles = target("/v1/apply/styles").request().get(
				Collection.class);
		assertNotNull(styles);
		assertEquals(styles.size(), 0);
	}

	@Override
	protected TestContainerFactory getTestContainerFactory()
			throws TestContainerException {
		return new TestContainerFactory() {
			@Override
			public TestContainer create(final URI baseUri,
					DeploymentContext arg1) throws IllegalArgumentException {
				return new TestContainer() {
					private HttpServer server;

					@Override
					public ClientConfig getClientConfig() {
						return null;
					}

					@Override
					public URI getBaseUri() {
						return baseUri;
					}

					@Override
					public void start() {
						try {
							final ResourceConfig rc = new ResourceConfig(
									RootResource.class, NetworkResource.class,
									NetworkFullResource.class,
									NetworkViewResource.class,
									TableResource.class, MiscResource.class,
									AlgorithmicResource.class,
									StyleResource.class, GroupResource.class,
									GlobalTableResource.class);
							rc.registerInstances(binder)
									.packages(
											"org.glassfish.jersey.examples.jackson")
									.register(JacksonFeature.class);

							this.server = GrizzlyHttpServerFactory
									.createHttpServer(baseUri, rc);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void stop() {
						this.server.stop();
					}
				};

			}
		};
	}
}