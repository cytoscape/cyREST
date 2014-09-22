package org.cytoscape.rest.internal.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.write.VizmapWriterFactory;
import org.cytoscape.rest.internal.CyActivator.WriterListener;
import org.cytoscape.rest.internal.MappingFactoryManager;
import org.cytoscape.rest.internal.datamapper.VisualStyleMapper;
import org.cytoscape.rest.internal.serializer.VisualStyleModule;
import org.cytoscape.rest.internal.serializer.VisualStyleSerializer;
import org.cytoscape.rest.internal.task.HeadlessTaskMonitor;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskMonitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
@Path("/v1/styles")
public class StyleResource extends AbstractResource {

	private final VisualStyleSerializer styleSerializer = new VisualStyleSerializer();

	@Context
	private WriterListener writerListener;

	@Context
	private TaskMonitor tm;

	@Context
	private VisualStyleFactory vsFactory;

	@Context
	private MappingFactoryManager factoryManager;

	private final ObjectMapper styleMapper;
	private final VisualStyleMapper visualStyleMapper;

	public StyleResource() {
		super();
		this.styleMapper = new ObjectMapper();
		this.styleMapper.registerModule(new VisualStyleModule());

		this.visualStyleMapper = new VisualStyleMapper();
	}

	
	/**
	 * 
	 * @summary Get list of Visual Style titles
	 * 
	 * @return List of Visual Style titles.
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStyleNames() {
		final Collection<VisualStyle> styles = vmm.getAllVisualStyles();
		final List<String> styleNames = new ArrayList<String>();
		for (final VisualStyle style : styles) {
			styleNames.add(style.getTitle());
		}
		try {
			return getNames(styleNames);
		} catch (IOException e) {
			throw getError("Could not get style names.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * @summary Get number of Visual Styles
	 * 
	 * @return Number of Visual Styles available in current session.
	 * 
	 */
	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStylCount() {
		return getNumberObjectString(JsonTags.COUNT, vmm.getAllVisualStyles().size());
	}

	/**
	 * 
	 * @summary Delete a Visual Style
	 * 
	 * @param name
	 */
	@DELETE
	@Path("/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteStyle(@PathParam("name") String name) {
		final VisualStyle style = getStyleByName(name);
		vmm.removeVisualStyle(style);
	}
	
	
	/**
	 * @summary Delete all Visual Styles except default style
	 * 
	 */
	@DELETE
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteAllStyles() {
		Set<VisualStyle> styles = vmm.getAllVisualStyles();
		Set<VisualStyle> toBeDeleted = new HashSet<VisualStyle>();
		for(final VisualStyle style: styles) {
			if(style.getTitle().equals("default") == false) {
				toBeDeleted.add(style);
			}
		}
		for(final VisualStyle style: toBeDeleted) {
			vmm.removeVisualStyle(style);
		}
	}


	/**
	 * 
	 * @summary Delete a Visual Mapping from a Visual Style
	 * 
	 * @param name Name of the Visual Style
	 * @param vpName Visual Property name associated with the mapping
	 * 
	 */
	@DELETE
	@Path("/{name}/mappings/{vpName}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteMapping(@PathParam("name") String name, @PathParam("vpName") String vpName) {
		final VisualStyle style = getStyleByName(name);
		final VisualProperty<?> vp = getVisualProperty(vpName);
		if(vp == null) {
			throw new NotFoundException("Could not find Visual Property: " + vpName);
		}
		final VisualMappingFunction<?,?> mapping = style.getVisualMappingFunction(vp);
		if(mapping == null) {
			throw new NotFoundException("Could not find mapping for: " + vpName);
		}
		style.removeVisualMappingFunction(vp);
	}


	private final VisualLexicon getLexicon() {
		final Set<VisualLexicon> lexicon = this.vmm.getAllVisualLexicon();
		if (lexicon.isEmpty()) {
			throw getError("Could not find visual lexicon.", new IllegalStateException(), Response.Status.INTERNAL_SERVER_ERROR);
		}
		return lexicon.iterator().next();
	}

	
	/**
	 * 
	 * @summary Get all default values for the Visual Style
	 * 
	 * @param name Name of the Visual Style
	 * 
	 * @return List of all default values
	 * 
	 */
	@GET
	@Path("/{name}/defaults")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDefaults(@PathParam("name") String name) {
		return getVp(name, null);
	}

	
	/**
	 * 
	 * @summary Get a default value for the Visual Property
	 * 
	 * @param name Name of Visual Style
	 * @param vp Unique ID of the Visual Property.  This should be the unique ID of the VP.
	 * 
	 * @return Default value for the Visual Property
	 * 
	 */
	@GET
	@Path("/{name}/defaults/{vp}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDefaultValue(@PathParam("name") String name,
			@PathParam("vp") String vp) {
		return getVp(name, vp);
	}


	/**
	 * 
	 * @summary Get all Visual Mappings for the Visual Style
	 * 
	 * @param name Name of the Visual Style
	 * 
	 * @return List of all Visual Mappings.
	 * 
	 */
	@GET
	@Path("/{name}/mappings")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMappings(@PathParam("name") String name) {
		final VisualStyle style = getStyleByName(name);
		final Collection<VisualMappingFunction<?, ?>> mappings = style.getAllVisualMappingFunctions();
		try {
			return styleMapper.writeValueAsString(mappings);
		} catch (JsonProcessingException e) {
			throw getError("Could not serialize Mappings.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	
	/**
	 * 
	 * @summary Get a Visual Mapping associated with the Visual Property
	 * 
	 * @param name Name of the Visual Style
	 * @param vp Unique ID of the Visual Property.  This should be the unique ID of the VP.
	 * 
	 * @return Visual Mapping assigned to the Visual Property
	 * 
	 */
	@GET
	@Path("/{name}/mappings/{vp}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMapping(@PathParam("name") String name, @PathParam("vp") String vp) {
		final VisualStyle style = getStyleByName(name);
		final VisualLexicon lexicon = getLexicon();
		final Set<VisualProperty<?>> allVp = lexicon.getAllVisualProperties();
		VisualProperty<?> visualProp = null;
		for (VisualProperty<?> curVp : allVp) {
			if (curVp.getIdString().equals(vp)) {
				visualProp = curVp;
				break;
			}
		}
		if (visualProp == null) {
			throw new NotFoundException("Could not find VisualProperty: " + vp);
		}
		final VisualMappingFunction<?, ?> mapping = style.getVisualMappingFunction(visualProp);
		try {
			return styleMapper.writeValueAsString(mapping);
		} catch (JsonProcessingException e) {
			throw getError("Could not serialize Mapping.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	private final String getVp(String name, String vpName) {
		final VisualStyle style = getStyleByName(name);
		final VisualLexicon lexicon = getLexicon();
		final Collection<VisualProperty<?>> vps;
		if (vpName == null) {
			vps = lexicon.getAllVisualProperties();
		} else {
			VisualProperty<?> vp = getVisualProperty(vpName);
			vps = new HashSet<VisualProperty<?>>();
			if(vp != null) {
				vps.add(vp);
			}
		}
		try {
			return styleSerializer.serializeDefaults(vps, style);
		} catch (IOException e) {
			throw getError("Could not serialize default values.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	private final VisualProperty<?> getVisualProperty(String vpName) {
		final VisualLexicon lexicon = getLexicon();
		final Collection<VisualProperty<?>> vps = lexicon.getAllVisualProperties();
		for (VisualProperty<?> vp : vps) {
			if (vp.getIdString().equals(vpName)) {
				return vp;
			}
		}

		return null;
	}

	// /////////// Update Default Values ///////////////

	
	/**
	 * 
	 * @summary Update a default value for the Visual Property
	 * 
	 * @param name Name of the Visual Style
	 * 
	 */
	@PUT
	@Path("/{name}/defaults")
	@Produces(MediaType.APPLICATION_JSON)
	public void updateDefaults(@PathParam("name") String name, InputStream is) {
		final VisualStyle style = getStyleByName(name);
		final ObjectMapper objMapper = new ObjectMapper();
		try {
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
			updateVisualProperties(rootNode, style);
		} catch (Exception e) {
			throw getError("Could not update default values.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@SuppressWarnings("unchecked")
	private final void updateVisualProperties(final JsonNode rootNode, VisualStyle style) {
		for(JsonNode defaultNode: rootNode) {
			final String vpName = defaultNode.get("visual_property").textValue();

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


	/**
	 * 
	 * @summary Get a Visual Style in Cytoscape.js CSS format.
	 * 
	 * @param name Name of the Visual Style
	 * 
	 * @return Visual Style in Cytoscape.js CSS format.  This is always in an array.
	 * 
	 */
	@GET
	@Path("/{name}.json")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStyle(@PathParam("name") String name) {
		final VisualStyle style = getStyleByName(name);

		final VizmapWriterFactory jsonVsFact = this.writerListener.getFactory();
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final Set<VisualStyle> styleCollection = new HashSet<VisualStyle>();
		styleCollection.add(style);
		final CyWriter styleWriter = jsonVsFact.createWriter(os, styleCollection);
		try {
			styleWriter.run(new HeadlessTaskMonitor());
			String jsonString = os.toString("UTF-8");
			os.close();
			return jsonString;
		} catch (Exception e) {
			throw getError("Could not get Visual Style in Cytoscape.js format.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 
	 * This returns JSON version of Visual Style object with full details.  Format is simple:
	 * 
	 * <pre>
	 * {
	 * 	"title": (name of this Visual Style),
	 * 	"defaults": [ default values ],
	 * 	"mappings": [ Mappings ]
	 * }
	 * </pre>
	 * 
	 * Essentially, this is a JSON version of the Visual Style XML file.
	 * 
	 * 
	 * @summary Get a Visual Style with full details
	 * 
	 * @param name Name of the Visual Style
	 * 
	 * @return Visual Style in Cytoscape JSON format 
	 */
	@GET
	@Path("/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStyleFull(@PathParam("name") String name) {
		final VisualStyle style = getStyleByName(name);
		try {
			return styleSerializer.serializeStyle(getLexicon().getAllVisualProperties(), style);
		} catch (IOException e) {
			throw getError("Could not get Visual Style JSON.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}


	/**
	 * @summary Create a new Visual Style from JSON.
	 * 
	 * @return Title of the new Visual Style.
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createStyle(InputStream is) {
		final ObjectMapper objMapper = new ObjectMapper();
		JsonNode rootNode;
		try {
			rootNode = objMapper.readValue(is, JsonNode.class);
			VisualStyle style = this.visualStyleMapper.buildVisualStyle(factoryManager, vsFactory, getLexicon(),
					rootNode);
			vmm.addVisualStyle(style);
			
			// This may be different from the original one if same name exists.
			return "{\"title\": \""+ style.getTitle() + "\"}";
		} catch (Exception e) {
			throw getError("Could not create new Visual Style.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}



	/**
	 * 
	 * Create a new Visual Mapping from JSON and add it to the Style.
	 * 
	 * @summary Add a new Visual Mapping
	 * 
	 * @param name Name of the Visual Style
	 */
	@POST
	@Path("/{name}/mappings")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void addMappings(@PathParam("name") String name,InputStream is) {
		final VisualStyle style = getStyleByName(name);
		final ObjectMapper objMapper = new ObjectMapper();
		JsonNode rootNode;
		try {
			rootNode = objMapper.readValue(is, JsonNode.class);
			this.visualStyleMapper.buildMappings(style, factoryManager, getLexicon(),rootNode);
		} catch (Exception e) {
			throw getError("Could not create new Mapping.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}


	/**
	 * 
	 * @summary Update name of a Visual Style
	 * 
	 * @param name Original name of the Visual Style
	 * 
	 */
	@PUT
	@Path("/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void updateStyleName(@PathParam("name") String name, InputStream is) {
		final VisualStyle style = getStyleByName(name);
		final ObjectMapper objMapper = new ObjectMapper();
		JsonNode rootNode;
		try {
			rootNode = objMapper.readValue(is, JsonNode.class);
			this.visualStyleMapper.updateStyleName(style, getLexicon(), rootNode);
		} catch (Exception e) {
			throw getError("Could not update Visual Style title.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}


	private final VisualStyle getStyleByName(final String name) {
		final Collection<VisualStyle> styles = vmm.getAllVisualStyles();
		for (final VisualStyle style : styles) {
			if (style.getTitle().equals(name)) {
				return style;
			}
		}

		throw new NotFoundException("Could not find Visual Style: " + name);
	}
}