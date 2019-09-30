package org.cytoscape.rest.internal.resource;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.rest.internal.ClearAllEdgeBends;
import org.cytoscape.rest.internal.EdgeBundler;
import org.cytoscape.rest.internal.datamapper.MapperUtil;
import org.cytoscape.rest.internal.model.LayoutColumnTypesModel;
import org.cytoscape.rest.internal.model.LayoutModel;
import org.cytoscape.rest.internal.model.LayoutParameterModel;
import org.cytoscape.rest.internal.model.MessageModel;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 
 * Algorithmic resources.
 * Runs Cytoscape tasks, such as layouts or apply Style.
 * 
 */
@Api()
@Singleton
@Path("/v1/apply")
public class AlgorithmicResource extends AbstractResource {

	@Inject
	@NotNull
	private TaskMonitor headlessTaskMonitor;

	@Inject
	@NotNull
	private CyLayoutAlgorithmManager layoutManager;

	@Inject
	@NotNull
	private NetworkViewTaskFactory fitContent;

	@Inject
	@NotNull
	private EdgeBundler edgeBundler;
	
	@Inject
	@NotNull
	private ClearAllEdgeBends clearAllEdgeBends;

	private static final String RESOURCE_URN = "apply";

	@Override
	public String getResourceURI() {
		return RESOURCE_URN;
	}
	
	private final static Logger logger = LoggerFactory.getLogger(AlgorithmicResource.class);
	
	@Override
	public Logger getResourceLogger() {
		return logger;
	}
	
	private static final int NETWORK_NOT_FOUND_ERROR= 1;
	private static final int NETWORK_VIEW_NOT_FOUND_ERROR = 2;
	private static final int LAYOUT_ALGORITHM_NOT_FOUND_ERROR = 3;
	private static final int STYLE_NOT_FOUND_ERROR = 4;
	
	private static final int ALGORITHM_EXECUTION_ERROR = 5;
	private static final int INVALID_PARAMETER_ERROR = 6;
	private static final int ILLEGAL_Y_FILES_ACCESS_ERROR  = 7;
	
	@GET
	@Path("/layouts/{algorithmName}/{networkId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Apply a Layout to a Network", 
		notes="Applies the Layout specified by the `algorithmName` parameter to the Network "
				+ "specified by the `networkId` parameter. If the Layout is has an option to "
				+ "use a Column, it can be specified by the `column` parameter.",
		tags={CyRESTSwagger.CyRESTSwaggerConfig.LAYOUTS_TAG})
	public MessageModel applyLayout(
			@ApiParam(value="Name of layout algorithm", example="circular") @PathParam("algorithmName") String algorithmName,
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId,
			@ApiParam(value="Name of the Column to be used by the Layout", required=false) @QueryParam("column") String column) {
		
		throw404ifYFiles(algorithmName);
		
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		final Collection<CyNetworkView> views = 
				this.networkViewManager.getNetworkViews(network);
		if (views.isEmpty()) {
			//throw new NotFoundException(
			//		"Could not find view for the network with SUID: " 
			//		+ networkId);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					NETWORK_VIEW_NOT_FOUND_ERROR, 
					"Could not find view for the network with SUID: " + networkId, 
					getResourceLogger(), null);
		}

		final CyNetworkView view = views.iterator().next();
		final CyLayoutAlgorithm layout = this.layoutManager.getLayout(algorithmName);
		if (layout == null) {
			//throw new NotFoundException("No such layout algorithm: " + algorithmName);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					LAYOUT_ALGORITHM_NOT_FOUND_ERROR, 
					"No such layout algorithm: " + algorithmName, 
					getResourceLogger(), null);
		}

		String columnForLayout = column;
		if(columnForLayout == null) {
			columnForLayout = "";
		}
		
		final TaskIterator itr = layout.createTaskIterator(view,
				layout.getDefaultLayoutContext(),
				CyLayoutAlgorithm.ALL_NODE_VIEWS, columnForLayout);
		try {
			itr.next().run(headlessTaskMonitor);
		} catch (Exception e) {
			//throw getError("Could not apply layout.", e,
			//		Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					ALGORITHM_EXECUTION_ERROR, 
					"Could not apply layout.", 
					getResourceLogger(), e);
		}
	
		return new MessageModel("Layout finished.");		
	}
	
	@GET
	@Path("/layouts/{algorithmName}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get all details of a Layout algorithm", tags={CyRESTSwagger.CyRESTSwaggerConfig.LAYOUTS_TAG},
	 notes = "Returns all the details, including names, parameters, and compatible column types for the Layout algorithm "
	 		+ "specified by the `algorithmName` parameter.",
	 		response=LayoutModel.class
			)
	public Response getLayout(
			@ApiParam(value="Name of the Layout algorithm") @PathParam("algorithmName") String algorithmName) {
		
		throw404ifYFiles(algorithmName);
		
		final CyLayoutAlgorithm layout = this.layoutManager.getLayout(algorithmName);
		if (layout == null) {
			//throw new NotFoundException("No such layout algorithm: " + algorithmName);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					LAYOUT_ALGORITHM_NOT_FOUND_ERROR, 
					"No such layout algorithm: " + algorithmName, 
					getResourceLogger(), null);
		}

		Map<String, Object> params;
		try {
			params = getLayoutDetails(layout);
		} catch (Exception e) {
			//throw getError("Could not get layout parameters.", e,
			//		Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					INVALID_PARAMETER_ERROR, 
					"Could not get layout parameters.", 
					getResourceLogger(), e);
		}
		return Response.status(Response.Status.OK)
				.entity(params)
				.type(MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("/layouts/{algorithmName}/parameters")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get Layout parameters", tags={CyRESTSwagger.CyRESTSwaggerConfig.LAYOUTS_TAG},
			notes="Returns all editable parameters for the Layout algorithm specified by the "
					+ "`algorithmName` parameter.",
					response=LayoutParameterModel.class, responseContainer="List")
	public Response getLayoutParameters(
			@ApiParam(value="Name of the Layout algorithm") @PathParam("algorithmName") String algorithmName) {
		
		throw404ifYFiles(algorithmName);
		
		final CyLayoutAlgorithm layout = this.layoutManager.getLayout(algorithmName);
		if (layout == null) {
			//throw new NotFoundException("No such layout algorithm: " + algorithmName);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					LAYOUT_ALGORITHM_NOT_FOUND_ERROR, 
					"No such layout algorithm: " + algorithmName, 
					getResourceLogger(), null);
		}

		final List<Map<String, Object>> params;
		try {
			params = getLayoutParameterDetails(layout);
		} catch (Exception e) {
			//throw getError("Could not get layout parameters.", e,
			//		Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					INVALID_PARAMETER_ERROR, 
					"Could not get layout parameters.", 
					getResourceLogger(), e);
		}
		
		return Response.status(Response.Status.OK)
				.entity(params)
				.type(MediaType.APPLICATION_JSON).build();
	}
	
	
	@GET
	@Path("/layouts/{algorithmName}/columntypes")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get column data types compatible a Layout algorithm", 
		tags={CyRESTSwagger.CyRESTSwaggerConfig.LAYOUTS_TAG},
		notes="Returns a list of all compatible column data types for the Layout algorithm "
				+ "specified by the `algorithmName` parameter.",
		response=LayoutColumnTypesModel.class)
	public Response getCompatibleColumnDataTypes(
			@ApiParam(value="Name of layout algorithm") @PathParam("algorithmName") String algorithmName) {

		throw404ifYFiles(algorithmName);
		
		final CyLayoutAlgorithm layout = this.layoutManager.getLayout(algorithmName);
		if (layout == null) {
			//throw new NotFoundException("No such layout algorithm: " + algorithmName);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					LAYOUT_ALGORITHM_NOT_FOUND_ERROR, 
					"No such layout algorithm: " + algorithmName, 
					getResourceLogger(), null);
		}
		final Map<String, Set<String>> result = getCompatibleTypes(layout);
		return Response.status(Response.Status.OK)
				.entity(result)
				.type(MediaType.APPLICATION_JSON).build();
	}
	
	private Map<String, Set<String>> getCompatibleTypes(final CyLayoutAlgorithm layout) {
		final Map<String, Set<String>> result = new HashMap<>();
		Set<Class<?>> compatibleTypes = Collections.emptySet();
		if (layout.getSupportedNodeAttributeTypes().isEmpty() == false) {
			compatibleTypes = layout.getSupportedNodeAttributeTypes();
			final Set<String> nodeSet = compatibleTypes.stream().map(type -> type.getSimpleName())
					.collect(Collectors.toSet());
			result.put("compatibleNodeColumnDataTypes", nodeSet);
		}

		if (layout.getSupportedEdgeAttributeTypes().isEmpty() == false) {
			compatibleTypes = layout.getSupportedEdgeAttributeTypes();
			final Set<String> edgeSet = compatibleTypes.stream().map(type -> type.getSimpleName())
					.collect(Collectors.toSet());
			result.put("compatibleEdgeColumnDataTypes", edgeSet);
		}
		return result;
	}
	
	
	@PUT
	@Path("/layouts/{algorithmName}/parameters")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Update Layout parameters for a Layout algorithm", tags={CyRESTSwagger.CyRESTSwaggerConfig.LAYOUTS_TAG},
	notes="Updates the Layout parameters for the Layout algorithm specified by the `algorithmName` parameter."
	)
	@ApiImplicitParams(
			@ApiImplicitParam(value="A list of Layout Parameters with Values.", 
				dataType="[Lorg.cytoscape.rest.internal.model.LayoutParameterValueModel;", paramType="body", required=true)
			)
	public Response updateLayoutParameters(
			@ApiParam(value="Name of the layout algorithm") @PathParam("algorithmName") String algorithmName, 
			@ApiParam(hidden=true) final InputStream is
			) {
		
		throw404ifYFiles(algorithmName);
		
		final ObjectMapper objMapper = new ObjectMapper();
		final CyLayoutAlgorithm layout = this.layoutManager.getLayout(algorithmName);
		final Object context = layout.getDefaultLayoutContext();
		
		try {
			final Map<String, Class<?>> params = getParameterTypes(layout);
			
		
			
			// This should be an JSON array.
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
			for (final JsonNode entry : rootNode) {
				final String parameterName = entry.get("name").asText();
				final Class<?> type = params.get(parameterName);
				
				if(type == null) {
					//throw new NotFoundException("No such parameter: " + parameterName);
					//Was a 404 above, but params are not in the path, so this was changed below to 500
					throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
							getResourceURI(), 
							INVALID_PARAMETER_ERROR, 
							"No such parameter: " + parameterName, 
							getResourceLogger(), null);
				}
				
				final JsonNode val = entry.get("value");
				final Object value = MapperUtil.getValue(val, params.get(parameterName));
				context.getClass().getField(parameterName).set(context, value);
			}
			
		} catch (Exception e) {
			//throw getError(
			//		"Could not parse the input JSON for updating view because: "
			//				+ e.getMessage(), e,
			//		Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					INVALID_PARAMETER_ERROR, 
					"Could not parse input JSON.", 
					getResourceLogger(), e);
		}
		
		return Response.status(Response.Status.OK)
				.type(MediaType.APPLICATION_JSON).build();
	}
	
	
	private final Map<String, Class<?>> getParameterTypes(final CyLayoutAlgorithm layout) {
		final Object context = layout.getDefaultLayoutContext();
		final List<Field> fields = Arrays.asList(context.getClass().getFields());
		return fields.stream()
			.filter(field -> field.getAnnotation(Tunable.class) != null)
			.collect(Collectors.toMap(Field::getName, Field::getType));
	}
	
	private final Map<String, Object> getLayoutDetails(final CyLayoutAlgorithm layout) throws NoSuchFieldException, SecurityException {
		final Map<String, Object> layoutInfo = new HashMap<String, Object>();
		layoutInfo.put("name", layout.getName());
		layoutInfo.put("longName", layout.toString());
		layoutInfo.put("parameters", getLayoutParameterDetails(layout));
		layoutInfo.put("compatibleColumnDataTypes", getCompatibleTypes(layout));
		return layoutInfo;
	}
	
	private final List<Map<String, Object>> getLayoutParameterDetails(final CyLayoutAlgorithm layout) {
		return Arrays.asList(
				layout.getDefaultLayoutContext().getClass().getFields()
			).stream()
				.filter(field -> field.getAnnotation(Tunable.class) != null)
				.map(field -> buildLayoutParamEntry(layout, field))
				.collect(Collectors.toList());
		
	}
	
	private final Map<String, Object> buildLayoutParamEntry(final CyLayoutAlgorithm layout, final Field field) {
		final Map<String, Object> entryMap = new HashMap<>();
		final Tunable tunable = field.getAnnotation(Tunable.class);
		
		entryMap.put("name", field.getName());
		entryMap.put("description", tunable.description());
		entryMap.put("type", field.getType().getSimpleName());
		try {
			entryMap.put("value", field.get(layout.getDefaultLayoutContext()));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return entryMap;
	}

	@GET
	@Path("/styles/{styleName}/{networkId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Apply Visual Style to a network", 
		notes="Applies the Visual Style specified by the `styleName` parameter to the network specified by the `networkId` parameter.",
		tags={CyRESTSwagger.CyRESTSwaggerConfig.VISUAL_STYLES_TAG})
	public MessageModel applyStyle(
			@ApiParam(value="Name of the Visual Style") @PathParam("styleName") String styleName,
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId
			) {

		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);
		final Set<VisualStyle> styles = vmm.getAllVisualStyles();
		VisualStyle targetStyle = null;
		for (final VisualStyle style : styles) {
			final String name = style.getTitle();
			if (name.equals(styleName)) {
				targetStyle = style;
				break;
			}
		}

		if (targetStyle == null) {
			//throw new NotFoundException("Visual Style does not exist: "
			//		+ styleName);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					STYLE_NOT_FOUND_ERROR, 
					"Visual Style does not exist: "	+ styleName, 
					getResourceLogger(), null);
		}

		Collection<CyNetworkView> views = this.networkViewManager
				.getNetworkViews(network);
		if (views.isEmpty()) {
			//throw new NotFoundException(
			//		"Network view does not exist for the network with SUID: "
			//				+ networkId);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					NETWORK_VIEW_NOT_FOUND_ERROR, 
					"Network view does not exist for the network with SUID: " + networkId, 
					getResourceLogger(), null);
		}

		final CyNetworkView view = views.iterator().next();
		vmm.setVisualStyle(targetStyle, view);
		vmm.setCurrentVisualStyle(targetStyle);
		targetStyle.apply(view);

		return new MessageModel("Visual Style applied.");
	}

	@GET
	@Path("/fit/{networkId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
		value="Fit network to the window", 
		tags={CyRESTSwagger.CyRESTSwaggerConfig.NETWORK_VIEWS_TAG},
		notes="Fit the first available Network View for the Network specified by the `networkId` parameter to the current window.")
	public MessageModel fitContent(
			@ApiParam(value="SUID of the Network", required=true) @PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);

		Collection<CyNetworkView> views = this.networkViewManager
				.getNetworkViews(network);
		
		if (views.isEmpty()) {
			//throw new NotFoundException(
			//		"Network view does not exist for the network with SUID: "
			//				+ networkId);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					NETWORK_VIEW_NOT_FOUND_ERROR, 
					"Network view does not exist for the network with SUID: " + networkId, 
					getResourceLogger(), null);
		}
		CyNetworkView view = views.iterator().next();
		TaskIterator fit = fitContent.createTaskIterator(view);
		try {
			fit.next().run(headlessTaskMonitor);
		} catch (Exception e) {
			//throw getError("Could not fit content.", e,
			//		Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					ALGORITHM_EXECUTION_ERROR, 
					"Could not fit content.", 
					getResourceLogger(), e);
		}
		return new MessageModel("Fit content success.");
	}

	@GET
	@Path("/edgebundling/{networkId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value="Apply Edge Bundling to a network", 
			tags={CyRESTSwagger.CyRESTSwaggerConfig.LAYOUTS_TAG},
			notes="Apply edge bundling to the Network specified by the `networkId` parameter. Edge bundling is executed with default parameters; at present, optional parameters are not supported."
			)
	public MessageModel bundleEdge(
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);

		Collection<CyNetworkView> views = this.networkViewManager
				.getNetworkViews(network);
		if (views.isEmpty()) {
			//throw new NotFoundException(
			//		"Network view does not exist for the network with SUID: "
			//				+ networkId);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					NETWORK_VIEW_NOT_FOUND_ERROR, 
					"Network view does not exist for the network with SUID: " + networkId, 
					getResourceLogger(), null);
		}
		final TaskIterator bundler = edgeBundler.getBundlerTF()
				.createTaskIterator(network);
		try {
			bundler.next().run(headlessTaskMonitor);
		} catch (Exception e) {
			//throw getError("Could not finish edge bundling.", e,
			//		Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					ALGORITHM_EXECUTION_ERROR, 
					"Could not finish edge bundling.", 
					getResourceLogger(), e);
		}

		return new MessageModel("Edge bundling success.");
	}

	@GET
	@Path("/clearalledgebends/{networkId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value="Clear all edge bends in a network", 
			tags={CyRESTSwagger.CyRESTSwaggerConfig.LAYOUTS_TAG},
			notes="Clear all edge bends in the Network specified by the `networkId` parameter."
			)
	public MessageModel clearAllEdgeBends(
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(NETWORK_NOT_FOUND_ERROR, networkId);

		Collection<CyNetworkView> views = this.networkViewManager
				.getNetworkViews(network);
		if (views.isEmpty()) {
			//throw new NotFoundException(
			//		"Network view does not exist for the network with SUID: "
			//				+ networkId);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					NETWORK_VIEW_NOT_FOUND_ERROR, 
					"Network view does not exist for the network with SUID: " + networkId, 
					getResourceLogger(), null);
		}
		final TaskIterator clearAllEdgeBendsTaskIterator = clearAllEdgeBends.getClearAllEdgeBendsTF()
				.createTaskIterator(views);
		try {
			clearAllEdgeBendsTaskIterator.next().run(headlessTaskMonitor);
		} catch (Exception e) {
			//throw getError("Could not finish edge bundling.", e,
			//		Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					ALGORITHM_EXECUTION_ERROR, 
					"Could not finish clearing all edge bends.", 
					getResourceLogger(), e);
		}

		return new MessageModel("Clear all edge bends success.");
	}
	
	@GET
	@Path("/layouts")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			nickname="layoutList",
			value="Get all available Layout names", 
			tags={CyRESTSwagger.CyRESTSwaggerConfig.LAYOUTS_TAG},
			notes="Returns all available layouts as a list of layout names.\n\n"
					+ "<h3>Important Note</h3>\n\n" 
					+"This <strong>does not include yFiles layout algorithms</strong>, due to license issues."
			)
	public Collection<String> getLayoutNames() {
		return layoutManager.getAllLayouts().stream()
			.map(layout->layout.getName()).filter(name -> !isYFiles(name))
			.collect(Collectors.toList());
	}


	
	@GET
	@Path("/styles")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value="Get list of all Visual Style names", 
			tags={CyRESTSwagger.CyRESTSwaggerConfig.VISUAL_STYLES_TAG},
			notes="Returns a list of all Visual Style names. Style names may not be unique."
			)
	public Collection<String> getStyleNames() {
		return vmm.getAllVisualStyles().stream()
				.map(style->style.getTitle())
				.collect(Collectors.toList());
	}
	
	private static boolean isYFiles(String name) {
		return name.startsWith("com.yworks.yfiles.layout") || name.startsWith("yfiles.");
	}
	
	private void throw404ifYFiles(String algorithmName) {
		if (isYFiles(algorithmName)) {
			//throw getError("No such layout: " + algorithmName,
			//		new Exception("No such layout: " + algorithmName), Response.Status.NOT_FOUND);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					ILLEGAL_Y_FILES_ACCESS_ERROR, 
					"No such layout: " + algorithmName, 
					getResourceLogger(), null);
		}
	}
	
}