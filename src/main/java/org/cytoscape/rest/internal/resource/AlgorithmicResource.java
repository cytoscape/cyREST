package org.cytoscape.rest.internal.resource;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.rest.internal.EdgeBundler;
import org.cytoscape.rest.internal.datamapper.MapperUtil;
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

/**
 * 
 * Algorithmic resources. Essentially, this is a high-level task executor.
 * 
 */
@Singleton
@Path("/v1/apply")
public class AlgorithmicResource extends AbstractResource {

	@Context
	@NotNull
	private TaskMonitor headlessTaskMonitor;

	@Context
	@NotNull
	private CyLayoutAlgorithmManager layoutManager;

	@Context
	@NotNull
	private NetworkTaskFactory fitContent;

	@Context
	@NotNull
	private EdgeBundler edgeBundler;

	/**
	 * 
	 * @summary Apply layout to a network
	 * 
	 * @param algorithmName
	 *            Name of layout algorithm ("circular", "force-directed", etc.)
	 * @param networkId
	 *            Target network SUID
	 * 
	 * @return Success message
	 */
	@GET
	@Path("/layouts/{algorithmName}/{networkId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response applyLayout(
			@PathParam("algorithmName") String algorithmName,
			@PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		final Collection<CyNetworkView> views = this.networkViewManager.getNetworkViews(network);
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

		final TaskIterator itr = layout.createTaskIterator(view,
				layout.getDefaultLayoutContext(),
				CyLayoutAlgorithm.ALL_NODE_VIEWS, "");
		try {
			itr.next().run(headlessTaskMonitor);
		} catch (Exception e) {
			throw getError("Could not apply layout.", e,
					Response.Status.INTERNAL_SERVER_ERROR);
		}
		return Response.status(Response.Status.OK)
				.entity("{\"message\":\"Layout finished.\"}")
				.type(MediaType.APPLICATION_JSON).build();
	}
	
	
	@GET
	@Path("/layouts/{algorithmName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLayoutParameters(@PathParam("algorithmName") String algorithmName) {

		final CyLayoutAlgorithm layout = this.layoutManager.getLayout(algorithmName);
		if (layout == null) {
			throw new NotFoundException("No such layout algorithm: " + algorithmName);
		}

		List<Map<String, Object>> params;
		try {
			params = getLayoutParams(layout);
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
	public Response getCompatibleColumnDataTypes(
			@PathParam("algorithmName") String algorithmName) {

		final CyLayoutAlgorithm layout = this.layoutManager.getLayout(algorithmName);
		if (layout == null) {
			throw new NotFoundException("No such layout algorithm: " + algorithmName);
		}

		final Map<String, Set<String>> result= new HashMap<>();
		Set<Class<?>> compatibleTypes = Collections.emptySet();
		try {
			if(layout.getSupportedNodeAttributeTypes().isEmpty() == false) {
				compatibleTypes = layout.getSupportedNodeAttributeTypes();
				final Set<String> nodeSet = compatibleTypes.stream()
					.map(type -> type.getSimpleName())
					.collect(Collectors.toSet());
				result.put("compatibleNodeColumnDataTypes", nodeSet);
			}
			
			if(layout.getSupportedEdgeAttributeTypes().isEmpty() == false) {
				compatibleTypes = layout.getSupportedEdgeAttributeTypes();
				final Set<String> edgeSet = compatibleTypes.stream()
					.map(type -> type.getSimpleName())
					.collect(Collectors.toSet());
				result.put("compatibleEdgeColumnDataTypes", edgeSet);
			}

		} catch (Exception e) {
			throw getError("Could not get layout parameters.", e,
					Response.Status.INTERNAL_SERVER_ERROR);
		}
		return Response.status(Response.Status.OK)
				.entity(result)
				.type(MediaType.APPLICATION_JSON).build();
	}
	
	
	@PUT
	@Path("/layouts/{algorithmName}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateLayoutParameters(@PathParam("algorithmName") String algorithmName, final InputStream is) {
		
		final ObjectMapper objMapper = new ObjectMapper();
		final CyLayoutAlgorithm layout = this.layoutManager.getLayout(algorithmName);
		final Object context = layout.getDefaultLayoutContext();

		System.out.println("===================> " + layout.getName());
		
		try {
			final Map<String, Class<?>> params = getParameterTypes(layout);
			System.out.println("===================> params " + params.keySet());
			// This should be an JSON array.
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
			for (final JsonNode entry : rootNode) {
				final String parameterName = entry.get("name").asText();
				System.out.println("########===================> " + parameterName);
				final Class<?> type = params.get(parameterName);
				System.out.println("type===================> " + type);
				
				if(type == null) {
					System.out.println("!!!!!!!!! Could not found: " + parameterName);
					throw new NotFoundException("No such parameter: " + parameterName);
				}
				
				final JsonNode val = entry.get("value");
				final Object value = MapperUtil.getValue(val, params.get(parameterName));
				
				System.out.println("########===================> value is " + value);
				System.out.println("########===================>type2 " + value.getClass());
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
	
	private final List<Map<String, Object>> getLayoutParams(final CyLayoutAlgorithm layout) throws NoSuchFieldException, SecurityException {
		final Object context = layout.getDefaultLayoutContext();
		final List<Field> fields = Arrays.asList(context.getClass().getFields());
		List<Map<String, Object>> tunableFields = fields.stream()
			.filter(field -> field.getAnnotation(Tunable.class) != null)
			.map(field -> buildLayoutParamEntry(layout, field))
			.collect(Collectors.toList());
		
		return tunableFields;
		
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

	/**
	 * 
	 * @summary Apply Visual Style to a network
	 * 
	 * @param styleName
	 *            Visual Style name (title)
	 * @param networkId
	 *            Target network SUID
	 * 
	 * @return Success message.
	 */
	@GET
	@Path("/styles/{styleName}/{networkId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response applyStyle(@PathParam("styleName") String styleName,
			@PathParam("networkId") Long networkId) {

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

		return Response.status(Response.Status.OK)
				.type(MediaType.APPLICATION_JSON)
				.entity("{\"message\":\"Visual Style applied.\"}").build();
	}

	/**
	 * 
	 * Fit an existing network view to current window.
	 * 
	 * @summary Fit network to the window
	 * 
	 * @param networkId
	 *            Network SUID
	 * @return Success message
	 */
	@GET
	@Path("/fit/{networkId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fitContent(@PathParam("networkId") Long networkId) {
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

		return Response.status(Response.Status.OK)
				.type(MediaType.APPLICATION_JSON)
				.entity("{\"message\":\"Fit content success.\"}").build();
	}

	/**
	 * Apply edge bundling with default parameters. Currently optional
	 * parameters are not supported.
	 * 
	 * @summary Apply Edge Bundling to a network
	 * 
	 * @param networkId
	 *            Target network SUID
	 * @return Success message
	 * 
	 */
	@GET
	@Path("/edgebundling/{networkId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response bundleEdge(@PathParam("networkId") Long networkId) {
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

		return Response.status(Response.Status.OK)
				.type(MediaType.APPLICATION_JSON)
				.entity("{\"message\":\"Edge bundling success.\"}").build();
	}

	/**
	 * List of all available layout algorithm names. This <strong>does not
	 * include yFiles</strong> algorithms due to license issues.
	 *
	 * @summary Get list of available layout algorithm names
	 *
	 * 
	 * @return List of layout algorithm names.
	 */
	@GET
	@Path("/layouts")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<String> getLayoutNames() {
		try {
			final Collection<CyLayoutAlgorithm> layouts = layoutManager
					.getAllLayouts();
			final List<String> layoutNames = new ArrayList<String>();
			for (final CyLayoutAlgorithm layout : layouts) {
				layoutNames.add(layout.getName());
			}
			return layoutNames;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Could not get layout names.");
		}
	}

	/**
	 * Get list of all Visual Style names. This may not be unique.
	 * 
	 * @summary Get list of all Visual Style names
	 * 
	 * @return List of Visual Style names.
	 * 
	 */
	@GET
	@Path("/styles")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<String> getStyleNames() {
		final Set<VisualStyle> styles = vmm.getAllVisualStyles();
		final List<String> styleNames = new ArrayList<String>();
		for (final VisualStyle style : styles) {
			styleNames.add(style.getTitle());
		}
		return styleNames;
	}
}