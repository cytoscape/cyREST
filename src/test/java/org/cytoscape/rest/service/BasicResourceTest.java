package org.cytoscape.rest.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.core.Context;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.ding.DVisualLexicon;
import org.cytoscape.ding.Justification;
import org.cytoscape.ding.NetworkViewTestSupport;
import org.cytoscape.ding.ObjectPosition;
import org.cytoscape.ding.Position;
import org.cytoscape.ding.customgraphics.CustomGraphicsManager;
import org.cytoscape.ding.impl.ObjectPositionImpl;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.NetworkTestSupport;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.rest.TaskFactoryManager;
import org.cytoscape.rest.internal.CyActivator.LevelOfDetails;
import org.cytoscape.rest.internal.CyActivator.WriterListener;
import org.cytoscape.rest.internal.EdgeBundler;
import org.cytoscape.rest.internal.MappingFactoryManager;
import org.cytoscape.rest.internal.reader.EdgeListReaderFactory;
import org.cytoscape.rest.internal.resource.AlgorithmicResource;
import org.cytoscape.rest.internal.resource.GlobalTableResource;
import org.cytoscape.rest.internal.resource.GroupResource;
import org.cytoscape.rest.internal.resource.MiscResource;
import org.cytoscape.rest.internal.resource.NetworkFullResource;
import org.cytoscape.rest.internal.resource.NetworkNameResource;
import org.cytoscape.rest.internal.resource.NetworkResource;
import org.cytoscape.rest.internal.resource.NetworkViewResource;
import org.cytoscape.rest.internal.resource.RootResource;
import org.cytoscape.rest.internal.resource.SessionResource;
import org.cytoscape.rest.internal.resource.StyleResource;
import org.cytoscape.rest.internal.resource.TableResource;
import org.cytoscape.rest.internal.resource.UIResource;
import org.cytoscape.rest.internal.task.CyBinder;
import org.cytoscape.rest.internal.task.HeadlessTaskMonitor;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.task.create.NewNetworkSelectedNodesAndEdgesTaskFactory;
import org.cytoscape.task.create.NewSessionTaskFactory;
import org.cytoscape.task.read.LoadNetworkURLTaskFactory;
import org.cytoscape.task.read.OpenSessionTaskFactory;
import org.cytoscape.task.select.SelectFirstNeighborsTaskFactory;
import org.cytoscape.task.write.SaveSessionAsTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.presentation.property.ArrowShapeVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.LineTypeVisualProperty;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.values.NodeShape;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.internal.VisualLexiconManager;
import org.cytoscape.view.vizmap.internal.VisualStyleFactoryImpl;
import org.cytoscape.view.vizmap.internal.mappings.ContinuousMappingFactory;
import org.cytoscape.view.vizmap.internal.mappings.DiscreteMappingFactory;
import org.cytoscape.view.vizmap.internal.mappings.PassthroughMappingFactory;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
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
	
	protected VisualStyle style;
	protected VisualLexicon lexicon;

	private PassthroughMappingFactory passthroughFactory;
	private ContinuousMappingFactory continuousFactory;
	private DiscreteMappingFactory discreteFactory;
	
	protected CyNetworkManager networkManager = nts.getNetworkManager();
	
	protected SaveSessionAsTaskFactory saveSessionAsTaskFactory;
	protected OpenSessionTaskFactory openSessionTaskFactory;
	protected NewSessionTaskFactory newSessionTaskFactory;
	protected SelectFirstNeighborsTaskFactory selectFirstNeighborsTaskFactory;
	
	protected MappingFactoryManager mappingFactoryManager = new MappingFactoryManager();

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
		when(layouts.getLayout("grid")).thenReturn(def);

		CyNetworkFactory netFactory = nts.getNetworkFactory();
		this.network = createNetwork("network1");
		this.view = nvts.getNetworkViewFactory().createNetworkView(network);
		networkManager.addNetwork(network);
		
		CyNetwork network2 = createNetwork("network2");
		networkManager.addNetwork(network2);

		CyRootNetworkManager rootNetworkManager = nts.getRootNetworkFactory();
		CyNetworkViewManager viewManager = mock(CyNetworkViewManager.class);
		Collection<CyNetworkView> views = new HashSet<>();
		views.add(view);
		when(viewManager.getNetworkViews(network)).thenReturn(views);

		CyApplicationManager cyApplicationManager = mock(CyApplicationManager.class);
		CyNetworkViewFactory viewFactory = nvts.getNetworkViewFactory();

		TaskFactoryManager tfm = mock(TaskFactoryManager.class);
		VisualMappingManager vmm = mock(VisualMappingManager.class);
		Set<VisualStyle> styles = new HashSet<VisualStyle>();
		VisualStyle mockStyle = mock(VisualStyle.class);
		when(mockStyle.getTitle()).thenReturn("mock1");
		styles.add(mockStyle);
		try {
			this.style = initStyle();
			styles.add(this.style);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException("Could not init Style.", e);
		}
		when(vmm.getAllVisualStyles()).thenReturn(styles);
		Set<VisualLexicon> lex = new HashSet<>();
		lex.add(lexicon);
		when(vmm.getAllVisualLexicon()).thenReturn(lex);
		when(vmm.getDefaultVisualStyle()).thenReturn(this.style);

		CyNetworkViewWriterFactory cytoscapeJsWriterFactory = mock(CyNetworkViewWriterFactory.class);
		WriterListener writerListsner = mock(WriterListener.class);
		TaskMonitor headlessTaskMonitor = new HeadlessTaskMonitor();

		CyTableManager tableManager = mock(CyTableManager.class);
		
		VisualStyleFactory vsFactory = mock(VisualStyleFactory.class);
		VisualStyle emptyStyle = mock(VisualStyle.class);
		when(vsFactory.createVisualStyle(anyString())).thenReturn(emptyStyle);
		
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

		CySessionManager sessionManager = mock(CySessionManager.class);
		when(sessionManager.getCurrentSessionFileName()).thenReturn("testSession");
		
		saveSessionAsTaskFactory = mock(SaveSessionAsTaskFactory.class);
		Task mockTask = mock(Task.class);
		when(saveSessionAsTaskFactory.createTaskIterator((File) anyObject())).thenReturn(new TaskIterator(mockTask));
		openSessionTaskFactory = mock(OpenSessionTaskFactory.class);
		when(openSessionTaskFactory.createTaskIterator((File) anyObject())).thenReturn(new TaskIterator(mockTask));
		newSessionTaskFactory = mock(NewSessionTaskFactory.class);
		when(newSessionTaskFactory.createTaskIterator(true)).thenReturn(new TaskIterator(mockTask));
		CySwingApplication desktop = mock(CySwingApplication.class);
		LevelOfDetails lodTF = mock(LevelOfDetails.class);
		
		this.selectFirstNeighborsTaskFactory = mock(SelectFirstNeighborsTaskFactory.class);
		
		this.binder = new CyBinder(networkManager, viewManager, netFactory,
				tfm, cyApplicationManager, vmm, cytoscapeJsWriterFactory,
				edgeListReaderFactory, layouts, writerListsner,
				headlessTaskMonitor, tableManager, vsFactory,
				mappingFactoryManager, groupFactory, groupManager,
				rootNetworkManager, loadNetworkURLTaskFactory,
				cyPropertyServiceRef, networkSelectedNodesAndEdgesTaskFactory,
				edgeListReaderFactory, viewFactory, tableFactory, fitContent,
				edgeBundler, renderingEngineManager, sessionManager, 
				saveSessionAsTaskFactory, openSessionTaskFactory, newSessionTaskFactory, 
				desktop, lodTF, selectFirstNeighborsTaskFactory);
	}
	
	
	private VisualStyle initStyle() throws Exception {
		final CustomGraphicsManager cgManager = mock(CustomGraphicsManager.class);
		lexicon = new DVisualLexicon(cgManager);
		
		final CyEventHelper eventHelper = mock(CyEventHelper.class);
		passthroughFactory = new PassthroughMappingFactory(eventHelper);
		discreteFactory = new DiscreteMappingFactory(eventHelper);
		continuousFactory = new ContinuousMappingFactory(eventHelper);

		mappingFactoryManager.addFactory(passthroughFactory, null);
		mappingFactoryManager.addFactory(continuousFactory, null);
		mappingFactoryManager.addFactory(discreteFactory, null);
		
		this.style = generateVisualStyle(lexicon);
		setDefaults();
		setMappings();
		
		return style;
	}


	private final VisualStyle generateVisualStyle(final VisualLexicon lexicon) {

		final VisualLexiconManager lexManager = mock(VisualLexiconManager.class);
		final Set<VisualLexicon> lexSet = new HashSet<VisualLexicon>();
		lexSet.add(lexicon);
		final Collection<VisualProperty<?>> nodeVP = lexicon.getAllDescendants(BasicVisualLexicon.NODE);
		final Collection<VisualProperty<?>> edgeVP = lexicon.getAllDescendants(BasicVisualLexicon.EDGE);
		when(lexManager.getNodeVisualProperties()).thenReturn(nodeVP);
		when(lexManager.getEdgeVisualProperties()).thenReturn(edgeVP);

		when(lexManager.getAllVisualLexicon()).thenReturn(lexSet);

		final CyServiceRegistrar serviceRegistrar = mock(CyServiceRegistrar.class);
		final VisualMappingFunctionFactory ptFactory = mock(VisualMappingFunctionFactory.class);
		final CyEventHelper eventHelper = mock(CyEventHelper.class);
		final VisualStyleFactoryImpl visualStyleFactory = new VisualStyleFactoryImpl(lexManager, serviceRegistrar,
				ptFactory, eventHelper);

		return visualStyleFactory.createVisualStyle("vs1");
	}

	private final void setDefaults() {
		// Node default values
		style.setDefaultValue(BasicVisualLexicon.NODE_FILL_COLOR, new Color(10, 10, 200));
		style.setDefaultValue(BasicVisualLexicon.NODE_TRANSPARENCY, 200);

		style.setDefaultValue(BasicVisualLexicon.NODE_WIDTH, 40d);
		style.setDefaultValue(BasicVisualLexicon.NODE_HEIGHT, 30d);
		style.setDefaultValue(BasicVisualLexicon.NODE_SIZE, 60d);

		style.setDefaultValue(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.ROUND_RECTANGLE);

		style.setDefaultValue(BasicVisualLexicon.NODE_BORDER_PAINT, Color.BLUE);
		style.setDefaultValue(BasicVisualLexicon.NODE_BORDER_WIDTH, 2d);
		style.setDefaultValue(BasicVisualLexicon.NODE_BORDER_TRANSPARENCY, 150);

		style.setDefaultValue(BasicVisualLexicon.NODE_LABEL_COLOR, Color.BLUE);
		style.setDefaultValue(BasicVisualLexicon.NODE_LABEL_FONT_SIZE, 18);
		style.setDefaultValue(BasicVisualLexicon.NODE_LABEL_FONT_FACE, new Font("Helvetica", Font.PLAIN, 12));
		style.setDefaultValue(BasicVisualLexicon.NODE_LABEL_TRANSPARENCY, 122);
		style.setDefaultValue(DVisualLexicon.NODE_LABEL_POSITION,
				new ObjectPositionImpl(Position.NORTH_EAST, Position.CENTER, Justification.JUSTIFY_CENTER, 0,0));

		// For Selected
		style.setDefaultValue(BasicVisualLexicon.NODE_SELECTED_PAINT, Color.RED);

		// Edge default values
		style.setDefaultValue(BasicVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT, new Color(12,100,200));
		style.setDefaultValue(BasicVisualLexicon.EDGE_UNSELECTED_PAINT, new Color(222, 100, 10));

		style.setDefaultValue(BasicVisualLexicon.EDGE_TRANSPARENCY, 100);

		style.setDefaultValue(BasicVisualLexicon.EDGE_LINE_TYPE, LineTypeVisualProperty.DOT);

		style.setDefaultValue(BasicVisualLexicon.EDGE_WIDTH, 3d);

		style.setDefaultValue(BasicVisualLexicon.EDGE_LABEL_COLOR, Color.red);
		style.setDefaultValue(BasicVisualLexicon.EDGE_LABEL_FONT_FACE, new Font("SansSerif", Font.BOLD, 12));
		style.setDefaultValue(BasicVisualLexicon.EDGE_LABEL_FONT_SIZE, 11);
		style.setDefaultValue(BasicVisualLexicon.EDGE_LABEL_TRANSPARENCY, 220);

		style.setDefaultValue(BasicVisualLexicon.EDGE_TARGET_ARROW_SHAPE, ArrowShapeVisualProperty.DELTA);
		style.setDefaultValue(BasicVisualLexicon.EDGE_SOURCE_ARROW_SHAPE, ArrowShapeVisualProperty.T);
		
		style.setDefaultValue(DVisualLexicon.EDGE_TARGET_ARROW_UNSELECTED_PAINT, new Color(20, 100, 100));
		style.setDefaultValue(DVisualLexicon.EDGE_SOURCE_ARROW_UNSELECTED_PAINT, new Color(10, 100, 100));
		
		// For Selected
		style.setDefaultValue(BasicVisualLexicon.EDGE_SELECTED_PAINT, Color.PINK);
		style.setDefaultValue(BasicVisualLexicon.EDGE_STROKE_SELECTED_PAINT, Color.ORANGE);
	}

	private final void setMappings() {
		// Passthrough mappings
		final VisualMappingFunction<String, String> nodeLabelMapping = passthroughFactory.createVisualMappingFunction(
				CyNetwork.NAME, String.class, BasicVisualLexicon.NODE_LABEL);
		final VisualMappingFunction<String, String> edgeLabelMapping = passthroughFactory.createVisualMappingFunction(
				CyEdge.INTERACTION, String.class, BasicVisualLexicon.EDGE_LABEL);
		style.addVisualMappingFunction(nodeLabelMapping);
		style.addVisualMappingFunction(edgeLabelMapping);

		// Continuous mappings
		// Simple two points mapping.
		final ContinuousMapping<Integer, Paint> nodeLabelColorMapping = (ContinuousMapping<Integer, Paint>) continuousFactory
				.createVisualMappingFunction("Degree", Integer.class, BasicVisualLexicon.NODE_LABEL_COLOR);
		
		final ContinuousMapping<Double, Integer> nodeOpacityMapping = (ContinuousMapping<Double, Integer>) continuousFactory
				.createVisualMappingFunction("Betweenness Centrality", Double.class, BasicVisualLexicon.NODE_TRANSPARENCY);
		
		final ContinuousMapping<Integer, Double> nodeWidthMapping = (ContinuousMapping<Integer, Double>) continuousFactory
				.createVisualMappingFunction("Degree", Integer.class, BasicVisualLexicon.NODE_WIDTH);
		final ContinuousMapping<Integer, Double> nodeHeightMapping = (ContinuousMapping<Integer, Double>) continuousFactory
				.createVisualMappingFunction("Degree", Integer.class, BasicVisualLexicon.NODE_HEIGHT);
		
		// Complex multi-point mapping
		final ContinuousMapping<Integer, Paint> nodeColorMapping = (ContinuousMapping<Integer, Paint>) continuousFactory
				.createVisualMappingFunction("Degree", Integer.class, BasicVisualLexicon.NODE_FILL_COLOR);

		final BoundaryRangeValues<Paint> lc1 = new BoundaryRangeValues<Paint>(Color.black, Color.yellow, Color.green);
		final BoundaryRangeValues<Paint> lc2 = new BoundaryRangeValues<Paint>(Color.red, Color.pink, Color.blue);
		nodeLabelColorMapping.addPoint(3, lc1);
		nodeLabelColorMapping.addPoint(10, lc2);
		style.addVisualMappingFunction(nodeLabelColorMapping);
		
		final BoundaryRangeValues<Paint> color1 = new BoundaryRangeValues<Paint>(Color.black, Color.red, Color.orange);
		final BoundaryRangeValues<Paint> color2 = new BoundaryRangeValues<Paint>(Color.white, Color.white, Color.white);
		final BoundaryRangeValues<Paint> color3= new BoundaryRangeValues<Paint>(Color.green, Color.pink, Color.blue);
		
		// Shuffle insertion.
		nodeColorMapping.addPoint(2, color1);
		nodeColorMapping.addPoint(5, color2);
		nodeColorMapping.addPoint(10, color3);

		final BoundaryRangeValues<Double> bv0 = new BoundaryRangeValues<Double>(20d, 20d, 20d);
		final BoundaryRangeValues<Double> bv1 = new BoundaryRangeValues<Double>(200d, 200d, 400d);
		nodeWidthMapping.addPoint(1, bv0);
		nodeWidthMapping.addPoint(20, bv1);
		nodeHeightMapping.addPoint(1, bv0);
		nodeHeightMapping.addPoint(20, bv1);

		final BoundaryRangeValues<Integer> trans0 = new BoundaryRangeValues<Integer>(10, 10, 10);
		final BoundaryRangeValues<Integer> trans1 = new BoundaryRangeValues<Integer>(80, 80, 100);
		final BoundaryRangeValues<Integer> trans2 = new BoundaryRangeValues<Integer>(222, 222, 250);
		nodeOpacityMapping.addPoint(0.22, trans0);
		nodeOpacityMapping.addPoint(0.61, trans1);
		nodeOpacityMapping.addPoint(0.95, trans2);

		style.addVisualMappingFunction(nodeWidthMapping);
		style.addVisualMappingFunction(nodeHeightMapping);
		style.addVisualMappingFunction(nodeOpacityMapping);
		style.addVisualMappingFunction(nodeColorMapping);

		// Discrete mappings
		final DiscreteMapping<String, NodeShape> nodeShapeMapping = (DiscreteMapping<String, NodeShape>) discreteFactory
				.createVisualMappingFunction("Node Type", String.class, BasicVisualLexicon.NODE_SHAPE);
		nodeShapeMapping.putMapValue("gene", NodeShapeVisualProperty.DIAMOND);
		nodeShapeMapping.putMapValue("protein", NodeShapeVisualProperty.ELLIPSE);
		nodeShapeMapping.putMapValue("compound", NodeShapeVisualProperty.ROUND_RECTANGLE);
		nodeShapeMapping.putMapValue("pathway", NodeShapeVisualProperty.OCTAGON);

		style.addVisualMappingFunction(nodeShapeMapping);

		final DiscreteMapping<String, ObjectPosition> nodeLabelPosMapping = (DiscreteMapping<String, ObjectPosition>) discreteFactory
				.createVisualMappingFunction("Node Type", String.class, DVisualLexicon.NODE_LABEL_POSITION);
		nodeLabelPosMapping.putMapValue("gene", new ObjectPositionImpl(Position.SOUTH, Position.NORTH_WEST, Justification.JUSTIFY_CENTER, 0,0));
		nodeLabelPosMapping.putMapValue("protein", new ObjectPositionImpl(Position.EAST, Position.WEST, Justification.JUSTIFY_CENTER, 0,0));

		style.addVisualMappingFunction(nodeLabelPosMapping);

		final DiscreteMapping<String, Paint> edgeColorMapping = (DiscreteMapping<String, Paint>) discreteFactory
				.createVisualMappingFunction("interaction", String.class,
						BasicVisualLexicon.EDGE_UNSELECTED_PAINT);
		edgeColorMapping.putMapValue("pp", Color.green);
		edgeColorMapping.putMapValue("pd", Color.red);

		style.addVisualMappingFunction(edgeColorMapping);
		
		final DiscreteMapping<String, Integer> edgeTransparencyMapping = (DiscreteMapping<String, Integer>) discreteFactory
				.createVisualMappingFunction("interaction", String.class,
						BasicVisualLexicon.EDGE_TRANSPARENCY);
		edgeTransparencyMapping.putMapValue("pp", 222);
		edgeTransparencyMapping.putMapValue("pd", 123);

		style.addVisualMappingFunction(edgeTransparencyMapping);
	}


	/**
	 * Create a simple network for testing.
	 * 
	 * @return sample network
	 */
	private final CyNetwork createNetwork(String networkName) {
		final CyNetwork network = nvts.getNetwork();
		network.getRow(network).set(CyNetwork.NAME, networkName);
		CyNode n1 = network.addNode();
		CyNode n2 = network.addNode();
		CyNode n3 = network.addNode();
		CyNode n4 = network.addNode();

		network.getRow(n1).set(CyNetwork.NAME, "n1");
		network.getRow(n2).set(CyNetwork.NAME, "n2");
		network.getRow(n3).set(CyNetwork.NAME, "n3");
		network.getRow(n4).set(CyNetwork.NAME, "n4");
		
		// For local table tests
		final CyTable localNodeTable = network.getTable(
				CyNode.class, CyNetwork.LOCAL_ATTRS);
		
		localNodeTable.createColumn("local1", Double.class, false);
		localNodeTable.getRow(n1.getSUID()).set("local1", 1.0);
		localNodeTable.getRow(n2.getSUID()).set("local1", 2.0);
		localNodeTable.getRow(n3.getSUID()).set("local1", 3.0);
		localNodeTable.getRow(n4.getSUID()).set("local1", 4.0);
		
		final CyEdge e1 = network.addEdge(n1, n2, true);
		final CyEdge e2 = network.addEdge(n2, n3, true);
		final CyEdge e3 = network.addEdge(n3, n1, true);
		
		network.getRow(e1).set(CyEdge.INTERACTION, "pp");
		network.getRow(e2).set(CyEdge.INTERACTION, "pp");
		network.getRow(e3).set(CyEdge.INTERACTION, "pd");

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
									GlobalTableResource.class,
									SessionResource.class,
									NetworkNameResource.class,
									UIResource.class);
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
