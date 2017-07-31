package org.cytoscape.rest.internal.resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.write.PresentationWriterFactory;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.rest.internal.CIUtils;
import org.cytoscape.rest.internal.CyNetworkViewWriterFactoryManager;
import org.cytoscape.rest.internal.GraphicsWriterManager;
import org.cytoscape.rest.internal.datamapper.VisualStyleMapper;
import org.cytoscape.rest.internal.model.CIResponse;
import org.cytoscape.rest.internal.model.Count;
import org.cytoscape.rest.internal.model.NetworkViewSUID;
import org.cytoscape.rest.internal.model.NetworkViewVisualProperty;
import org.cytoscape.rest.internal.serializer.VisualStyleSerializer;
import org.cytoscape.rest.internal.task.HeadlessTaskMonitor;
import org.cytoscape.rest.internal.task.LogLocation;
import org.cytoscape.task.write.ExportNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.util.BoundedDouble;
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
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * REST API for Network View objects.
 * 
 * TODO: add custom view information section
 * 
 */
@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.NETWORK_VIEWS_TAG})
@Singleton
@Path("/v1/networks/{networkId}/views")
public class NetworkViewResource extends AbstractResource {

	// Filter rendering engine by ID.
	private static final String DING_ID = "org.cytoscape.ding";

	private static final String DEF_HEIGHT = "600";

	private static final String RESOURCE_URN = "networks:views";

	private static final String COULD_NOT_FIND_RESOURCE_ERROR = "1";

	private static final String INVALID_PARAMETER_ERROR = "2";

	private final static Logger logger = LoggerFactory.getLogger(TableResource.class);

	@Inject
	@NotNull
	private RenderingEngineManager renderingEngineManager;

	@Inject
	@NotNull
	private GraphicsWriterManager graphicsWriterManager;

	@Inject
	@NotNull
	private ExportNetworkViewTaskFactory exportNetworkViewTaskFactory;

	@Inject
	@LogLocation
	private String logLocation;

	private final VisualStyleMapper styleMapper;
	private final VisualStyleSerializer styleSerializer;

	private VisualLexicon lexicon;
	private Collection<VisualProperty<?>> nodeLexicon;
	private Collection<VisualProperty<?>> edgeLexicon;
	private Collection<VisualProperty<?>> networkLexicon;



	public NetworkViewResource() {
		super();
		this.styleMapper = new VisualStyleMapper();
		this.styleSerializer = new VisualStyleSerializer();
	}

	private final void initLexicon() {
		// Prepare lexicon
		this.lexicon = getLexicon();
		nodeLexicon = lexicon.getAllDescendants(BasicVisualLexicon.NODE);
		edgeLexicon = lexicon.getAllDescendants(BasicVisualLexicon.EDGE);
		networkLexicon = lexicon.getAllDescendants(BasicVisualLexicon.NETWORK).stream()
				.filter(vp->vp.getIdString().startsWith("NETWORK")).collect(Collectors.toSet());;

	}

	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Create view for the network")
	@ApiResponses(value = { 
			@ApiResponse(code = 201, message = "Network View SUID", response = NetworkViewSUID.class),
	})
	public Response createNetworkView(@ApiParam(value="Network SUID", required=true) @PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyNetworkView view = networkViewFactory.createNetworkView(network);
		networkViewManager.addNetworkView(view);
		return Response.status(Response.Status.CREATED).entity(new NetworkViewSUID(view.getSUID())).build();
	}

	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get number of views for the given network model",
	notes = "Cytoscape can have multiple views per network model. This feature is not exposed to end-users, but"
			+ " you can access it from this API.",
			response = Count.class)
	public String getNetworkViewCount(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId) {
		return getNumberObjectString(JsonTags.COUNT, networkViewManager.getNetworkViews(getCyNetwork(networkId)).size());
	}


	@DELETE
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Delete all Network Views", notes="In general, a network has one view. But if there are multiple views, this deletes all of them.")
	public Response deleteAllNetworkViews(
			@ApiParam(value="Network SUID", required=true) @PathParam("networkId") Long networkId) {
		try {
			final Collection<CyNetworkView> views = this.networkViewManager.getNetworkViews(getCyNetwork(networkId));
			final Set<CyNetworkView> toBeDestroyed = new HashSet<CyNetworkView>(views);
			for (final CyNetworkView view : toBeDestroyed) {
				networkViewManager.destroyNetworkView(view);
			}

			return Response.ok().build();
		} catch (Exception e) {
			throw getError("Could not delete network views for network with SUID: " + networkId, e,
					Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("/first")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Convenience method to get the first view model.",
	notes="This returns the first view of the network.  As of Cytoscape 3.2.x, this is the only view accessible "
			+ "through the Cytoscape GUI.\n\nReturns Network view in JSON or location of the file")
	public Response getFirstNetworkView(
			@ApiParam(value="Network SUID" )@PathParam("networkId") Long networkId, 
			@ApiParam(value="If you want to get the view as a file, you can specify output file name with extension. For example, file=test.sif creates new SIF file in the current directory", required=false)@QueryParam("file") String file) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(networkId);
		if (views.isEmpty()) {
			throw new NotFoundException("Could not find view for the network: " + networkId);
		}
		final CyNetworkView view = views.iterator().next();
		return getNetworkView(networkId, view.getSUID(), file);
	}

	@DELETE
	@Path("/first")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Delete the first available view")
	public Response deleteFirstNetworkView(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(networkId);
		if (views.isEmpty() == false) {
			networkViewManager.destroyNetworkView(views.iterator().next());
		}

		return Response.ok().build();
	}


	@GET
	@Path("/{viewId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a network view (as JSON or a file)", notes="View in Cytoscape.js JSON. Currently, view information is (x, y) location only.  If you specify <strong>file</strong> option, this returns absolute path to the file. ")
	public Response getNetworkView(
			@ApiParam(value="Network SUID", required=true) @PathParam("networkId") Long networkId, 
			@ApiParam(value="Network View SUID", required=true) @PathParam("viewId") Long viewId,
			@ApiParam(value="Output File. If you want to get the view as a file, you can specify output file name with extension. <br>For example, file=test.sif creates new SIF file in the current directory", required=false) @QueryParam("file") String file) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(networkId);

		CyNetworkView targetView = null;
		for (final CyNetworkView view : views) {
			final Long vid = view.getSUID();
			if (vid.equals(viewId)) {
				targetView = view;
				break;
			}
		}

		if(targetView == null) {
			return Response.ok("{}").build();
		} else {
			if(file != null) {
				return Response.ok(writeNetworkFile(file, targetView)).build();
			} else {
				return Response.ok(getNetworkViewString(targetView)).build();
			}
		}
	}

	@GET
	@Path("/{viewId}.cx")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get a Network View in CX format")
	public Response getNetworkViewAsCx(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Network View SUID") @PathParam("viewId") Long viewId,
			@ApiParam(value="File (unused)") @QueryParam("file") String file) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(networkId);

		CyNetworkView targetView = null;
		for (final CyNetworkView view : views) {
			final Long vid = view.getSUID();
			if (vid.equals(viewId)) {
				targetView = view;
				break;
			}
		}

		if(targetView == null) {
			return Response.ok("{}").build();
		} else {

			final CyNetworkViewWriterFactory cxWriterFactory = 
					viewWriterFactoryManager.getFactory(CyNetworkViewWriterFactoryManager.CX_WRITER_ID);
			if(cxWriterFactory == null) {
				throw getError("CX writer is not supported.  Please install CX Support App to use this API.", 
						new RuntimeException(), Status.NOT_IMPLEMENTED);
			} else {
				return Response.ok(getNetworkViewStringAsCX(targetView)).build();
			}
		}
	}

	private final Map<String, String> writeNetworkFile(String file, CyNetworkView view) {
		File networkFile = null;
		try {
			networkFile = new File(file);
			TaskIterator itr = exportNetworkViewTaskFactory.createTaskIterator(view, networkFile);
			while(itr.hasNext()) {
				final Task task = itr.next();
				task.run(new HeadlessTaskMonitor());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw getError("Could not save network file.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}

		final Map<String, String> message = new HashMap<>();
		message.put("file", networkFile.getAbsolutePath());
		return message;
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get an array of all Network Views", notes="Returns an array of all network view SUIDs")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "An array of SUIDs", response=Long.class, responseContainer="List"),
	})
	public Collection<Long> getAllNetworkViews(
			@ApiParam(value="A Network SUID", required=true) @PathParam("networkId") Long networkId) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(networkId);

		final Collection<Long> suids = new HashSet<Long>();

		for (final CyNetworkView view : views) {
			final Long viewId = view.getSUID();
			suids.add(viewId);
		}
		return suids;
	}

	private final String getNetworkViewString(final CyNetworkView networkView) {
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		CyNetworkViewWriterFactory cytoscapeJsWriterFactory = (CyNetworkViewWriterFactory) this.cytoscapeJsWriterFactory.getService();
		CyWriter writer = cytoscapeJsWriterFactory.createWriter(stream, networkView);
		String jsonString = null;
		try {
			writer.run(null);
			jsonString = stream.toString("UTF-8");
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonString;
	}

	private final String getNetworkViewStringAsCX(final CyNetworkView networkView) {
		final CyNetworkViewWriterFactory cxWriterFactory = 
				viewWriterFactoryManager.getFactory(CyNetworkViewWriterFactoryManager.CX_WRITER_ID);

		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		CyWriter writer = cxWriterFactory.createWriter(stream, networkView);
		String jsonString = null;
		try {
			writer.run(null);
			jsonString = stream.toString("UTF-8");
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonString;
	}

	@GET
	@Path("/first.png")
	@Produces("image/png")
	@ApiOperation(value="Get PNG image of the first available network view", notes="Default size is 600 px")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "PNG image stream."),
	})
	public Response getFirstImageAsPng(
			@ApiParam(required=true, value="Network SUID") @PathParam("networkId") Long networkId,
			@ApiParam(required=false, value="Height of the image. Width is set automatically") @DefaultValue(DEF_HEIGHT) @QueryParam("h") int height
			) {
		return getImage("png", networkId, height);
	}

	@GET
	@Path("/first.svg")
	@Produces("image/svg+xml")
	@ApiOperation(value="Get SVG image of the first available network view", notes="Default size is 600 px")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "SVG image stream."),
	})
	public Response getFirstImageAsSvg(
			@ApiParam(required=true, value="Network SUID") @PathParam("networkId") Long networkId,
			@ApiParam(required=false, value="Height of the image. Width is set automatically") @DefaultValue(DEF_HEIGHT) @QueryParam("h") int height
			) {

		return getImage("svg", networkId, height);
	}

	@GET
	@Path("/first.pdf")
	@Produces("image/pdf")
	@ApiOperation(value="Get PDF image of the first available network view", notes="Default size is 600 px")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "PDF image stream."),
	})
	public Response getFirstImageAsPdf(
			@ApiParam(required=true, value="Network SUID") @PathParam("networkId") Long networkId,
			@ApiParam(required=false, value="Height of the image. Width is set automatically") @DefaultValue(DEF_HEIGHT) @QueryParam("h") int height
			) {
		return getImage("pdf", networkId, height);
	}

	private final Response getImage(String fileType, Long networkId, int height) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(networkId);
		if (views.isEmpty()) {
			throw getError("Could not create image.", new NotFoundException("Could not find view for the network: " + networkId),
					Response.Status.NOT_FOUND);
		}

		final PresentationWriterFactory factory = graphicsWriterManager.getFactory(fileType);
		final CyNetworkView view = views.iterator().next();

		return imageGenerator(fileType, factory, view, height, height);
	}

	@GET
	@Path("/{viewId}.png")
	@Produces("image/png")
	@ApiOperation(value="Get PNG image of a network view", notes="Generate a PNG network image as stream. Default size is 600 px.")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "PNG image stream."),
	})
	public Response getImageAsPng(
			@ApiParam(required=true, value="Network SUID") @PathParam("networkId") Long networkId,
			@ApiParam(required=true, value="Network View SUID") @PathParam("viewId") Long viewId,
			@ApiParam(required=false, value="Height of the image. Width is set automatically") @DefaultValue(DEF_HEIGHT) @QueryParam("h") int height
			) {
		return getImageForView("png", networkId, viewId, height);
	}

	@GET
	@Path("/{viewId}.svg")
	@Produces("image/svg+xml")
	@ApiOperation(value="Get SVG image of a network view", notes="Default size is 600 px")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "SVG image stream."),
	})
	public Response getImageAsSvg(
			@ApiParam(required=true, value="Network SUID") @PathParam("networkId") Long networkId,
			@ApiParam(required=true, value="Network View SUID") @PathParam("viewId") Long viewId,
			@ApiParam(required=false, value="Height of the image. Width is set automatically") @DefaultValue(DEF_HEIGHT) @QueryParam("h") int height
			) {
		return getImageForView("svg", networkId, viewId, height);
	}

	@GET
	@Path("/{viewId}.pdf")
	@Produces("image/pdf")
	@ApiOperation(value="Get PDF image of a network view", notes="Size is set to 500 px")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "PDF image stream."),
	})
	public Response getImageAsPdf(
			@ApiParam(required=true, value="Network SUID") @PathParam("networkId") Long networkId,
			@ApiParam(required=true, value="Network View SUID") @PathParam("viewId") Long viewId
			) {
		return getImageForView("pdf", networkId, viewId, 500);
	}

	private Response getImageForView(String fileType, Long networkId, Long viewId, Integer height) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(networkId);
		for (final CyNetworkView view : views) {
			final Long vid = view.getSUID();
			if (vid.equals(viewId)) {
				return getImage(fileType, networkId, height);
			}
		}

		throw new NotFoundException("Could not find view for the network: " + networkId);
	}

	private final Response imageGenerator(final String fileType, PresentationWriterFactory factory, 
			final CyNetworkView view, int width, int height) {
		final Collection<RenderingEngine<?>> re = renderingEngineManager.getRenderingEngines(view);
		if (re.isEmpty()) {
			throw new IllegalArgumentException("No rendering engine.");
		}
		try {
			RenderingEngine<?> engine = null;

			for (RenderingEngine<?> r : re) {
				if (r.getRendererId().equals(DING_ID)) {
					engine = r;
					break;
				}
			}
			if (engine == null) {
				throw new IllegalArgumentException("Could not find Ding rendering eigine.");
			}

			final ByteArrayOutputStream baos = new ByteArrayOutputStream();

			final CyWriter writer = factory.createWriter(baos, engine);

			if (fileType.equals("png")) {
				// FIXME !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				// This is a hack to set properties...
				// TODO: Are there cleaner way to access these props?

				final Object zl = writer.getClass().getMethod("getZoom").invoke(writer);
				final BoundedDouble bound = (BoundedDouble)zl;

				// Set large upper bound for generating large PNG.
				bound.setBounds(bound.getLowerBound(), 15000.0);

				try {
					// This is for 3.4 and below
					writer.getClass().getMethod("setHeightInPixels", int.class).invoke(writer, height);
				} catch(Exception e) {
					// For 3.5+					
					try {
						Object units = writer.getClass().getMethod("getUnits").invoke(writer);
						final Method method = units.getClass().getMethod("setSelectedValue", Object.class);
						method.invoke(units, "pixels");
						final Double doubleHeight = ((Integer)height).doubleValue();
						writer.getClass().getMethod("setHeight", Double.class).invoke(writer, doubleHeight);
					} catch(Exception e2) {
						e2.printStackTrace();
					}
				}
			}
			writer.run(new HeadlessTaskMonitor());

			final byte[] imageData = baos.toByteArray();
			return Response.ok(new ByteArrayInputStream(imageData)).build();
		} catch (Exception e) {
			e.printStackTrace();
			throw getError("Could not create image.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@POST
	@Path("/{viewId}/{objectType}/query")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Post Visual Properties for objects matching a query", notes="")
	public Response postViewsByQuery(
			@ApiParam(value="Network SUID", required=true) @PathParam("networkId") Long networkId,
			@ApiParam(value="Network View SUID", required=true) @PathParam("viewId") Long viewId,
			@ApiParam(value="Object Type", required=true, allowableValues="nodes,edges") @PathParam("objectType") String objectType, 
			@ApiParam(value="Bypass the Visual Style with these properties", defaultValue="false") @QueryParam("bypass") Boolean bypass,
			@ApiParam(value="Node table column name to be used for search", required=false) @QueryParam("column") String column,
			@ApiParam(value="Search query string", required=false) @QueryParam("query") String query,
			final InputStream is
			)
	{
		final CyNetworkView networkView = getView(networkId, viewId);

		final ObjectMapper objMapper = new ObjectMapper();

		if (bypass == null) {
			bypass = false;
		}

		Collection<Long> matchingObjects = getByQuery(networkId, "nodes", column, query);
		try {
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);

			for (long objectId : matchingObjects) {
				setVisualProperty(networkView, objectType, objectId, rootNode, bypass);
			}

		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(CIUtils.getCIErrorResponse(
							logger, logLocation, Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
							RESOURCE_URN, INVALID_PARAMETER_ERROR, 
							"Could not parse the input JSON for updating view. Reason: " + e.getMessage(), e))
					.build();	
		}
		return Response.ok().entity(new CIResponse<Object>(new Object())).build();
	}

	@DELETE
	@Path("/{viewId}/{objectType}/query")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Delete Visual Properties for objects matching a query", notes="")
	public Response deleteViewsByQuery(
			@ApiParam(value="Network SUID", required=true) @PathParam("networkId") Long networkId,
			@ApiParam(value="Network View SUID", required=true) @PathParam("viewId") Long viewId,
			@ApiParam(value="Object Type", required=true, allowableValues="nodes,edges") @PathParam("objectType") String objectType, 
			@ApiParam(value="Visual Property", required=true) @QueryParam("visualProperty") String visualProperty,
			@ApiParam(value="Bypass the Visual Style with these properties", defaultValue="true") @QueryParam("bypass") Boolean bypass,
			@ApiParam(value="Node table column name to be used for search", required=false) @QueryParam("column") String column,
			@ApiParam(value="Search query string", required=false) @QueryParam("query") String query
			)
	{
		CyNetworkView networkView = null;
		try {
			networkView = getView(networkId, viewId);
		} catch(NotFoundException e) {
			return Response.status(Status.NOT_FOUND)
					.entity(CIUtils.getCIErrorResponse(
							logger, logLocation, Status.NOT_FOUND.getStatusCode(), 
							RESOURCE_URN, COULD_NOT_FIND_RESOURCE_ERROR, 
							e.getMessage(), null))
					.build();
		}
		if (bypass == null) {
			bypass = false;
		}

		Collection<Long> matchingObjects = getByQuery(networkId, "nodes", column, query);
		Collection<VisualProperty<?>> vps = getVisualProperties(objectType);

		VisualProperty<?> targetVp = null;
		for(final VisualProperty<?> vp: vps) {
			if(vp.getIdString().equals(visualProperty)) { 
				targetVp = vp;
			}
		}
		
		if (targetVp == null) {
			return Response.status(Status.NOT_FOUND)
					.entity(CIUtils.getCIErrorResponse(
							logger, logLocation, Status.NOT_FOUND.getStatusCode(), 
							RESOURCE_URN, COULD_NOT_FIND_RESOURCE_ERROR, 
							"Could not find visual property: " + visualProperty, null))
					.build();	
		}
		
		for (long objectId : matchingObjects) {
			View<? extends CyIdentifiable> view = getObjectView(networkView, objectType, objectId);
			if (view.isValueLocked(targetVp) && bypass) {
			view.clearValueLock(targetVp); }
			//Not
		}
		
		return Response.ok().entity(new CIResponse<Object>(new Object())).build();
	}
	
	private void setVisualProperty(CyNetworkView networkView, String objectType, long objectId, JsonNode viewNode, boolean bypass) {

		if(viewNode == null) {
			return;
		}

		// This error throw is left over from a previous implementation, and persists to ensure backward compatibility. 
		if (objectType == null || (!objectType.equals("nodes") && !objectType.equals("edges"))) {
			throw getError("Method not supported.",
					new IllegalStateException(),
					Response.Status.INTERNAL_SERVER_ERROR);
		}

		View<? extends CyIdentifiable> view = getObjectView(networkView, objectType, objectId);

		if (view == null) {
			throw getError("Could not find view.",
					new IllegalArgumentException(),
					Response.Status.NOT_FOUND);
		}
		styleMapper.updateView(view, viewNode, getLexicon(), bypass);
	}

	@PUT
	@Path("/{viewId}/{objectType}")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Update node/edge view objects at once",
	notes="By passing list of key-value pair for each Visual Property, update node/edge view.\n\n"
			+ "The body should have the following JSON:\n"			
			+ "```\n"
			+ "[\n"
			+ "  {\n"
			+ "    \"SUID\": SUID of node/edge,\n"
			+ "    \"view\": [\n"
			+ "      {\n"
			+ "        \"visualProperty\": \"Visual Property Name, like NODE_FILL_COLOR\",\n"
			+ "        \"value\": \"Serialized form of value, like 'red.'\",\n"
			+ "      },\n"
			+ "      ...\n"
			+ "      {}\n"
			+ "    ]\n"
			+ "  },\n"
			+ "  ...\n"
			+ "  {}\n"
			+ "]\n"
			+ "```\n"
			+ "Note that if the Bypass parameter is not present or is false, the API will directly set the value to the view "
			+ "objects, and once a Visual Style applied, those values will be overridden by the Visual Style.\n")
	public Response updateViews(
			@ApiParam(value="Network SUID", required=true) @PathParam("networkId") Long networkId,
			@ApiParam(value="Network View SUID", required=true) @PathParam("viewId") Long viewId,
			@ApiParam(value="Object Type", required=true, allowableValues="nodes,edges") @PathParam("objectType") String objectType, 
			@ApiParam(value="Bypass the Visual Style with these properties", defaultValue="false") @QueryParam("bypass") Boolean bypass,
			final InputStream is
			) {

		final CyNetworkView networkView = getView(networkId, viewId);

		final ObjectMapper objMapper = new ObjectMapper();

		if (bypass == null) {
			bypass = false;
		}

		try {
			// This should be an JSON array.
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);

			for (JsonNode entry : rootNode) {
				final Long objectId = entry.get(CyIdentifiable.SUID).asLong();
				final JsonNode viewNode = entry.get("view");
				if(objectId == null) {
					continue;
				}
				setVisualProperty(networkView, objectType, objectId, viewNode, bypass);
			}

			// Repaint
			networkView.updateView();
		} catch (Exception e) {
			throw getError(
					"Could not parse the input JSON for updating view because: "
							+ e.getMessage(), e,
							Response.Status.INTERNAL_SERVER_ERROR);
		}
		return Response.ok().build();
	}

	@PUT
	@Path("/{viewId}/{objectType}/{objectId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Update single node/edge view object",
	notes="Update a single node/edge view.\n\n"
			+ "If the Bypass parameter is not present or is false, the API will directly set the "
			+ "value to the view objects, and once a Visual Style is applied, those values will be overridden "
			+ "by the Visual Style.\n"
			)
	@ApiImplicitParams(
			@ApiImplicitParam(value="Array of visualProperties", dataType="[Lorg.cytoscape.rest.internal.model.NetworkViewVisualProperty;", paramType="body", required=true)
			)
	public Response updateView(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Network View SUID") @PathParam("viewId") Long viewId,
			@ApiParam(value="Type of objects", allowableValues="nodes,edges,network") @PathParam("objectType") String objectType, 
			@ApiParam(value="node/edge SUID (NOT node/edge view SUID)") @PathParam("objectId") Long objectId,
			@ApiParam(value="Bypass the Visual Style with these properties", defaultValue="true") @QueryParam("bypass") Boolean bypass,
			@ApiParam(hidden=true) final InputStream is) {

		final CyNetworkView networkView = getView(networkId, viewId);

		View<? extends CyIdentifiable> view = getObjectView(networkView, objectType, objectId);
		/* This section is never reached due to /{viewId}/network/{visualProperty} existing as another endpoint.
		else if(objectType.equals("network")) {
			view = networkView;
		}*/

		if(view == null) {
			throw getError("Could not find view.", new IllegalArgumentException(), Response.Status.NOT_FOUND);
		}

		final ObjectMapper objMapper = new ObjectMapper();

		if (bypass == null) {
			bypass = false;
		}

		try {
			// This should be an JSON array.
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
			styleMapper.updateView(view, rootNode, getLexicon(), bypass);
		} catch (Exception e) {
			throw getError("Could not parse the input JSON for updating view because: " + e.getMessage(), e, Response.Status.INTERNAL_SERVER_ERROR);
		}
		// Repaint
		networkView.updateView();
		return Response.ok().build();
	}

	@PUT
	@Path("/{viewId}/network")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Update single network view value, such as background color or zoom level.",
	notes="By passing a list of key-value pairs for each Visual Property, update network view.\n\n"
			+ "The body should have the following JSON:\n"
			+ "```\n"
			+ "[\n"
			+ "  {\n"
			+ "    \"visualProperty\": \"Visual Property Name, like NETWORK_BACKGROUND_PAINT\",\n"
			+ "    \"value\": \"Serialized form of value, like 'red.'\"\n"
			+ "  },\n"
			+ "  ...\n"
			+ "  {}\n"
			+ "]\n"
			+ "```\n"
			+ "Note that if the Bypass parameter is not present or is false, the API will directly set the value to the view, "
			+ "and once a Visual Style is applied, that value will be overridden by the Visual Style.\n")
	public Response updateNetworkView(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Network View SUID") @PathParam("viewId") Long viewId,
			@ApiParam(value="Bypass the Visual Style with these properties", defaultValue="false") @QueryParam("bypass") Boolean bypass, 
			final InputStream is) {
		final CyNetworkView networkView = getView(networkId, viewId);
		final ObjectMapper objMapper = new ObjectMapper();

		if (bypass == null) {
			bypass = false;
		}

		try {
			// This should be an JSON array.
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
			styleMapper.updateView(networkView, rootNode, getLexicon(), bypass);
		} catch (Exception e) {
			throw getError("Could not parse the input JSON for updating view because: " + e.getMessage(), e, Response.Status.INTERNAL_SERVER_ERROR);
		}
		// Repaint
		networkView.updateView();

		return Response.ok().build();
	}


	@GET
	@Path("/{viewId}/network")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "List the Visual Properties for a Network View")
	public Response getNetworkVisualProps(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Network View SUID") @PathParam("viewId") Long viewId) {
		return this.getViews(networkId, viewId, "network", null);
	}

	@GET
	@Path("/{viewId}/{objectType}/{objectId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get view object for the specified type (node or edge)")
	public String getView(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Network View SUID") @PathParam("viewId") Long viewId,
			@ApiParam(value="Object Type", allowableValues="nodes,edges") @PathParam("objectType") String objectType, 
			@ApiParam(value="Object SUID")@PathParam("objectId") Long objectId) {
		final CyNetworkView networkView = getView(networkId, viewId);

		View<? extends CyIdentifiable> view = getObjectView(networkView, objectType, objectId);
		Collection<VisualProperty<?>> lexicon = getVisualProperties(objectType);

		if(view == null) {
			throw getError("Could not find view.", new IllegalArgumentException(), Response.Status.NOT_FOUND);
		}

		try {
			return styleSerializer.serializeView(view, lexicon);
		} catch (IOException e) {
			throw getError("Could not serialize the view object.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	private Collection<VisualProperty<?>> getVisualProperties(String objectType) {
		Collection<VisualProperty<?>> vps = null;

		if(nodeLexicon == null) {
			initLexicon();
		}

		if(objectType.equals("nodes")) {
			vps = nodeLexicon;
		} else if(objectType.equals("edges")) {
			vps = edgeLexicon;
		} else if(objectType.equals("network")) {
			vps = networkLexicon;
		}
		return vps;
	}

	private View<? extends CyIdentifiable> getObjectView(CyNetworkView networkView, String objectType, Long objectId) {
		View<? extends CyIdentifiable> view = null;

		if(objectType.equals("nodes")) {
			view = networkView.getNodeView(networkView.getModel().getNode(objectId));
		} else if(objectType.equals("edges")) {
			view = networkView.getEdgeView(networkView.getModel().getEdge(objectId));
		}
		return view;
	}

	@GET
	@Path("/{viewId}/{objectType}/{objectId}/{visualProperty}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a specific object visual property")
	public String getSingleVisualPropertyValue(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Network View SUID") @PathParam("viewId") Long viewId,
			@ApiParam(value="Object Type", allowableValues="nodes,edges")@PathParam("objectType") String objectType,
			@ApiParam(value="Object SUID") @PathParam("objectId") Long objectId, 
			@ApiParam(value="Unique name of a Visual Property")@PathParam("visualProperty") String visualProperty) {
		final CyNetworkView networkView = getView(networkId, viewId);

		if (objectType == null || (!objectType.equals("nodes") && !objectType.equals("edges"))) {
			throw getError("Object type " + objectType + " not recognized", new IllegalArgumentException(), Response.Status.NOT_FOUND);
		}

		Collection<VisualProperty<?>> vps = getVisualProperties(objectType);
		View<? extends CyIdentifiable> view = getObjectView(networkView, objectType, objectId);

		if(view == null) {
			throw getError("Could not find view.", new IllegalArgumentException(), Response.Status.NOT_FOUND);
		}

		return getSingleVp(visualProperty, view, vps);
	}

	private static class SingleVisualPropertyResponse extends CIResponse<NetworkViewVisualProperty> {};

	@GET
	@Path("/{viewId}/{objectType}/{objectId}/{visualProperty}/bypass")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a specific object visual property bypass",
	response=SingleVisualPropertyResponse.class
			)
	public Response getSingleVisualPropertyValueBypass(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Network View SUID") @PathParam("viewId") Long viewId,
			@ApiParam(value="Object Type", allowableValues="nodes,edges")@PathParam("objectType") String objectType,
			@ApiParam(value="Object SUID") @PathParam("objectId") Long objectId, 
			@ApiParam(value="Unique name of a Visual Property")@PathParam("visualProperty") String visualProperty) {

		CyNetworkView networkView = null;
		try {
			networkView = getView(networkId, viewId);
		} catch(NotFoundException e) {
			return Response.status(Status.NOT_FOUND)
					.entity(CIUtils.getCIErrorResponse(
							logger, logLocation, Status.NOT_FOUND.getStatusCode(), 
							RESOURCE_URN, COULD_NOT_FIND_RESOURCE_ERROR, 
							e.getMessage(), null))
					.build();
		}
		View<? extends CyIdentifiable> view = getObjectView(networkView, objectType, objectId);

		if (view == null) {
			return Response.status(Status.NOT_FOUND)
					.entity(CIUtils.getCIErrorResponse(
							logger, logLocation, Status.NOT_FOUND.getStatusCode(), 
							RESOURCE_URN, COULD_NOT_FIND_RESOURCE_ERROR, 
							"Could not find object view: " + objectId, null))
					.build();
		}

		if (objectType == null || (!objectType.equals("nodes") && !objectType.equals("edges"))) {
			return Response.status(Status.NOT_FOUND)
					.entity(CIUtils.getCIErrorResponse(
							logger, logLocation, Status.NOT_FOUND.getStatusCode(), 
							RESOURCE_URN, COULD_NOT_FIND_RESOURCE_ERROR, 
							"Object type not recognized: " + objectType, null))
					.build();
		}

		Collection<VisualProperty<?>> vps = getVisualProperties(objectType);

		VisualProperty<Object> targetVp = null;
		for(final VisualProperty<?> vp: vps) {
			if(vp.getIdString().equals(visualProperty)) {
				if (view.isDirectlyLocked(vp)) {
					targetVp = (VisualProperty<Object>) vp;
					break;
				} else {
					return Response.status(Status.NOT_FOUND)
							.entity(CIUtils.getCIErrorResponse(
									logger, logLocation, Status.NOT_FOUND.getStatusCode(), 
									RESOURCE_URN, COULD_NOT_FIND_RESOURCE_ERROR, 
									"Could not find bypass visual property: " + visualProperty, null))
							.build();	
				}
			}
		}

		NetworkViewVisualProperty entity = new NetworkViewVisualProperty();
		entity.visualProperty = targetVp.getIdString();
		Object value = view.getVisualProperty(targetVp);
		if  (targetVp.getRange().getType().equals(Boolean.class)
				|| targetVp.getRange().getType().equals(String.class)
				||	Number.class.isAssignableFrom(targetVp.getRange().getType())) {
			entity.value = value;
		} else {
			entity.value = targetVp.toSerializableString(value);
		}
		return Response.ok(new CIResponse<NetworkViewVisualProperty>(entity)).build();

	}

	@PUT
	@Path("/{viewId}/{objectType}/{objectId}/{visualProperty}/bypass")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Set a specific object visual property bypass")
	public Response putSingleVisualPropertyValueBypass(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Network View SUID") @PathParam("viewId") Long viewId,
			@ApiParam(value="Object Type", allowableValues="nodes,edges")@PathParam("objectType") String objectType,
			@ApiParam(value="Object SUID")@PathParam("objectId") Long objectId, 
			@ApiParam(value="Unique name of a Visual Property")@PathParam("visualProperty") String visualProperty,
			final InputStream inputStream) {

		CyNetworkView networkView = null;
		try {
			networkView = getView(networkId, viewId);
		} catch(NotFoundException e) {
			return Response.status(Status.NOT_FOUND)
					.entity(CIUtils.getCIErrorResponse(
							logger, logLocation, Status.NOT_FOUND.getStatusCode(), 
							RESOURCE_URN, COULD_NOT_FIND_RESOURCE_ERROR, 
							e.getMessage(), null))
					.build();
		}
		View<? extends CyIdentifiable> view = getObjectView(networkView, objectType, objectId);

		if (view == null) {
			return Response.status(Status.NOT_FOUND)
					.entity(CIUtils.getCIErrorResponse(
							logger, logLocation, Status.NOT_FOUND.getStatusCode(), 
							RESOURCE_URN, COULD_NOT_FIND_RESOURCE_ERROR, 
							"Could not find object view: " + objectId, null))
					.build();
		}
		Collection<VisualProperty<?>> vps = getVisualProperties(objectType);

		VisualProperty<?> targetVp = null;
		for(final VisualProperty<?> vp: vps) {
			if(vp.getIdString().equals(visualProperty)) {
				targetVp = vp;
				break;
			}
		}
		if(targetVp == null) {
			return Response.status(Status.NOT_FOUND)
					.entity(CIUtils.getCIErrorResponse(
							logger, logLocation, Status.NOT_FOUND.getStatusCode(), 
							RESOURCE_URN, COULD_NOT_FIND_RESOURCE_ERROR, 
							"Could not find bypass visual property: " + visualProperty, null))
					.build();	
		}

		try {
			final ObjectMapper objMapper = new ObjectMapper();
			final JsonNode rootNode = objMapper.readValue(inputStream, JsonNode.class);
			styleMapper.updateViewVisualProperty(view, rootNode, getLexicon(), true);
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(CIUtils.getCIErrorResponse(
							logger, logLocation, Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
							RESOURCE_URN, INVALID_PARAMETER_ERROR, 
							"Could not parse the input JSON for updating view. Reason: " + e.getMessage(), e))
					.build();	
		}
		return Response.ok().entity(new CIResponse<Object>(new Object())).build();
	}

	@DELETE
	@Path("/{viewId}/{objectType}/{objectId}/{visualProperty}/bypass")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Delete a specific object visual property bypass",
	notes="Deleting a bypass property will turn off this objects style bypass and delete any associated property"
			+ "data (color, size, etc.).",
			response=CIResponse.class
			)
	public Response deleteSingleVisualPropertyValueBypass(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Network View SUID") @PathParam("viewId") Long viewId,
			@ApiParam(value="Object Type", allowableValues="nodes,edges")@PathParam("objectType") String objectType,
			@ApiParam(value="Object SUID")@PathParam("objectId") Long objectId, 
			@ApiParam(value="Unique name of a Visual Property")@PathParam("visualProperty") String visualProperty) {

		CyNetworkView networkView = null;
		try {
			networkView = getView(networkId, viewId);
		} catch(NotFoundException e) {
			return Response.status(Status.NOT_FOUND)
					.entity(CIUtils.getCIErrorResponse(
							logger, logLocation, Status.NOT_FOUND.getStatusCode(), 
							RESOURCE_URN, COULD_NOT_FIND_RESOURCE_ERROR, 
							e.getMessage(), null))
					.build();
		}
		View<? extends CyIdentifiable> view = getObjectView(networkView, objectType, objectId);

		if (view == null) {
			return Response.status(Status.NOT_FOUND)
					.entity(CIUtils.getCIErrorResponse(
							logger, logLocation, Status.NOT_FOUND.getStatusCode(), 
							RESOURCE_URN, COULD_NOT_FIND_RESOURCE_ERROR, 
							"Could not find object view: " + objectId, null))
					.build();
		}

		Collection<VisualProperty<?>> vps = getVisualProperties(objectType);

		VisualProperty<?> targetVp = null;
		for(final VisualProperty<?> vp: vps) {
			if(vp.getIdString().equals(visualProperty)) {
				if (view.isDirectlyLocked(vp)) {
					targetVp = vp;
					break;
				} else {
					return Response.status(Status.NOT_FOUND)
							.entity(CIUtils.getCIErrorResponse(
									logger, logLocation, Status.NOT_FOUND.getStatusCode(), 
									RESOURCE_URN, COULD_NOT_FIND_RESOURCE_ERROR, 
									"Could not find bypass visual property: " + visualProperty, null))
							.build();
				}
			}
		}
		if(targetVp == null) {
			return Response.status(Status.NOT_FOUND)
					.entity(CIUtils.getCIErrorResponse(
							logger, logLocation, Status.NOT_FOUND.getStatusCode(), 
							RESOURCE_URN, COULD_NOT_FIND_RESOURCE_ERROR, 
							"Could not find bypass visual property: " + visualProperty, null))
					.build();		
		}

		view.clearValueLock(targetVp);

		return Response.ok().entity(new CIResponse<Object>(new Object())).build();
	}


	@GET
	@Path("/{viewId}/network/{visualProperty}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a specific network visual property")
	public String getNetworkVisualProp(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Network View SUID") @PathParam("viewId") Long viewId,
			@ApiParam(value="Unique name of a Visual Property") @PathParam("visualProperty") String visualProperty) {
		final CyNetworkView networkView = getView(networkId, viewId);

		if(nodeLexicon == null) {
			initLexicon();
		}
		return getSingleVp(visualProperty, networkView, networkLexicon);
	}

	private String getSingleVp(final String visualProperty, final View<? extends CyIdentifiable> view, 
			final Collection<VisualProperty<?>> vps) {
		VisualProperty<?> targetVp = null;
		for(final VisualProperty<?> vp: vps) {
			if(vp.getIdString().equals(visualProperty)) {
				targetVp = vp;
				break;
			}
		}
		if(targetVp == null) {
			throw getError("Could not find such Visual Property: " + visualProperty , new NotFoundException(), Response.Status.NOT_FOUND);
		}

		try {
			return styleSerializer.serializeSingleVisualProp(view, targetVp);
		} catch (IOException e) {
			throw getError("Could not serialize the view object.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("/{viewId}/{objectType}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get current values for a specific Visual Property")
	public Response getViews(
			@ApiParam(value="Network SUID")@PathParam("networkId") Long networkId, 
			@ApiParam(value="Network View SUID") @PathParam("viewId") Long viewId,
			@ApiParam(value="Object Type", allowableValues="nodes,edges,network") @PathParam("objectType") String objectType, 
			@ApiParam(value="Unique name of a Visual Property") @QueryParam("visualProperty") String visualProperty) {

		if(visualProperty != null) {
			final String result = getSingleVisualPropertyOfViews(networkId, viewId, objectType, visualProperty);
			return Response.ok(result).build();
		}

		Collection<VisualProperty<?>> vps = getVisualProperties(objectType);

		return Response.ok(getViewForVPList(networkId, viewId, objectType, vps)).build();
	}

	private final String getViewForVPList(final Long networkId, final Long viewId, final String objectType, Collection<VisualProperty<?>> vps) {
		final CyNetworkView networkView = getView(networkId, viewId);
		Collection<? extends View<? extends CyIdentifiable>> graphObjects = null;

		if(objectType.equals("nodes")) {
			graphObjects = networkView.getNodeViews();
		} else if(objectType.equals("edges")) {
			graphObjects = networkView.getEdgeViews();
		} else if(objectType.equals("network")) {
			try {
				return styleSerializer.serializeView(networkView, vps);
			} catch (IOException e) {
				throw getError("Could not serialize the view object.", e, Response.Status.INTERNAL_SERVER_ERROR);
			}
		} 

		if(graphObjects == null || graphObjects.isEmpty()) {
			throw getError("Could not find views.", new IllegalArgumentException(), Response.Status.NOT_FOUND);
		}
		try {
			return styleSerializer.serializeViews(graphObjects, vps);
		} catch (IOException e) {
			throw getError("Could not serialize the view object.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}	


	private final String getSingleVisualPropertyOfViews(Long networkId, Long viewId,
			String objectType, String visualPropertyName) {
		if(nodeLexicon == null) {
			initLexicon();
		}

		final Collection<VisualProperty<?>> vps = new HashSet<>();
		VisualProperty<?> vp = null;

		if(objectType.equals("nodes")) {
			vp = lexicon.lookup(CyNode.class, visualPropertyName);
		} else if(objectType.equals("edges")) {
			vp = lexicon.lookup(CyEdge.class, visualPropertyName);
		}

		if(vp == null) {
			throw getError("Visual Property does not exist: " + visualPropertyName, new NotFoundException(), Response.Status.NOT_FOUND);
		}

		vps.add(vp);
		return getViewForVPList(networkId, viewId, objectType, vps);

	}

	private final CyNetworkView getView(Long networkId, Long viewId) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(networkId);
		for (final CyNetworkView view : views) {
			final Long vid = view.getSUID();
			if (vid.equals(viewId)) {
				return view;
			}
		}
		throw new NotFoundException("Could not find view: " + viewId);
	}
}