package org.cytoscape.rest.internal.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.write.VizmapWriterFactory;
import org.cytoscape.rest.internal.CyActivator.WriterListener;
import org.cytoscape.rest.internal.MappingFactoryManager;
import org.cytoscape.rest.internal.datamapper.VisualStyleMapper;
import org.cytoscape.rest.internal.model.CountModel;
import org.cytoscape.rest.internal.model.ModelConstants;
import org.cytoscape.rest.internal.model.TitleModel;
import org.cytoscape.rest.internal.model.VisualPropertyDependencyModel;
import org.cytoscape.rest.internal.model.VisualPropertyModel;
import org.cytoscape.rest.internal.model.VisualStyleDefaultsModel;
import org.cytoscape.rest.internal.model.VisualStyleMappingModel;
import org.cytoscape.rest.internal.model.VisualStyleModel;
import org.cytoscape.rest.internal.model.VisualPropertyValueModel;
import org.cytoscape.rest.internal.model.VisualPropertyValuesModel;
import org.cytoscape.rest.internal.serializer.VisualStyleModule;
import org.cytoscape.rest.internal.serializer.VisualStyleSerializer;
import org.cytoscape.rest.internal.task.HeadlessTaskMonitor;
import org.cytoscape.view.model.DiscreteRange;
import org.cytoscape.view.model.Range;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualPropertyDependency;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;

@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.VISUAL_STYLES_TAG})
@Singleton
@Path("/v1/styles")
public class StyleResource extends AbstractResource {

	static final String RESOURCE_URN = "styles";

	@Override
	public String getResourceURI() {
		return RESOURCE_URN;
	}
	
	private final static Logger logger = LoggerFactory.getLogger(StyleResource.class);
		
	@Override
	public Logger getResourceLogger() {
		return logger;
	}
	
	static final int NO_VISUAL_LEXICON_ERROR = 1;
	static final int INTERNAL_METHOD_ERROR = 2;
	static final int SERIALIZATION_ERROR = 3;
	static final int INVALID_PARAMETER_ERROR = 4;
	static final int NOT_FOUND_ERROR = 5;
	
	private final VisualStyleSerializer styleSerializer = new VisualStyleSerializer();

	@Inject
	private WriterListener writerListener;

	@Inject
	private TaskMonitor tm;

	@Inject
	private VisualStyleFactory vsFactory;

	@Inject
	private MappingFactoryManager factoryManager;

	private final ObjectMapper styleMapper;
	private final VisualStyleMapper visualStyleMapper;
	private final VisualStyleSerializer visualStyleSerializer;

	private static final String MAPPING_DESCRIPTION =  "The types of mapping available in Cytoscape are explained in depth [here](http://manual.cytoscape.org/en/stable/Styles.html#how-mappings-work). An example of the data format for each is included below. For additional details, such as what Visual Properties supported by each Mapping, click on the relevant JavaDoc API link.\n\n" +  ModelConstants.MAPPING_EXAMPLES;
	
	public StyleResource() {
		super();
		this.styleMapper = new ObjectMapper();
		this.styleMapper.registerModule(new VisualStyleModule());

		this.visualStyleMapper = new VisualStyleMapper();
		this.visualStyleSerializer = new VisualStyleSerializer();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get list of Visual Style names", 
		notes="Returns a list of all the Visual Style names in the current session.", 
		response=String.class, 
		responseContainer="List")
	public String getStyleNames() {
		final Collection<VisualStyle> styles = vmm.getAllVisualStyles();
		final List<String> styleNames = new ArrayList<String>();
		for (final VisualStyle style : styles) {
			styleNames.add(style.getTitle());
		}
		try {
			return getNames(styleNames);
		} catch (IOException e) {
			//throw getError("Could not get style names.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INTERNAL_METHOD_ERROR, 
					"Could not get style names.", 
					logger, e);
		}

	}

	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get number of Visual Styles",
		notes = "Returns the number of Visual Styles available in the current session",
		response = CountModel.class)
	public CountModel getStyleCount() {
		return new CountModel((long)vmm.getAllVisualStyles().size());
	}

	@DELETE
	@Path("/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Delete a Visual Style",
			notes="Deletes the Visual Style specified by the `name` parameter.")
	public Response deleteStyle(
			@ApiParam(value="Visual Style Name")@PathParam("name") String name) {
		vmm.removeVisualStyle(getStyleByName(name));
		return Response.ok().build();
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Delete all Visual Styles", 
		notes="Deletes all vision styles except for default style")
	public Response deleteAllStyles() {
		Set<VisualStyle> styles = vmm.getAllVisualStyles();
		Set<VisualStyle> toBeDeleted = new HashSet<VisualStyle>();
		for(final VisualStyle style: styles) {
			if(style.getTitle().equals("default") == false) {
				toBeDeleted.add(style);
			}
		}
		toBeDeleted.stream()
			.forEach(style->vmm.removeVisualStyle(style));
		return Response.ok().build();
	}

	@DELETE
	@Path("/{name}/mappings/{vpName}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Delete a Visual Mapping from a Visual Style",
			notes="Deletes the Visual Property mapping specified by the `vpName` and `name` parameters.")
	public Response deleteMapping(
			@ApiParam(value="Name of the Visual Style containing the Visual Mapping", example="default") @PathParam("name") String name, 
			@ApiParam(value="Name of the Visual Property that the Visual Mapping controls", example="NODE_SIZE") @PathParam("vpName") String vpName) {
		final VisualStyle style = getStyleByName(name);
		final VisualProperty<?> vp = getVisualProperty(vpName);
		if(vp == null) {
			//throw new NotFoundException("Could not find Visual Property: " + vpName);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NOT_FOUND_ERROR, 
					"Could not find Visual Property: " + vpName, 
					logger, null);
		}
		final VisualMappingFunction<?,?> mapping = style.getVisualMappingFunction(vp);
		if(mapping == null) {
			//throw new NotFoundException("Could not find mapping for: " + vpName);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NOT_FOUND_ERROR, 
					"Could not find mapping for: " + vpName, 
					logger, null);
		}
		style.removeVisualMappingFunction(vp);
		
		return Response.ok().build();
	}

	@GET
	@Path("/{name}/defaults")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get all default values for the Visual Style", 
		notes="Returns a list of all the default values for the Visual Style specified by the `name` parameter.",
		response=VisualStyleDefaultsModel.class)
	public String getDefaults(
			@ApiParam(value="Name of the Visual Style") @PathParam("name") String name) {
		return serializeDefaultValues(name);
	}

	@GET
	@Path("/{name}/defaults/{vp}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a default value for the Visual Property",
			notes="Returns the default value for the Visual Property specified by the `name` and `vp` parameters.\n\n"
					+ ModelConstants.VISUAL_PROPERTIES_REFERENCES +"\n\n",
			response=VisualPropertyValueModel.class)
	public String getDefaultValue(
			@ApiParam(value="Name of the Visual Style containing the Visual Property") @PathParam("name") String name, 
			@ApiParam(value="Name of the Visual Property") @PathParam("vp") String vp) {
		return serializeDefaultValue(name, vp);
	}

	@GET
	@Path("/visualproperties/{vp}/values")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get all available values for a Visual Property",
			notes="Returns a list of all available values for the Visual Property specified by the `vp` parameter.\n\nThis method is only for Visual Properties with a Discrete Range, such as NODE_SHAPE or EDGE_LINE_TYPE.\n\n"
				+ ModelConstants.VISUAL_PROPERTIES_REFERENCES +"\n\n",
			response=VisualPropertyValuesModel.class)
	public String getRangeValues(
			@ApiParam(value="ID of the Visual Property", example="NODE_SHAPE") @PathParam("vp") String vp) {
		return serializeRangeValue(vp);
	}

	@GET
	@Path("/visualproperties")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get all available Visual Properties", 
		notes="Get all available Visual Properties.\n\n"
			+ ModelConstants.VISUAL_PROPERTIES_REFERENCES)
	public Collection<VisualPropertyModel> getVisualProperties() {
		Set<VisualProperty<?>> vps = getAllVisualProperties();	
		return vps.stream().map(cyVp -> new org.cytoscape.rest.internal.model.VisualPropertyModel((VisualProperty<Object>) cyVp)).collect(Collectors.toList());
	}

	@GET
	@Path("/visualproperties/{visualProperty}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a Visual Property", notes="Return the Visual Property specified by the `visualProperty` parameter.\n\n"
			+ ModelConstants.VISUAL_PROPERTIES_REFERENCES, 
		response=VisualPropertyModel.class)
	public String getSingleVisualProperty(
			@ApiParam(value="ID of the Visual Property", example="NODE_SHAPE") @PathParam("visualProperty") String visualProperty) {
		final VisualProperty<?> vp = getVisualProperty(visualProperty);
		try {
			return styleSerializer.serializeVisualProperty(vp);
		} catch (IOException e) {
			//throw getError("Could not serialize Visual Properties.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INTERNAL_METHOD_ERROR, 
					"Could not serialize Visual Properties.", 
					logger, e);
		}
	}

	@GET
	@Path("/{name}/mappings")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get all Visual Mappings for the Visual Style",
	notes="Returns a list of all Visual Mappings used in the Visual Style specified by the `name` parameter.\n\n" + MAPPING_DESCRIPTION +"\n" 
			+ ModelConstants.VISUAL_PROPERTIES_REFERENCES
	, response=VisualStyleMappingModel.class, responseContainer="List")
	public String getMappings(
			@ApiParam(value="Name of the Visual Style") @PathParam("name") String name) {
		final VisualStyle style = getStyleByName(name);
		final Collection<VisualMappingFunction<?, ?>> mappings = style.getAllVisualMappingFunctions();
		if(mappings.isEmpty()) {
			return "[]";
		}
		
		try {
			return styleMapper.writeValueAsString(mappings);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			//throw getError("Could not serialize Mappings.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INTERNAL_METHOD_ERROR, 
					"Could not serialize Mappings.", 
					logger, e);
		} catch(Exception ex) {
			ex.printStackTrace();
			//throw getError("Could not serialize Mappings.", ex, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INTERNAL_METHOD_ERROR, 
					"Could not serialize Mappings.", 
					logger, ex);
		}
	}

	@GET
	@Path("/{name}/mappings/{vp}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a Visual Mapping associated with the Visual Property",
	notes="Returns the Visual Mapping assigned to the Visual Property specified by the `name` and `vp` parameters.\n\n" + MAPPING_DESCRIPTION +"\n"
			+ ModelConstants.VISUAL_PROPERTIES_REFERENCES, 
			response=VisualStyleMappingModel.class)
	public String getMapping(
			@ApiParam(value="Name of the Visual Style containing the Visual Property mapping") @PathParam("name") String name, 
			@ApiParam(value="Name of the Visual Property that the Visual Mapping controls") @PathParam("vp") String vp) {
		final VisualStyle style = getStyleByName(name);
		final VisualMappingFunction<?, ?> mapping = getMappingFunction(vp, style);
		
		try {
			return styleMapper.writeValueAsString(mapping);
		} catch (JsonProcessingException e) {
			//throw getError("Could not serialize Mapping.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INTERNAL_METHOD_ERROR, 
					"Could not serialize Mapping.", 
					logger, e);
		}
	}
	
	private final VisualMappingFunction<?, ?> getMappingFunction(final String vp, final VisualStyle style) {
		final VisualLexicon lexicon = getLexicon(NO_VISUAL_LEXICON_ERROR);
		final Set<VisualProperty<?>> allVp = lexicon.getAllVisualProperties();
		VisualProperty<?> visualProp = null;
		for (VisualProperty<?> curVp : allVp) {
			if (curVp.getIdString().equals(vp)) {
				visualProp = curVp;
				break;
			}
		}
		if (visualProp == null) {
			//throw new NotFoundException("Could not find VisualProperty: " + vp);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NOT_FOUND_ERROR, 
					"Could not find VisualProperty: " + vp, 
					logger, null);
		}
		final VisualMappingFunction<?, ?> mapping = style.getVisualMappingFunction(visualProp);
		
		if(mapping == null) {
			//throw new NotFoundException("Could not find visual mapping function for " + vp);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NOT_FOUND_ERROR, 
					"Could not find visual mapping function for " + vp, 
					logger, null);
		}
		
		return mapping;
	}

	private final String serializeDefaultValues(String name) {
		final VisualStyle style = getStyleByName(name);
		final VisualLexicon lexicon = getLexicon(NO_VISUAL_LEXICON_ERROR);
		final Collection<VisualProperty<?>> vps = lexicon.getAllVisualProperties();
		try {
			return styleSerializer.serializeDefaults(vps, style);
		} catch (IOException e) {
			//throw getError("Could not serialize default values.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INTERNAL_METHOD_ERROR, 
					"Could not serialize default values.", 
					logger, e);
		}
	}


	private final String serializeDefaultValue(final String styleName, final String vpName) {
		final VisualStyle style = getStyleByName(styleName);
		VisualProperty<Object> vp = (VisualProperty<Object>) getVisualProperty(vpName);
		try {
			return styleSerializer.serializeDefault(vp, style);
		} catch (IOException e) {
			//throw getError("Could not serialize default value for " + vpName, e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INTERNAL_METHOD_ERROR, 
					"Could not serialize default value for " + vpName, 
					logger, e);
		}
	}


	private final String serializeRangeValue(final String vpName) {
		VisualProperty<Object> vp = (VisualProperty<Object>) getVisualProperty(vpName);
		Range<Object> range = vp.getRange();
		if (range.isDiscrete()) {
			final DiscreteRange<Object> discRange = (DiscreteRange<Object>)range;
			try {
				return styleSerializer.serializeDiscreteRange(vp, discRange);
			} catch (IOException e) {
				//throw getError("Could not serialize default value for "
				//		+ vpName, e, Response.Status.INTERNAL_SERVER_ERROR);
				throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						RESOURCE_URN, 
						INTERNAL_METHOD_ERROR, 
						"Could not serialize default value for " + vpName, 
						logger, e);
			}
		} else {
			//throw new NotFoundException("Range object is not available for " + vpName);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					RESOURCE_URN, 
					NOT_FOUND_ERROR, 
					"Range object is not available for " + vpName, 
					logger, null);
		}
	}

	private final Set<VisualProperty<?>> getAllVisualProperties() {
		final VisualLexicon lexicon = getLexicon(NO_VISUAL_LEXICON_ERROR);
		return lexicon.getAllVisualProperties();
	}

	private final VisualProperty<?> getVisualProperty(String vpName) {
		final VisualLexicon lexicon = getLexicon(NO_VISUAL_LEXICON_ERROR);
		final Collection<VisualProperty<?>> vps = lexicon.getAllVisualProperties();
		for (VisualProperty<?> vp : vps) {
			if (vp.getIdString().equals(vpName)) {
				return vp;
			}
		}

		return null;
	}

	// /////////// Update Default Values ///////////////

	@PUT
	@Path("/{name}/defaults")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Update the default values for Visual Properties",
			notes="Updates the default values for the Visual Properties in the Visual Style specified by the `name` parameter.\n\n"
				+ ModelConstants.VISUAL_PROPERTIES_REFERENCES)
	@ApiImplicitParams(
			@ApiImplicitParam(value="A list of Visual Property values to update.", 
				dataType="[Lorg.cytoscape.rest.internal.model.VisualPropertyValueModel;", paramType="body", required=true)
			)
	public Response updateDefaults(
			@ApiParam(value="Name of the Visual Style")@PathParam("name") String name, 
			@ApiParam(hidden=true) InputStream is) {
			
		final VisualStyle style = getStyleByName(name);
		final ObjectMapper objMapper = new ObjectMapper();
		try {
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
			updateVisualProperties(rootNode, style);
		} catch (Exception e) {
			//throw getError("Could not update default values.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INVALID_PARAMETER_ERROR, 
					"Could not update default values.", 
					logger, e);
		}
		
		return Response.ok().build();
	}

	@SuppressWarnings("unchecked")
	private final void updateVisualProperties(final JsonNode rootNode, VisualStyle style) {
		for(JsonNode defaultNode: rootNode) {
			final String vpName = defaultNode.get(VisualStyleMapper.MAPPING_VP).textValue();

			@SuppressWarnings("rawtypes")
			final VisualProperty vp = getVisualProperty(vpName);
			if (vp == null) {
				continue;
			}
			final Object value = vp.parseSerializableString(defaultNode.get("value").asText());
			if(value != null) {
				style.setDefaultValue(vp, value);
			}
		}
	}

	@GET
	@Path("/{name}.json")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a Visual Style in Cytoscape.js CSS format.",
			notes="Visual Style in [Cytoscape.js CSS](http://js.cytoscape.org/#style) format.")
	public String getStyle(
			@ApiParam(value="Name of the Visual Style") @PathParam("name") String name) {
		if(networkViewManager.getNetworkViewSet().isEmpty()) {
			//throw getError("You need at least one view object to use this feature."
			//		, new IllegalStateException(), Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),  
			          RESOURCE_URN,  
			          INVALID_PARAMETER_ERROR,  
			          "You need at least one view object to use this feature.",  
			          logger, null); 
		}
		final VisualStyle style = getStyleByName(name);
		final VizmapWriterFactory jsonVsFact = this.writerListener.getFactory();
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final Set<VisualStyle> styleCollection = new HashSet<VisualStyle>();
		styleCollection.add(style);
		try {
			final CyWriter styleWriter = jsonVsFact.createWriter(os, styleCollection);
			styleWriter.run(new HeadlessTaskMonitor());
			String jsonString = os.toString("UTF-8");
			os.close();
			return jsonString;
		} catch (Exception e) {
			//throw getError("Could not get Visual Style in Cytoscape.js format.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					SERIALIZATION_ERROR, 
					"Could not get Visual Style in Cytoscape.js format.", 
					logger, e);
		}
	}

	@GET
	@Path("/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a Visual Style with full details",
			notes="Returns the Visual Style specified by the `name` parameter.",
			response=VisualStyleModel.class
			)
	public String getStyleFull(
			@ApiParam(value="Name of the Visual Style") @PathParam("name") String name) {
		final VisualStyle style = getStyleByName(name);
		try {
			return styleSerializer.serializeStyle(getLexicon(NO_VISUAL_LEXICON_ERROR).getAllVisualProperties(), style);
		} catch (IOException e) {
			//throw getError("Could not get Visual Style JSON.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INTERNAL_METHOD_ERROR, 
					"Could not get Visual Style JSON.", 
					logger, e);
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Create a new Visual Style from JSON.", 
		notes="Creates a new Visual Style using the message body.\n\nReturns the title of the new Visual Style. If the title of the Visual Style already existed in the session, a new one will be automatically generated and returned.",
		response=TitleModel.class)
	@ApiImplicitParams(
			@ApiImplicitParam(value="The details of the new Visual Style to be created.", dataType="org.cytoscape.rest.internal.model.VisualStyleModel", paramType="body", required=true)
			)
	public Response createStyle(@ApiParam(hidden=true)InputStream is) {
		final ObjectMapper objMapper = new ObjectMapper();
		JsonNode rootNode;
		try {
			rootNode = objMapper.readValue(is, JsonNode.class);
			VisualStyle style = this.visualStyleMapper.buildVisualStyle(factoryManager, vsFactory, getLexicon(NO_VISUAL_LEXICON_ERROR),
					rootNode);
			vmm.addVisualStyle(style);
			
			// This may be different from the original one if same name exists.
			final String result = "{\"title\": \""+ style.getTitle() + "\"}";
			
			return Response.status(Response.Status.CREATED).entity(result).build();
		} catch (Exception e) {
			//throw getError("Could not create new Visual Style.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INVALID_PARAMETER_ERROR, 
					"Could not create new Visual Style.", 
					logger, e);
		}
	}

	@POST
	@Path("/{name}/mappings")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Add a new Visual Mapping", notes="Create a new Visual Mapping function and add it to the Visual Style specified by the `name` parameter. Existing mappings in the Visual Style will be overidden by the new mappings created.\n\n"
			+ MAPPING_DESCRIPTION + "\n"
			+ ModelConstants.VISUAL_PROPERTIES_REFERENCES
			)
	@ApiImplicitParams(
		@ApiImplicitParam(value="A list of new mappings.", dataType="[Lorg.cytoscape.rest.internal.model.VisualStyleMappingModel;", 
		paramType="body", 
		required=true, 
		examples = //The following is for some reason not included in the generated swagger. This needs repair.
			@Example(value = {
					@ExampleProperty(mediaType="default", value="[{\n" + 
							"      \"mappingType\": \"continuous\",\n" + 
							"      \"mappingColumn\": \"degree.layout\",\n" + 
							"      \"mappingColumnType\": \"Integer\",\n" + 
							"      \"visualProperty\": \"NODE_SIZE\",\n" + 
							"      \"points\": [\n" + 
							"        {\n" + 
							"          \"value\": 1,\n" + 
							"          \"lesser\": \"1.0\",\n" + 
							"          \"equal\": \"40.0\",\n" + 
							"          \"greater\": \"40.0\"\n" + 
							"        },\n" + 
							"        {\n" + 
							"          \"value\": 18,\n" + 
							"          \"lesser\": \"150.0\",\n" + 
							"          \"equal\": \"150.0\",\n" + 
							"          \"greater\": \"1.0\"\n" + 
							"        }\n" + 
							"      ]\n" + 
							"    }")}
			)
		)
	)
	public Response createMappings(
			@ApiParam(value="Name of the Visual Style") @PathParam("name") String name,
			@ApiParam(hidden=true) InputStream is) {
		final VisualStyle style = getStyleByName(name);
		final ObjectMapper objMapper = new ObjectMapper();
		JsonNode rootNode;
		try {
			rootNode = objMapper.readValue(is, JsonNode.class);
			this.visualStyleMapper.buildMappings(style, factoryManager, getLexicon(NO_VISUAL_LEXICON_ERROR),rootNode);
		} catch (Exception e) {
			e.printStackTrace();
			//throw getError("Could not create new Mapping.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INVALID_PARAMETER_ERROR, 
					"Could not create new Mapping.", 
					logger, e);
		}
		return Response.status(Response.Status.CREATED).build();
	}

	@PUT
	@Path("/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Update name of a Visual Style", notes="Updates the name of the Visual Style specified by the `name` parameter.")
	@ApiImplicitParams(
			@ApiImplicitParam(value="A new name for the Visual Style.", dataType="org.cytoscape.rest.internal.model.TitleModel", paramType="body", required=true)
	)
	public void updateStyleName(
			@ApiParam(value="Original name of the Visual Style") @PathParam("name") String name,
			@ApiParam(hidden=true) InputStream is) {
		final VisualStyle style = getStyleByName(name);
		final ObjectMapper objMapper = new ObjectMapper();
		JsonNode rootNode;
		try {
			rootNode = objMapper.readValue(is, JsonNode.class);
		} catch (Exception e) {
			//throw getError("Could not update Visual Style title.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INVALID_PARAMETER_ERROR, 
					"Could not update Visual Style title.", 
					logger, e);
		}
		this.visualStyleMapper.updateStyleName(style, getLexicon(NO_VISUAL_LEXICON_ERROR), rootNode);
	}

	@PUT
	@Path("/{name}/mappings/{vp}")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Update an existing Visual Mapping",
			notes="Update the visual mapping specified by the `name` and `vp` parameters.\n\n"
				+ MAPPING_DESCRIPTION + "\n"
				+ ModelConstants.VISUAL_PROPERTIES_REFERENCES)
	@ApiImplicitParams(
			@ApiImplicitParam(value="A list of new mappings.", dataType="[Lorg.cytoscape.rest.internal.model.VisualStyleMappingModel;", paramType="body", required=true)
			)
	public Response updateMapping(
			@ApiParam(value="Name of the Visual Style containing the Visual Mapping") @PathParam("name") String name,  
			@ApiParam(value="Name of the Visual Property that the Visual Mapping controls") @PathParam("vp") String vp, 
			@ApiParam(hidden=true) InputStream is) {
		final VisualStyle style = getStyleByName(name);
		final VisualMappingFunction<?, ?> currentMapping = getMappingFunction(vp, style);
	
		final ObjectMapper objMapper = new ObjectMapper();
		JsonNode rootNode;
		try {
			rootNode = objMapper.readValue(is, JsonNode.class);
			this.visualStyleMapper.buildMappings(style, factoryManager, getLexicon(NO_VISUAL_LEXICON_ERROR),rootNode);
		} catch (Exception e) {
			e.printStackTrace();
			//throw getError("Could not update Mapping.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INVALID_PARAMETER_ERROR, 
					"Could not update Mapping.", 
					logger, e);
		}
		
		return Response.ok().build();
	}


	private final VisualStyle getStyleByName(final String name) {
		final Collection<VisualStyle> styles = vmm.getAllVisualStyles();
		for (final VisualStyle style : styles) {
			if (style.getTitle().equals(name)) {
				return style;
			}
		}

		//throw new NotFoundException("Could not find Visual Style: " + name);
		throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
				RESOURCE_URN, 
				NOT_FOUND_ERROR, 
				"Could not find Visual Style: " + name, 
				logger, null);
	}

	@GET
	@Path("/{name}/dependencies")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get all Visual Property Dependency statuses",
			notes="Returns the status of all the Visual Property Dependencies.",
			response=VisualPropertyDependencyModel.class, responseContainer="List")
	public String getAllDependencies(
			@ApiParam("Name of the Visual Style") @PathParam("name") String name) {
		final VisualStyle style = getStyleByName(name);
		
		final Set<VisualPropertyDependency<?>> dependencies = style.getAllVisualPropertyDependencies();
		try {
			return visualStyleSerializer.serializeDependecies(dependencies);
		} catch (IOException e) {
			//throw getError("Could not get Visual Property denendencies.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INTERNAL_METHOD_ERROR, 
					"Could not get Visual Property dependencies.", 
					logger, e);
		}
	}

	@PUT
	@Path("/{name}/dependencies")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Set Visual Property Dependencies",
			notes="Sets the value of Visual Property dependencies to the values in the message body."
	 )
	@ApiImplicitParams(
			@ApiImplicitParam(value="A list of dependencies.", dataType="[Lorg.cytoscape.rest.internal.model.VisualPropertyDependencyModel;", paramType="body", required=true)
	)
	public void updateDependencies(
			@ApiParam(value="Name of the Visual Style") @PathParam("name") String name, 
			@ApiParam(hidden=true) InputStream is) {
		final VisualStyle style = getStyleByName(name);
		
		final ObjectMapper objMapper = new ObjectMapper();
		JsonNode rootNode;
		try {
			rootNode = objMapper.readValue(is, JsonNode.class);
		} catch (Exception e) {
			//throw getError("Could not update Visual Style title.", e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INVALID_PARAMETER_ERROR, 
					"Could not update Visual Style title.", 
					logger, e);
		}
		this.visualStyleMapper.updateDependencies(style, rootNode);
	}
}