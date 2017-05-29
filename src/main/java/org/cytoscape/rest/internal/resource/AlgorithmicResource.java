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
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.rest.internal.EdgeBundler;
import org.cytoscape.rest.internal.datamapper.MapperUtil;
import org.cytoscape.rest.internal.model.Message;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import io.swagger.annotations.Api;
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
	private NetworkTaskFactory fitContent;

	@Inject
	@NotNull
	private EdgeBundler edgeBundler;

	@GET
	@Path("/layouts/{algorithmName}/{networkId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Apply layout to a network", tags={CyRESTSwagger.CyRESTSwaggerConfig.LAYOUTS_TAG})
	public Message applyLayout(
			@ApiParam("Name of layout algorithm (\"circular\", \"force-directed\", etc.)") @PathParam("algorithmName") String algorithmName,
			@ApiParam("Network SUID") @PathParam("networkId") Long networkId,
			@ApiParam("Column name to be used by the layout algorithm") @QueryParam("column") String column) {
		final CyNetwork network = getCyNetwork(networkId);
		final Collection<CyNetworkView> views = 
				this.networkViewManager.getNetworkViews(network);
		if (views.isEmpty()) {
			throw new NotFoundException(
					"Could not find view for the network with SUID: " 
					+ networkId);
		}

		final CyNetworkView view = views.iterator().next();
		final CyLayoutAlgorithm layout = this.layoutManager.getLayout(algorithmName);
		if (layout == null) {
			throw new NotFoundException("No such layout algorithm: " + algorithmName);
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
			throw getError("Could not apply layout.", e,
					Response.Status.INTERNAL_SERVER_ERROR);
		}
	
		return new Message("Layout finished.");		
	}
	
	@GET
	@Path("/layouts/{algorithmName}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get layout parameters for the algorithm", tags={CyRESTSwagger.CyRESTSwaggerConfig.LAYOUTS_TAG},
	 notes = "The return value is an map of all layout details, and each parameter entry includes:\n\n"
			 + "* name: Unique name (ID) of the parameter\n"
			 + "* description: Description for the parameter\n"
			 + "* type: Java data type of the parameter\n"
			 + "* value: current value for the parameter field\n" 
			 + "\n"
			 + "Returns editable layout parameters\n"
			)
	public Response getLayout(
			@ApiParam(value="Name of the layout algorithm") @PathParam("algorithmName") String algorithmName) {
		final CyLayoutAlgorithm layout = this.layoutManager.getLayout(algorithmName);
		if (layout == null) {
			throw new NotFoundException("No such layout algorithm: " + algorithmName);
		}

		Map<String, Object> params;
		try {
			params = getLayoutDetails(layout);
		} catch (Exception e) {
			throw getError("Could not get layout parameters.", e,
					Response.Status.INTERNAL_SERVER_ERROR);
		}
		return Response.status(Response.Status.OK)
				.entity(params)
				.type(MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("/layouts/{algorithmName}/parameters")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get layout parameter list", tags={CyRESTSwagger.CyRESTSwaggerConfig.LAYOUTS_TAG},
			notes="Returns all editable parameters for this algorithm.")
	public Response getLayoutParameters(
			@ApiParam(value="Name of layout algorithm") @PathParam("algorithmName") String algorithmName) {
		final CyLayoutAlgorithm layout = this.layoutManager.getLayout(algorithmName);
		if (layout == null) {
			throw new NotFoundException("No such layout algorithm: " + algorithmName);
		}

		final List<Map<String, Object>> params;
		try {
			params = getLayoutParameterDetails(layout);
		} catch (Exception e) {
			throw getError("Could not get layout parameters.", e,
					Response.Status.INTERNAL_SERVER_ERROR);
		}
		
		return Response.status(Response.Status.OK)
				.entity(params)
				.type(MediaType.APPLICATION_JSON).build();
	}
	
	
	@GET
	@Path("/layouts/{algorithmName}/columntypes")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Column data types compatible with this algorithm", 
		tags={CyRESTSwagger.CyRESTSwaggerConfig.LAYOUTS_TAG},
		notes="Returns a list ist of all compatible column data types")
	public Response getCompatibleColumnDataTypes(
			@ApiParam(value="Name of layout algorithm") @PathParam("algorithmName") String algorithmName) {

		final CyLayoutAlgorithm layout = this.layoutManager.getLayout(algorithmName);
		if (layout == null) {
			throw new NotFoundException("No such layout algorithm: " + algorithmName);
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
	@ApiOperation(value="Update layout parameters for the algorithm", tags={CyRESTSwagger.CyRESTSwaggerConfig.LAYOUTS_TAG},
	notes="The body of your request should contain an array of new parameters. The data should look like the following:\n"
	+ "\n"
	+ "```\n"
	+ "[\n"
	+ " {\n"
	+ "  \"name\": \"nodeHorizontalSpacing\",\n"
	+ "  \"value\": 40.0\n"
	+ " }, ...\n"
	+ "]\n"
	+ "```\n"
	+ "where: \n"
	+ "* name: Unique name (ID) of the parameter\n"
	+ "* value: New value for the parameter field\n")
	public Response updateLayoutParameters(
			@ApiParam(value="Name of the layout algorithm") @PathParam("algorithmName") String algorithmName, 
			final InputStream is
			) {
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
					throw new NotFoundException("No such parameter: " + parameterName);
				}
				
				final JsonNode val = entry.get("value");
				final Object value = MapperUtil.getValue(val, params.get(parameterName));
				context.getClass().getField(parameterName).set(context, value);
			}
			
		} catch (Exception e) {
			throw getError(
					"Could not parse the input JSON for updating view because: "
							+ e.getMessage(), e,
					Response.Status.INTERNAL_SERVER_ERROR);
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
	@ApiOperation(value="Apply Visual Style to a network", tags={CyRESTSwagger.CyRESTSwaggerConfig.VISUAL_STYLES_TAG})
	public Message applyStyle(
			@ApiParam(value="Style Name") @PathParam("styleName") String styleName,
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId
			) {

		final CyNetwork network = getCyNetwork(networkId);
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
			throw new NotFoundException("Visual Style does not exist: "
					+ styleName);
		}

		Collection<CyNetworkView> views = this.networkViewManager
				.getNetworkViews(network);
		if (views.isEmpty()) {
			throw new NotFoundException(
					"Network view does not exist for the network with SUID: "
							+ networkId);
		}

		final CyNetworkView view = views.iterator().next();
		vmm.setVisualStyle(targetStyle, view);
		vmm.setCurrentVisualStyle(targetStyle);
		targetStyle.apply(view);

		return new Message("Visual Style applied.");
	}

	@GET
	@Path("/fit/{networkId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
		value="Fit network to the window", 
		tags={CyRESTSwagger.CyRESTSwaggerConfig.VISUAL_PROPERTIES_TAG},
		notes="Fit an existing network view to current window.")
	public Message fitContent(
			@ApiParam(value="Network SUID", required=true) @PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);

		Collection<CyNetworkView> views = this.networkViewManager
				.getNetworkViews(network);
		if (views.isEmpty()) {
			throw new NotFoundException(
					"Network view does not exist for the network with SUID: "
							+ networkId);
		}
		TaskIterator fit = fitContent.createTaskIterator(network);
		try {
			fit.next().run(headlessTaskMonitor);
		} catch (Exception e) {
			throw getError("Could not fit content.", e,
					Response.Status.INTERNAL_SERVER_ERROR);
		}
		return new Message("Fit content success.");
	}

	@GET
	@Path("/edgebundling/{networkId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value="Apply Edge Bundling to a network", 
			tags={CyRESTSwagger.CyRESTSwaggerConfig.LAYOUTS_TAG},
			notes="Apply edge bundling with default parameters. At present, optional parameters are not supported."
			)
	public Message bundleEdge(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);

		Collection<CyNetworkView> views = this.networkViewManager
				.getNetworkViews(network);
		if (views.isEmpty()) {
			throw new NotFoundException(
					"Network view does not exist for the network with SUID: "
							+ networkId);
		}
		final TaskIterator bundler = edgeBundler.getBundlerTF()
				.createTaskIterator(network);
		try {
			bundler.next().run(headlessTaskMonitor);
		} catch (Exception e) {
			throw getError("Could not finish edge bundling.", e,
					Response.Status.INTERNAL_SERVER_ERROR);
		}

		return new Message("Edge bundling success.");
	}

	@GET
	@Path("/layouts")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			nickname="layoutList",
			value="Get list of available layout algorithm names", 
			tags={CyRESTSwagger.CyRESTSwaggerConfig.LAYOUTS_TAG},
			notes="<h3>Important Note</h3>" 
			+" This <strong>does not include yFiles layout algorithms</strong>, due to license issues."
			)
	public Collection<String> getLayoutNames() {
		return layoutManager.getAllLayouts().stream()
			.map(layout->layout.getName())
			.collect(Collectors.toList());
	}


	
	@GET
	@Path("/styles")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value="Get list of all Visual Style names", 
			tags={CyRESTSwagger.CyRESTSwaggerConfig.VISUAL_STYLES_TAG},
			notes="Style names may not be unique."
			)
	public Collection<String> getStyleNames() {
		return vmm.getAllVisualStyles().stream()
				.map(style->style.getTitle())
				.collect(Collectors.toList());
	}
}