package org.cytoscape.rest.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.ding.NetworkViewTestSupport;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
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
import org.cytoscape.view.model.CyNetworkView;
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

public class BasicResourceTest extends JerseyTest {

	protected NetworkTestSupport nts = new NetworkTestSupport();
	protected NetworkViewTestSupport nvts = new NetworkViewTestSupport();

	protected final CyBinder binder;

	protected CyNetwork network;
	protected CyNetworkView view;

	public BasicResourceTest() {
		CyLayoutAlgorithm def = mock(CyLayoutAlgorithm.class);
		Object context = new Object();
		when(def.createLayoutContext()).thenReturn(context);
		when(def.getDefaultLayoutContext()).thenReturn(context);
		when(def.getName()).thenReturn("grid");

		Collection<CyLayoutAlgorithm> algorithms = new ArrayList<>();
		algorithms.add(def);
		CyLayoutAlgorithmManager layouts = mock(CyLayoutAlgorithmManager.class);
		when(layouts.getDefaultLayout()).thenReturn(def);
		when(layouts.getAllLayouts()).thenReturn(algorithms);

		CyNetworkFactory netFactory = nts.getNetworkFactory();
		CyNetworkManager networkManager = nts.getNetworkManager();
		this.network = createNetwork();
		this.view = nvts.getNetworkViewFactory().createNetworkView(network);
		networkManager.addNetwork(network);

		CyRootNetworkManager rootNetworkManager = nts.getRootNetworkFactory();
		CyNetworkViewManager viewManager = mock(CyNetworkViewManager.class);
		Collection<CyNetworkView> views = new HashSet<>();
		views.add(view);
		when(viewManager.getNetworkViews(network)).thenReturn(views);

		CyApplicationManager cyApplicationManager = mock(CyApplicationManager.class);
		CyNetworkViewFactory viewFactory = nvts.getNetworkViewFactory();

		TaskFactoryManager tfm = mock(TaskFactoryManager.class);
		VisualMappingManager vmm = mock(VisualMappingManager.class);

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

	/**
	 * Create a simple network for testing.
	 * 
	 * @return sample network
	 */
	private final CyNetwork createNetwork() {
		final CyNetwork network = nvts.getNetwork();
		network.getRow(network).set(CyNetwork.NAME, "network1");
		CyNode n1 = network.addNode();
		CyNode n2 = network.addNode();
		CyNode n3 = network.addNode();
		CyNode n4 = network.addNode();

		network.getRow(n1).set(CyNetwork.NAME, "n1");
		network.getRow(n2).set(CyNetwork.NAME, "n2");
		network.getRow(n3).set(CyNetwork.NAME, "n3");
		network.getRow(n4).set(CyNetwork.NAME, "n4");
		
		final CyEdge e1 = network.addEdge(n1, n2, true);
		final CyEdge e2 = network.addEdge(n2, n3, true);
		final CyEdge e3 = network.addEdge(n3, n1, true);

		return network;
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
