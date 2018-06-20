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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.cytoscape.ci.model.CIResponse;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.write.PresentationWriterFactory;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.rest.internal.CyNetworkViewWriterFactoryManager;
import org.cytoscape.rest.internal.CyRESTConstants;
import org.cytoscape.rest.internal.GraphicsWriterManager;
import org.cytoscape.rest.internal.datamapper.VisualStyleMapper;
import org.cytoscape.rest.internal.model.CountModel;
import org.cytoscape.rest.internal.model.ModelConstants;
import org.cytoscape.rest.internal.model.NetworkViewSUIDModel;
import org.cytoscape.rest.internal.model.ObjectVisualPropertyValueModel;
import org.cytoscape.rest.internal.model.VisualPropertyValueModel;
import org.cytoscape.rest.internal.serializer.VisualStyleSerializer;
import org.cytoscape.rest.internal.task.HeadlessTaskMonitor;
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

	static final String RESOURCE_URN = "networks:views";

	@Override
	public String getResourceURI() {
		return RESOURCE_URN;
	}
	
	private final static Logger logger = LoggerFactory.getLogger(NetworkViewResource.class);
		
	@Override
	public Logger getResourceLogger() {
		return logger;
	}
	
	static final int NOT_FOUND_ERROR = 1;
	static final int INVALID_PARAMETER_ERROR = 2;
	static final int NO_VIEWS_FOR_NETWORK_ERROR = 3;
	static final int NO_VISUAL_LEXICON_ERROR = 4;
	static final int SERIALIZATION_ERROR = 5;
	public static final int INTERNAL_METHOD_ERROR = 6;
	public static final int CX_SERVICE_UNAVAILABLE_ERROR = 7;
	public static final int VISUAL_PROPERTY_DOES_NOT_EXIST_ERROR = 8;
	
	private static final String FIRST_VIEWS_NOTE = "Cytoscape can have multiple views per network model, but this feature is not exposed in the Cytoscape GUI. GUI access is limited to the first available view only.";
	
	private static final String VIEW_FILE_PARAMETER_NOTES = "The format of the file written is defined by the file extension.\n\n"  
			+ "| Extension   | Details    |\n"
			+ "| ----------- | -----------|\n"
			+ "| .cys        | Cytoscape Style format |\n"
			+ "| .xml/.xgmml | [XGMML]("+CyRESTConstants.XGMML_FILE_FORMAT_LINK+") format |\n"
			+ "| .nnf        | [NNF]("+CyRESTConstants.NNF_FILE_FORMAT_LINK+") format |\n"
			+ "| .sif        | [SIF]("+CyRESTConstants.SIF_FILE_FORMAT_LINK+") format |\n"
			+ "| .cyjs       | [Cytoscape.js]("+CyRESTConstants.CYTOSCAPE_JS_FILE_FORMAT_LINK+") format |\n";
	
	private static final String VIEW_FILE_OPERATION_NOTES = "If the `file` parameter is left unspecified, the response will contain data in [Cytoscape.js]("+ CyRESTConstants.CYTOSCAPE_JS_FILE_FORMAT_LINK +") format.\n\n"
			+ "If the `file` parameter is specified, the Network View will be written to a file, and the response will contain the location of the file in the following format:\n\n"
			+ "```\n\n"
			+ "{\n" 
			+ "  \"file\": \"/media/HD1/myFiles/networkView.sif\"\n" 
			+ "}\n"
			+ "```\n\n"
			+ "The format of the output file is defined by the extension of the `file` parameter.";
	
	private static final String BYPASS_NOTES = "Note that this sets the Visual Properties temporarily unless the `bypass` parameter is set to `true`. If the `bypass` parameter is set to `true`, the Visual Style will be overridden by these Visual Property values. If the `bypass` parameter is not used or is set to `false`, any Visual Properties set will return "
			+ "to those defined in the Visual Style if the Network View is updated.\n";
	
	@Inject
	@NotNull
	private RenderingEngineManager renderingEngineManager;

	@Inject
	@NotNull
	private GraphicsWriterManager graphicsWriterManager;

	@Inject
	@NotNull
	private ExportNetworkViewTaskFactory exportNetworkViewTaskFactory;

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
		this.lexicon = getLexicon(NO_VISUAL_LEXICON_ERROR);
		nodeLexicon = lexicon.getAllDescendants(BasicVisualLexicon.NODE);
		edgeLexicon = lexicon.getAllDescendants(BasicVisualLexicon.EDGE);
		networkLexicon = lexicon.getAllDescendants(BasicVisualLexicon.NETWORK).stream()
				.filter(vp->vp.getIdString().startsWith("NETWORK")).collect(Collectors.toSet());;

	}

	@POST

	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Create a new Network View",
			notes="Creates a new Network View for the Network specified by the `networkId` parameter.")
	@ApiResponses(value = { 
			@ApiResponse(code = 201, message = "Network View SUID", response = NetworkViewSUIDModel.class),
	})
	public Response createNetworkView(@ApiParam(value="SUID of the Network", required=true) @PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(NOT_FOUND_ERROR, networkId);
		final CyNetworkView view = networkViewFactory.createNetworkView(network);
		networkViewManager.addNetworkView(view);
		return Response.status(Response.Status.CREATED).entity(new NetworkViewSUIDModel(view.getSUID())).build();
	}

	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get number of views for the given network model",
	notes = "Returns a count of the Network Views available for the Network specified by the `networkId` parameter.\n\n" + FIRST_VIEWS_NOTE,
			response = CountModel.class)
	public String getNetworkViewCount(
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId) {
		return getNumberObjectString(SERIALIZATION_ERROR, JsonTags.COUNT, networkViewManager.getNetworkViews(getCyNetwork(NOT_FOUND_ERROR, networkId)).size());
	}


	@DELETE
	
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Delete all Network Views", notes="Deletes all Network Views available in the Network specified by the `networkId` parameter. " + FIRST_VIEWS_NOTE + "\n\n")
	public Response deleteAllNetworkViews(
			@ApiParam(value="SUID of the Network", required=true) @PathParam("networkId") Long networkId) {
		try {
			final Collection<CyNetworkView> views = this.networkViewManager.getNetworkViews(getCyNetwork(NOT_FOUND_ERROR, networkId));
			final Set<CyNetworkView> toBeDestroyed = new HashSet<CyNetworkView>(views);
			for (final CyNetworkView view : toBeDestroyed) {
				networkViewManager.destroyNetworkView(view);
			}

			return Response.ok().build();
		} catch (Exception e) {
			//throw getError("Could not delete network views for network with SUID: " + networkId, e,
			//		Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					INTERNAL_METHOD_ERROR, 
					"Could not delete network views for network with SUID: " + networkId, 
					getResourceLogger(), e);
		}
	}

	@GET
	@Path("/first")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get the first Network View (as JSON or a file)",
	notes="This returns the first view of the network. " + FIRST_VIEWS_NOTE + "\n\n"
			+ VIEW_FILE_OPERATION_NOTES)
	public Response getFirstNetworkView(
			@ApiParam(value="SUID of the Network" )@PathParam("networkId") Long networkId, 
			@ApiParam(value="A path to a file relative to the current directory. " + VIEW_FILE_PARAMETER_NOTES, required=false)@QueryParam("file") String file) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(NOT_FOUND_ERROR, NO_VIEWS_FOR_NETWORK_ERROR, networkId);
		if (views.isEmpty()) {
			//throw new NotFoundException("Could not find view for the network: " + networkId);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NOT_FOUND_ERROR, 
					"Could not find view for the network: " + networkId, 
					logger, null);
		}
		final CyNetworkView view = views.iterator().next();
		return getNetworkView(networkId, view.getSUID(), file);
	}

	@DELETE
	@Path("/first")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Delete the first available view",
			notes="Deletes the first available Network View for the Network specified by the `networkId` parameter. "  + FIRST_VIEWS_NOTE)
	public Response deleteFirstNetworkView(
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(NOT_FOUND_ERROR, NO_VIEWS_FOR_NETWORK_ERROR, networkId);
		if (views.isEmpty() == false) {
			networkViewManager.destroyNetworkView(views.iterator().next());
		}

		return Response.ok().build();
	}


	@GET
	@Path("/{viewId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a Network View (as JSON or a file)", 
			notes="Gets the Network View specified by the `viewId` and `networkId` parameters.\n\n"
					+ VIEW_FILE_OPERATION_NOTES
			)
	public Response getNetworkView(
			@ApiParam(value="SUID of the Network", required=true) @PathParam("networkId") Long networkId, 
			@ApiParam(value="SUID of the Network View", required=true) @PathParam("viewId") Long viewId,
			@ApiParam(value="A path to a file relative to the current directory. " + VIEW_FILE_PARAMETER_NOTES, required=false) @QueryParam("file") String file) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(NOT_FOUND_ERROR, NO_VIEWS_FOR_NETWORK_ERROR, networkId);

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
	@ApiOperation(value = "Get a Network View in CX format", 
		notes="Returns the Network View specified by the `viewId` and `networkId` parameters "
				+ "in [CX format]("+CyRESTConstants.CX_FILE_FORMAT_LINK+")")
	public Response getNetworkViewAsCx(
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId, 
			@ApiParam(value="SUID of the Network View") @PathParam("viewId") Long viewId,
			@ApiParam(hidden=true, value="File (unused)") @QueryParam("file") String file) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(NOT_FOUND_ERROR, NO_VIEWS_FOR_NETWORK_ERROR, networkId);

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
				//throw getError("CX writer is not supported.  Please install CX Support App to use this API.", 
				//		new RuntimeException(), Status.NOT_IMPLEMENTED);
				throw this.getCIWebApplicationException(Status.SERVICE_UNAVAILABLE.getStatusCode(), 
						getResourceURI(), 
						CX_SERVICE_UNAVAILABLE_ERROR, 
						"CX writer is not available.  Please install CX Support App to use this API.", 
						getResourceLogger(), null);
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
			//throw getError("Could not save network file.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INTERNAL_METHOD_ERROR, 
					"Could not save network file.", 
					logger, e);
		}

		final Map<String, String> message = new HashMap<>();
		message.put("file", networkFile.getAbsolutePath());
		return message;
	}
	
	@GET
	
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get all Network Views", notes="Returns an array of all network views belonging to the network specified by the `networkId` paramter. The response is a list of Network SUIDs.")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "An array of Network View SUIDs", response=Long.class, responseContainer="List"),
	})
	public Collection<Long> getAllNetworkViews(
			@ApiParam(value="SUID of the Network", required=true) @PathParam("networkId") Long networkId) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(NOT_FOUND_ERROR, NO_VIEWS_FOR_NETWORK_ERROR, networkId);
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
	@ApiOperation(value="Get PNG image of the first available network view", notes="Returns a PNG image of the first available Network View for the Network specified by the `networkId` parameter.\n\nDefault size is 600 px")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "PNG image stream."),
	})
	public Response getFirstImageAsPng(
			@ApiParam(required=true, value="SUID of the Network") @PathParam("networkId") Long networkId,
			@ApiParam(required=false, value="Height of the image. Width is set automatically") @DefaultValue(DEF_HEIGHT) @QueryParam("h") int height
			) {
		return getImage("png", networkId, height);
	}

	@GET
	@Path("/first.svg")
	@Produces("image/svg+xml")
	@ApiOperation(value="Get SVG image of the first available network view", notes="Returns an SVG image of the first available Network View for the Network specified by the `networkId` parameter.\n\nDefault size is 600 px")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "SVG image stream."),
	})
	public Response getFirstImageAsSvg(
			@ApiParam(required=true, value="SUID of the Network") @PathParam("networkId") Long networkId,
			@ApiParam(required=false, value="Height of the image. Width is set automatically") @DefaultValue(DEF_HEIGHT) @QueryParam("h") int height
			) {

		return getImage("svg", networkId, height);
	}

	@GET
	@Path("/first.pdf")
	@Produces("image/pdf")
	@ApiOperation(value="Get PDF image of the first available network view", notes="Returns a PDF of the first available Network View for the Network specified by the `networkId` parameter.\n\nDefault size is 600 px")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "PDF image stream."),
	})
	public Response getFirstImageAsPdf(
			@ApiParam(required=true, value="SUID of the Network") @PathParam("networkId") Long networkId,
			@ApiParam(required=false, value="Height of the image. Width is set automatically") @DefaultValue(DEF_HEIGHT) @QueryParam("h") int height
			) {
		return getImage("pdf", networkId, height);
	}

	private final Response getImage(String fileType, Long networkId, int height) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(NOT_FOUND_ERROR, NO_VIEWS_FOR_NETWORK_ERROR, networkId);
		if (views.isEmpty()) {
			//throw getError("Could not create image.", new NotFoundException("Could not find view for the network: " + networkId),
			//		Response.Status.NOT_FOUND);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					NO_VIEWS_FOR_NETWORK_ERROR, 
					"Could not create image. No views available for network with SUID: " + networkId, 
					getResourceLogger(), null);
		}

		final PresentationWriterFactory factory = graphicsWriterManager.getFactory(fileType);
		final CyNetworkView view = views.iterator().next();

		return imageGenerator(fileType, factory, view, height, height);
	}

	@GET
	@Path("/{viewId}.png")
	@Produces("image/png")
	@ApiOperation(value="Get PNG image of a network view", notes="Returns a PNG image of the Network View specified by the `viewId` and `networkId` parameters.\n\nDefault size is 600 px.")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "PNG image stream."),
	})
	public Response getImageAsPng(
			@ApiParam(required=true, value="SUID of the Network") @PathParam("networkId") Long networkId,
			@ApiParam(required=true, value="SUID of the Network View") @PathParam("viewId") Long viewId,
			@ApiParam(required=false, value="Height of the image. Width is set automatically") @DefaultValue(DEF_HEIGHT) @QueryParam("h") int height
			) {
		return getImageForView("png", networkId, viewId, height);
	}

	@GET
	@Path("/{viewId}.svg")
	@Produces("image/svg+xml")
	@ApiOperation(value="Get SVG image of a network view",  notes="Returns an SVG image of the Network View specified by the `viewId` and `networkId` parameters.\n\nDefault size is 600 px.")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "SVG image stream."),
	})
	public Response getImageAsSvg(
			@ApiParam(required=true, value="SUID of the Network") @PathParam("networkId") Long networkId,
			@ApiParam(required=true, value="SUID of the Network View") @PathParam("viewId") Long viewId,
			@ApiParam(required=false, value="Height of the image. Width is set automatically") @DefaultValue(DEF_HEIGHT) @QueryParam("h") int height
			) {
		return getImageForView("svg", networkId, viewId, height);
	}

	@GET
	@Path("/{viewId}.pdf")
	@Produces("image/pdf")
	@ApiOperation(value="Get PDF image of a network view", notes="Returns a PDF of the Network View specified by the `viewId` and `networkId` parameters.\n\nDefault size is 500 px.")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "PDF image stream."),
	})
	public Response getImageAsPdf(
			@ApiParam(required=true, value="SUID of the Network") @PathParam("networkId") Long networkId,
			@ApiParam(required=true, value="SUID of the Network View") @PathParam("viewId") Long viewId
			) {
		return getImageForView("pdf", networkId, viewId, 500);
	}

	private Response getImageForView(String fileType, Long networkId, Long viewId, Integer height) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(NOT_FOUND_ERROR, NO_VIEWS_FOR_NETWORK_ERROR, networkId);
		for (final CyNetworkView view : views) {
			final Long vid = view.getSUID();
			if (vid.equals(viewId)) {
				return getImage(fileType, networkId, height);
			}
		}

		//throw new NotFoundException("Could not find view for the network: " + networkId);
		throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
				RESOURCE_URN, 
				NOT_FOUND_ERROR, 
				"Could not find view for the network: " + networkId, 
				logger, null);
	}

	private final Response imageGenerator(final String fileType, PresentationWriterFactory factory, 
			final CyNetworkView view, int width, int height) {
		Collection<RenderingEngine<?>> re = renderingEngineManager.getRenderingEngines(view);
		final int maxRetries = 20;
		final int sleepTime = 100;
		try {
			for (int retry = 0; re.isEmpty() && retry < maxRetries; retry++) {
				Thread.sleep(sleepTime);
				re = renderingEngineManager.getRenderingEngines(view);
			}
		} catch (InterruptedException e) {	
			//throw getError("Image generation interrupted", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					INTERNAL_METHOD_ERROR, 
					"Image generation interrupted", 
					getResourceLogger(), e);
		}
		if (re.isEmpty()) {
			throw new IllegalArgumentException("No rendering engine for {\"network\":" + view.getModel().getSUID() +", \"view\":" + view.getSUID() + "}");
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
				//throw getError("No DING rendering engine available", new Exception(), Response.Status.INTERNAL_SERVER_ERROR);
				throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						getResourceURI(), 
						INTERNAL_METHOD_ERROR, 
						"No DING rendering engine available", 
						getResourceLogger(), null);
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
			//throw getError("Could not create image.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					INTERNAL_METHOD_ERROR, 
					"Could not create image.", 
					getResourceLogger(), e);
		}
	}
	
	private void setVisualProperty(CyNetworkView networkView, String objectType, long objectId, JsonNode viewNode, boolean bypass) {

		if(viewNode == null) {
			return;
		}

		// This error throw is left over from a previous implementation, and persists to ensure backward compatibility. 
		if (objectType == null || (!objectType.equals("nodes") && !objectType.equals("edges"))) {
			//throw getError("Method not supported.",
			//		new IllegalStateException(),
			//		Response.Status.INTERNAL_SERVER_ERROR);
			 throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(),  
			          RESOURCE_URN,  
			          NOT_FOUND_ERROR,  
			          "Object type not recognized:" + objectType,  
			          logger, null); 
		}

		View<? extends CyIdentifiable> view = getObjectView(networkView, objectType, objectId);

		if (view == null) {
			//throw getError("Could not find view.",
			//		new IllegalArgumentException(),
			//		Response.Status.NOT_FOUND);
			 throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(),  
			          RESOURCE_URN,  
			          NOT_FOUND_ERROR,  
			          "Could not find view.",  
			          logger, null); 
		}
		styleMapper.updateView(view, viewNode, getLexicon(NO_VISUAL_LEXICON_ERROR), bypass);
	}

	@PUT
	@Path("/{viewId}/{objectType}")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Update multiple node/edge Visual Properties at once",
	notes="Updates multiple node or edge Visual Properties as defined by the `objectType` "
			+ "parameter, in the Network View specified by the `viewId` and `networkId` "
			+ "parameters.\n\nExamples of Visual Properties:\n\n" 
			+ ModelConstants.NODE_VISUAL_PROPERTY_EXAMPLES + "\n" 
			+ ModelConstants.EDGE_VISUAL_PROPERTY_EXAMPLES + "\n\n"
			+ ModelConstants.VISUAL_PROPERTIES_REFERENCES +"\n\n"
			 + BYPASS_NOTES
			)
	@ApiImplicitParams(
			@ApiImplicitParam(value="A list of Objects with Visual Properties.", 
				dataType="[Lorg.cytoscape.rest.internal.model.ObjectVisualPropertyValueModel;", paramType="body", required=true)
			)
	public Response updateViews(
			@ApiParam(value="SUID of the Network", required=true) @PathParam("networkId") Long networkId,
			@ApiParam(value="SUID of the Network View", required=true) @PathParam("viewId") Long viewId,
			@ApiParam(value="Type of Object", required=true, allowableValues="nodes,edges") @PathParam("objectType") String objectType, 
			@ApiParam(value="Bypass the Visual Style with these Visual Properties", defaultValue="false") @QueryParam("bypass") Boolean bypass,
			@ApiParam(hidden=true) final InputStream is
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
			//throw getError(
			//		"Could not parse the input JSON for updating view because: "
			//				+ e.getMessage(), e,
			//				Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INVALID_PARAMETER_ERROR, 
					"Could not parse the input JSON for updating view because: " + e.getMessage(), 
					logger, e);
		}
		return Response.ok().build();
	}

	@PUT
	@Path("/{viewId}/{objectType}/{objectId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Update the Visual Properties of an Object",
	notes="Updates the Visual Properties in the object specified by the `objectId` and "
			+ "`objectType` parameters in the Network View specified by the `viewId` and "
			+ "`networkId` parameters.\n\nExamples of Visual Properties:\n\n" 
			+ ModelConstants.NODE_VISUAL_PROPERTY_EXAMPLE + "\n" 
			+ ModelConstants.EDGE_VISUAL_PROPERTY_EXAMPLE + "\n" 
			+ ModelConstants.NETWORK_VISUAL_PROPERTY_EXAMPLE + "\n\n"
			+ ModelConstants.VISUAL_PROPERTIES_REFERENCES +"\n\n"
			+ BYPASS_NOTES
		)
	@ApiImplicitParams(
			@ApiImplicitParam(value="A list of Visual Properties and their values.", dataType="[Lorg.cytoscape.rest.internal.model.VisualPropertyValueModel;", paramType="body", required=true)
		)
	public Response updateView(
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId, 
			@ApiParam(value="SUID of the Network View") @PathParam("viewId") Long viewId,
			@ApiParam(value="Type of Object", allowableValues="nodes,edges,network") @PathParam("objectType") String objectType, 
			@ApiParam(value="SUID of the Object") @PathParam("objectId") Long objectId,
			@ApiParam(value="Bypass the Visual Style with these Visual Properties", defaultValue="true") @QueryParam("bypass") Boolean bypass,
			@ApiParam(hidden=true) final InputStream is) {

		final CyNetworkView networkView = getView(networkId, viewId);

		View<? extends CyIdentifiable> view = getObjectView(networkView, objectType, objectId);
		/* This section is never reached due to /{viewId}/network/{visualProperty} existing as another endpoint.
		else if(objectType.equals("network")) {
			view = networkView;
		}*/

		if(view == null) {
			//throw getError("Could not find view.", new IllegalArgumentException(), Response.Status.NOT_FOUND);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NOT_FOUND_ERROR, 
					"Could not find view.", 
					logger, null);
		}

		final ObjectMapper objMapper = new ObjectMapper();

		if (bypass == null) {
			bypass = false;
		}

		try {
			// This should be an JSON array.
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
			styleMapper.updateView(view, rootNode, getLexicon(NO_VISUAL_LEXICON_ERROR), bypass);
		} catch (Exception e) {
			//throw getError("Could not parse the input JSON for updating view because: " + e.getMessage(), e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INVALID_PARAMETER_ERROR, 
					"Could not parse the input JSON for updating view because: " + e.getMessage(), 
					logger, e);
		}
		// Repaint
		networkView.updateView();
		return Response.ok().build();
	}

	@PUT
	@Path("/{viewId}/network")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Update the Visual Properties for a Network View",
	notes="Updates the Visual Properties in the Network View specified by the `viewId` and `networkId` parameters.\n\nExample Visual Properties:\n" + ModelConstants.NETWORK_VISUAL_PROPERTY_EXAMPLES + "\n\n"
			+ ModelConstants.VISUAL_PROPERTIES_REFERENCES +"\n\n"
			+ BYPASS_NOTES)
	@ApiImplicitParams(
			@ApiImplicitParam(value="A list of Visual Properties and their values.", dataType="[Lorg.cytoscape.rest.internal.model.VisualPropertyValueModel;", paramType="body", required=true)
		)
	public Response updateNetworkView(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Network View SUID") @PathParam("viewId") Long viewId,
			@ApiParam(value="Bypass the Visual Style with these properties", defaultValue="false") @QueryParam("bypass") Boolean bypass, 
			@ApiParam(hidden=true) final InputStream is) {
		final CyNetworkView networkView = getView(networkId, viewId);
		final ObjectMapper objMapper = new ObjectMapper();

		if (bypass == null) {
			bypass = false;
		}

		try {
			// This should be an JSON array.
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
			styleMapper.updateView(networkView, rootNode, getLexicon(NO_VISUAL_LEXICON_ERROR), bypass);
		} catch (Exception e) {
			//throw getError("Could not parse the input JSON for updating view because: " + e.getMessage(), e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INVALID_PARAMETER_ERROR, 
					"Could not parse the input JSON for updating view because: " + e.getMessage(), 
					logger, e);
		}
		// Repaint
		networkView.updateView();

		return Response.ok().build();
	}


	@GET
	@Path("/{viewId}/network")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get the Visual Properties for a Network View", 
			notes="Returns a list of the Visual Properties for the Network View specified by the `viewId` and `networkId` parameters.\n\n"
				+ ModelConstants.VISUAL_PROPERTIES_REFERENCES,
			response=VisualPropertyValueModel.class, responseContainer="List")
	public Response getNetworkVisualProps(
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId, 
			@ApiParam(value="SUID of the Network View") @PathParam("viewId") Long viewId) {
		return this.getViews(networkId, viewId, "network", null);
	}

	@GET
	@Path("/{viewId}/{objectType}/{objectId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get the Visual Properties for a Node or Edge Object",
			notes="Gets a list of Visual Properties for the Object specified by the `objectId` "
					+ "and `objectType` parameters in the Network View specified by the "
					+ "`viewId` and `networkId` parameters.\n\n"
					+ ModelConstants.VISUAL_PROPERTIES_REFERENCES +"\n\n",
			response=VisualPropertyValueModel.class, responseContainer="List")
	public String getView(
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId, 
			@ApiParam(value="SUID of the Network View") @PathParam("viewId") Long viewId,
			@ApiParam(value="Type of Object", allowableValues="nodes,edges") @PathParam("objectType") String objectType, 
			@ApiParam(value="SUID of the Object")@PathParam("objectId") Long objectId) {
		final CyNetworkView networkView = getView(networkId, viewId);

		View<? extends CyIdentifiable> view = getObjectView(networkView, objectType, objectId);
		Collection<VisualProperty<?>> lexicon = getVisualProperties(objectType);

		if(view == null) {
			//throw getError("Could not find view.", new IllegalArgumentException(), Response.Status.NOT_FOUND);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NOT_FOUND_ERROR, 
					"Could not find view.", 
					logger, null);
		}

		try {
			return styleSerializer.serializeView(view, lexicon);
		} catch (IOException e) {
			//throw getError("Could not serialize the view object.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					SERIALIZATION_ERROR, 
					"Could not serialize the view object.", 
					getResourceLogger(), e);
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
		} else if (objectType.equals("network")) {
			view = networkView;
		}
		return view;
	}

	@GET
	@Path("/{viewId}/{objectType}/{objectId}/{visualProperty}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a Visual Property for an Object",
			notes="Gets the Visual Property specificed by the `visualProperty` parameter for the"
				+ " node or edge specified by the `objectId` parameter in the Network View "
				+ "specified by the `viewId` and `networkId` parameters.\n\n"
				+ ModelConstants.VISUAL_PROPERTIES_REFERENCES +"\n\n"
				,
			response=VisualPropertyValueModel.class)
	public String getSingleVisualPropertyValue(
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId, 
			@ApiParam(value="SUID of the Network View") @PathParam("viewId") Long viewId,
			@ApiParam(value="Type of Object", allowableValues="nodes,edges")@PathParam("objectType") String objectType,
			@ApiParam(value="SUID of the Object")@PathParam("objectId") Long objectId, 
			@ApiParam(value="Name of the Visual Property", example="NODE_SHAPE")@PathParam("visualProperty") String visualProperty) {
		final CyNetworkView networkView = getView(networkId, viewId);

		if (objectType == null || (!objectType.equals("nodes") && !objectType.equals("edges"))) {
			//throw getError("Object type " + objectType + " not recognized", new IllegalArgumentException(), Response.Status.NOT_FOUND);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NOT_FOUND_ERROR, 
					"Object type not recognized:" + objectType, 
					logger, null);
		}

		Collection<VisualProperty<?>> vps = getVisualProperties(objectType);
		View<? extends CyIdentifiable> view = getObjectView(networkView, objectType, objectId);

		if(view == null) {
			//throw getError("Could not find view.", new IllegalArgumentException(), Response.Status.NOT_FOUND);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NOT_FOUND_ERROR, 
					"Could not find view.", 
					logger, null);
		}

		return getSingleVp(visualProperty, view, vps);
	}

	private static class SingleVisualPropertyResponse extends CIResponse<VisualPropertyValueModel> {};

	@GET
	@Path("/{viewId}/{objectType}/{objectId}/{visualProperty}/bypass")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get the Visual Properties a Visual Style is bypassed with",
			notes = "Gets the bypass Visual Property specified by the `visualProperty` parameter from "
					+ "the object specified by the `objectId` and `objectType` parameters in the Network "
					+ "View Specified by the `viewId` and `networkId` parameters. The response is the "
					+ "Visual Property that is used in place of the definition provided by the Visual "
					+ "Style.\n\n"
					+ ModelConstants.VISUAL_PROPERTIES_REFERENCES +"\n\n",
	response=SingleVisualPropertyResponse.class
			)
	public Response getSingleVisualPropertyValueBypass(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Network View SUID") @PathParam("viewId") Long viewId,
			@ApiParam(value="Type of Object", allowableValues="nodes,edges")@PathParam("objectType") String objectType,
			@ApiParam(value="SUID of the Object") @PathParam("objectId") Long objectId, 
			@ApiParam(value="Name of the Visual Property")@PathParam("visualProperty") String visualProperty) {

		CyNetworkView networkView = getNetworkViewCI(networkId, viewId);
		
		View<? extends CyIdentifiable> view = getObjectView(networkView, objectType, objectId);

		if (view == null) {
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NOT_FOUND_ERROR, 
					"Could not find object view: " + objectId, 
					logger, null);
		}

		if (objectType == null || (!objectType.equals("nodes") && !objectType.equals("edges"))) {
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NOT_FOUND_ERROR, 
					"Object type not recognized: " + objectType, 
					logger, null);
		}

		VisualProperty<Object> targetVp = (VisualProperty<Object>) getTargetVisualPropertyCI(view, objectType, visualProperty, true);

		VisualPropertyValueModel entity = new VisualPropertyValueModel();
		entity.visualProperty = targetVp.getIdString();
		Object value = view.getVisualProperty(targetVp);
		if  (targetVp.getRange().getType().equals(Boolean.class)
				|| targetVp.getRange().getType().equals(String.class)
				||	Number.class.isAssignableFrom(targetVp.getRange().getType())) {
			entity.value = value;
		} else {
			entity.value = targetVp.toSerializableString(value);
		}
		return Response.ok(ciResponseFactory.getCIResponse(entity)).build();

	}

	private CyNetworkView getNetworkViewCI(long networkId, long viewId) throws WebApplicationException {
		CyNetworkView networkView = null;
		try {
			networkView = getView(networkId, viewId);
		} catch(NotFoundException e) {
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NOT_FOUND_ERROR, 
					e.getMessage(), 
					logger, e);
		}
		return networkView;
	}
	
	private VisualProperty<?> getTargetVisualPropertyCI(View<? extends CyIdentifiable> view, String objectType, String visualProperty, boolean mustExist) throws WebApplicationException {
		Collection<VisualProperty<?>> vps = getVisualProperties(objectType);

		VisualProperty<?> targetVp = null;
		for(final VisualProperty<?> vp: vps) {
			if(vp.getIdString().equals(visualProperty)) {
				if (mustExist) {
					if (view.isDirectlyLocked(vp)) {
						targetVp = vp;
					}
				} else {
					targetVp = vp;
				}
				break;
			}
		}
		if(targetVp == null) {
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					VISUAL_PROPERTY_DOES_NOT_EXIST_ERROR, 
					"Bypass Visual Property does not exist: " + visualProperty, 
					getResourceLogger(), null);
		}
		return targetVp;
	}
	
	@PUT
	@Path("/{viewId}/{objectType}/{objectId}/{visualProperty}/bypass")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Bypass a Visual Style with a set Visual Property",
			notes="Bypasses the Visual Style of the object specified by the `objectId` and "
				+ "`objectType` parameters, in the Network View specified by the `viewId` and "
				+ "`networkId` parameters. The Visual Property included in the message body "
				+ "will be used instead of the definition provided by the Visual Style.\n\n"
				+ "Examples of Visual Properties:\n\n" 
				+ ModelConstants.NODE_VISUAL_PROPERTY_EXAMPLE + "\n" 
				+ ModelConstants.EDGE_VISUAL_PROPERTY_EXAMPLE + "\n" 
				+ ModelConstants.NETWORK_VISUAL_PROPERTY_EXAMPLE + "\n\n"
				+ ModelConstants.VISUAL_PROPERTIES_REFERENCES)
	@ApiImplicitParams(
			@ApiImplicitParam(value="A Visual Property and its value.", dataType="org.cytoscape.rest.internal.model.VisualPropertyValueModel", paramType="body", required=true)
		)
	public Response putSingleVisualPropertyValueBypass(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Network View SUID") @PathParam("viewId") Long viewId,
			@ApiParam(value="Type of Object", allowableValues="nodes,edges")@PathParam("objectType") String objectType,
			@ApiParam(value="SUID of the Object")@PathParam("objectId") Long objectId, 
			@ApiParam(value="Name of the Visual Property")@PathParam("visualProperty") String visualProperty,
			@ApiParam(hidden= true)final InputStream inputStream) {

		CyNetworkView networkView = getNetworkViewCI(networkId, viewId);
		
		View<? extends CyIdentifiable> view = getObjectView(networkView, objectType, objectId);
		
		if (view == null) {
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NOT_FOUND_ERROR, 
					"Could not find object view: " + objectId, 
					logger, null);
		}
		
		getTargetVisualPropertyCI(view, objectType, visualProperty, false);
		
		try {
			final ObjectMapper objMapper = new ObjectMapper();
			final JsonNode rootNode = objMapper.readValue(inputStream, JsonNode.class);
			styleMapper.updateViewVisualProperty(view, rootNode, getLexicon(NO_VISUAL_LEXICON_ERROR), true);
		} catch (Exception e) {
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INVALID_PARAMETER_ERROR, 
					"Could not parse the input JSON for updating view. Reason: " + e.getMessage(), 
					logger, e);
		}
		return Response.ok().entity(ciResponseFactory.getCIResponse(new Object())).build();
	}

	@DELETE
	@Path("/{viewId}/{objectType}/{objectId}/{visualProperty}/bypass")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Delete the Visual Property a Visual Style is bypassed with",
	notes="Deletes the bypass Visual Property specified by the `visualProperty` parameter from "
			+ "the object specified by the `objectId` and `objectType` parameters in the Network "
			+ "View Specified by the `viewId` and `networkId` parameters. When this is done, the "
			+ "Visual Property will be defined by the Visual Style\n\n"
			+ ModelConstants.VISUAL_PROPERTIES_REFERENCES +"\n\n",
			response=CIResponse.class
			)
	public Response deleteSingleVisualPropertyValueBypass(
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId, 
			@ApiParam(value="SUID of the Network View") @PathParam("viewId") Long viewId,
			@ApiParam(value="Type of Object", allowableValues="nodes,edges")@PathParam("objectType") String objectType,
			@ApiParam(value="SUID of Object")@PathParam("objectId") Long objectId, 
			@ApiParam(value="Name of the Visual Property")@PathParam("visualProperty") String visualProperty) {

		CyNetworkView networkView = getNetworkViewCI(networkId, viewId);
		
		View<? extends CyIdentifiable> view = getObjectView(networkView, objectType, objectId);

		if (view == null) {
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NOT_FOUND_ERROR, 
					"Could not find object view: " + objectId, 
					logger, null);
		}

		VisualProperty<?> targetVp = getTargetVisualPropertyCI(view, objectType, visualProperty, true);

		view.clearValueLock(targetVp);

		return Response.ok().entity(ciResponseFactory.getCIResponse(new Object())).build();
	}


	@GET
	@Path("/{viewId}/network/{visualProperty}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a network Visual Property",
			notes="Gets the Network Visual Property specificed by the `visualProperty`, `viewId`, and `networkId` parameters.\n\n"
				+ ModelConstants.VISUAL_PROPERTIES_REFERENCES)
	public String getNetworkVisualProp(
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId, 
			@ApiParam(value="SUID of the Network View") @PathParam("viewId") Long viewId,
			@ApiParam(value="Name of the Visual Property") @PathParam("visualProperty") String visualProperty) {
		final CyNetworkView networkView = getView(networkId, viewId);

		if(nodeLexicon == null) {
			initLexicon();
		}
		return getSingleVp(visualProperty, networkView, networkLexicon);
	}
	
	@PUT
	@Path("/{viewId}/network/{visualProperty}/bypass")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Bypass the Network Visual Style with a set Visual Property",
			notes="Bypasses the Visual Style of the Network with the Visual Property specificed by the `visualProperty`, `viewId`, and `networkId` parameters.\n\n"
				+ ModelConstants.VISUAL_PROPERTIES_REFERENCES,
				response=CIResponse.class )
	@ApiImplicitParams(
			@ApiImplicitParam(value="A Visual Property and its value.", dataType="org.cytoscape.rest.internal.model.VisualPropertyValueModel", paramType="body", required=true)
		)
	public Response putNetworkVisualPropBypass(
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId, 
			@ApiParam(value="SUID of the Network View") @PathParam("viewId") Long viewId,
			@ApiParam(value="Name of the Visual Property") @PathParam("visualProperty") String visualProperty,
			@ApiParam(hidden=true) final InputStream is) {
		final CyNetworkView networkView = getNetworkViewCI(networkId, viewId);

		if(nodeLexicon == null) {
			initLexicon();
		}
		
		getTargetVisualPropertyCI(networkView, "network", visualProperty, false);

		try {
			final ObjectMapper objMapper = new ObjectMapper();
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
			styleMapper.updateViewVisualProperty(networkView, rootNode, getLexicon(NO_VISUAL_LEXICON_ERROR), true);
		} catch (Exception e) {
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INVALID_PARAMETER_ERROR, 
					"Could not parse the input JSON for updating view. Reason: " + e.getMessage(), 
					logger, e);
		}
		return Response.ok().entity(ciResponseFactory.getCIResponse(new Object())).build();
	}
	
	@DELETE
	@Path("/{viewId}/network/{visualProperty}/bypass")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Deletes the Visual Property the Network Visual Style is bypassed with",
			notes="Deletes the bypass Visual Property specificed by the `visualProperty`, `viewId`, and `networkId` parameters. When this is done, the " + 
				    "Visual Property will be defined by the Visual Style\n\n" + 
							ModelConstants.VISUAL_PROPERTIES_REFERENCES,
					response=CIResponse.class 
					)
	public Response deleteNetworkVisualProp(
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId, 
			@ApiParam(value="SUID of the Network View") @PathParam("viewId") Long viewId,
			@ApiParam(value="Name of the Visual Property") @PathParam("visualProperty") String visualProperty) {
		CyNetworkView networkView = getNetworkViewCI(networkId, viewId);
		
		VisualProperty<?> targetVp = getTargetVisualPropertyCI(networkView, "network", visualProperty, true);

		networkView.clearValueLock(targetVp);

		return Response.ok().entity(ciResponseFactory.getCIResponse(new Object())).build();
	}
	
	@GET
	@Path("/{viewId}/network/{visualProperty}/bypass")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get the Visual Property the Network Visual Style is bypassed with",
			notes="Gets the bypass Visual Property specified by the `visualProperty`, `viewId`, and `networkId` parameters. " + 
					" The response is the Visual Property that is used in place of the definition provided by the Visual "
					+ "Style.\n\n"
					+ ModelConstants.VISUAL_PROPERTIES_REFERENCES,
			response=SingleVisualPropertyResponse.class
	)
	public Response getNetworkVisualPropBypass(
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId, 
			@ApiParam(value="SUID of the Network View") @PathParam("viewId") Long viewId,
			@ApiParam(value="Name of the Visual Property") @PathParam("visualProperty") String visualProperty) {
		CyNetworkView networkView = getNetworkViewCI(networkId, viewId);
		
		VisualProperty<Object> targetVp = (VisualProperty<Object>) getTargetVisualPropertyCI(networkView, "network", visualProperty, true);

		VisualPropertyValueModel entity = new VisualPropertyValueModel();
		entity.visualProperty = targetVp.getIdString();
		Object value = networkView.getVisualProperty(targetVp);
		if  (targetVp.getRange().getType().equals(Boolean.class)
				|| targetVp.getRange().getType().equals(String.class)
				||	Number.class.isAssignableFrom(targetVp.getRange().getType())) {
			entity.value = value;
		} else {
			entity.value = targetVp.toSerializableString(value);
		}
		return Response.ok(ciResponseFactory.getCIResponse(entity)).build();
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
			//throw getError("Could not find such Visual Property: " + visualProperty , new NotFoundException(), Response.Status.NOT_FOUND);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					VISUAL_PROPERTY_DOES_NOT_EXIST_ERROR, 
					"Visual Property does not exist: " + visualProperty, 
					getResourceLogger(), null);
		}

		try {
			return styleSerializer.serializeSingleVisualProp(view, targetVp);
		} catch (IOException e) {
			//throw getError("Could not serialize the view object.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					SERIALIZATION_ERROR, 
					"Could not serialize the view object.", 
					getResourceLogger(), e);
		}
	}

	@GET
	@Path("/{viewId}/{objectType}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get all set values for a specific Visual Property",
		notes="Returns a list of all Visual Property values for the Visual Property specified by "
				+ "the `visualProperty` and `objectType` parameters, in the Network View specified by the "
				+ "`viewId` and `networkId` parameters.\n\n"
				+ ModelConstants.VISUAL_PROPERTIES_REFERENCES,
		response=ObjectVisualPropertyValueModel.class, responseContainer="List")
	public Response getViews(
			@ApiParam(value="SUID of the Network")@PathParam("networkId") Long networkId, 
			@ApiParam(value="SUID of the Network View") @PathParam("viewId") Long viewId,
			@ApiParam(value="Type of Object", allowableValues="nodes,edges,network") @PathParam("objectType") String objectType, 
			@ApiParam(value="Name of the Visual Property", example="NODE_SHAPE") @QueryParam("visualProperty") String visualProperty) {

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
				//throw getError("Could not serialize the view object.", e, Response.Status.INTERNAL_SERVER_ERROR);
				throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						getResourceURI(), 
						SERIALIZATION_ERROR, 
						"Could not serialize the view object.", 
						getResourceLogger(), e);
			}
		} 

		if(graphObjects == null || graphObjects.isEmpty()) {
			//throw getError("Could not find views.", new IllegalArgumentException(), Response.Status.NOT_FOUND);
			 throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(),  
			          RESOURCE_URN,  
			          NOT_FOUND_ERROR,  
			          "Could not find views.",  
			          logger, null); 
		}
		try {
			return styleSerializer.serializeViews(graphObjects, vps);
		} catch (IOException e) {
			//throw getError("Could not serialize the view object.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					SERIALIZATION_ERROR, 
					"Could not serialize the view object.", 
					getResourceLogger(), e);
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
			//throw getError("Visual Property does not exist: " + visualPropertyName, new NotFoundException(), Response.Status.NOT_FOUND);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					VISUAL_PROPERTY_DOES_NOT_EXIST_ERROR, 
					"Visual Property does not exist: " + visualPropertyName, 
					getResourceLogger(), null);
		}

		vps.add(vp);
		return getViewForVPList(networkId, viewId, objectType, vps);

	}

	private final CyNetworkView getView(Long networkId, Long viewId) {
		final Collection<CyNetworkView> views = this.getCyNetworkViews(NOT_FOUND_ERROR, NO_VIEWS_FOR_NETWORK_ERROR, networkId);
		for (final CyNetworkView view : views) {
			final Long vid = view.getSUID();
			if (vid.equals(viewId)) {
				return view;
			}
		}
		//throw new NotFoundException("Could not find view: " + viewId);
		throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
				RESOURCE_URN, 
				NOT_FOUND_ERROR, 
				"Could not find view: " + viewId, 
				logger, null);
	}
}