package org.cytoscape.rest.internal.service;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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
public class StyleService extends AbstractDataService {

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

	public StyleService() {
		super();
		this.styleMapper = new ObjectMapper();
		this.styleMapper.registerModule(new VisualStyleModule());

		this.visualStyleMapper = new VisualStyleMapper();
	}

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
			throw new WebApplicationException(e, 500);
		}

	}

	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStylCount() {
		return getNumberObjectString("styleCount", vmm.getAllVisualStyles().size());
	}

	@DELETE
	@Path("/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteStyle(@PathParam("name") String name) {
		final VisualStyle style = getStyleByName(name);
		vmm.removeVisualStyle(style);
	}


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


	@DELETE
	@Path("/{name}/mappings")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteAllMappings(@PathParam("name") String name) {
		final VisualStyle style = getStyleByName(name);
		final Collection<VisualMappingFunction<?, ?>> mappings = style.getAllVisualMappingFunctions();
		final Set<VisualProperty<?>> toBeRemoved = new HashSet<VisualProperty<?>>();
		for(final VisualMappingFunction<?,?> mapping:mappings) {
			toBeRemoved.add(mapping.getVisualProperty());
		}
		
		for(final VisualProperty<?> vp: toBeRemoved) {
			style.removeVisualMappingFunction(vp);
		}
	}


	private final VisualLexicon getLexicon() {
		final Set<VisualLexicon> lexicon = this.vmm.getAllVisualLexicon();
		if (lexicon.isEmpty()) {
			throw new WebApplicationException("Could not find lexicon.", 500);
		}
		return lexicon.iterator().next();
	}

	@GET
	@Path("/{name}/defaults")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDefaults(@PathParam("name") String name) {
		return getVp(name, null);
	}

	@GET
	@Path("/{name}/defaults/{vpName}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDefaultValue(@PathParam("name") String name,
			@PathParam("vpName") String vpName) {
		return getVp(name, vpName);
	}

	@GET
	@Path("/{name}/mappings")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMappings(@PathParam("name") String name) {
		final VisualStyle style = getStyleByName(name);
		final Collection<VisualMappingFunction<?, ?>> mappings = style.getAllVisualMappingFunctions();
		try {
			return styleMapper.writeValueAsString(mappings);
		} catch (JsonProcessingException e) {
			throw new WebApplicationException(e, 500);
		}
	}

	@GET
	@Path("/{name}/mappings/{vp}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMapping(@PathParam("name") String name, @PathParam("type") String type, @PathParam("vp") String vp) {
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
			throw new WebApplicationException(e, 500);
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
			throw new WebApplicationException(e, 500);
		}
	}

	private final VisualProperty getVisualProperty(String vpName) {
		final VisualLexicon lexicon = getLexicon();
		final Collection<VisualProperty<?>> vps = lexicon.getAllVisualProperties();
		for (VisualProperty vp : vps) {
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
	public void updateDefaults(@PathParam("name") String name, @PathParam("type") String type, InputStream is) {
		final VisualStyle style = getStyleByName(name);
		final ObjectMapper objMapper = new ObjectMapper();
		try {
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
			updateVisualProperties(rootNode, style);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e, 500);
		}
	}

	private final void updateVisualProperties(final JsonNode rootNode, VisualStyle style) {
		for(JsonNode defaultNode: rootNode) {
			final String vpName = defaultNode.get("visual_property").textValue();
			@SuppressWarnings("unchecked")
			final VisualProperty<Object> vp = getVisualProperty(vpName);
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
	@Path("/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStyle(@PathParam("name") String name, @QueryParam("format") String format) {
		final VisualStyle style = getStyleByName(name);

		if (format == null || format.trim().length() == 0) {
			final VizmapWriterFactory jsonVsFact = this.writerListener.getFactory();
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			final Set<VisualStyle> styleCollection = new HashSet<VisualStyle>();
			styleCollection.add(style);
			final CyWriter styleWriter = jsonVsFact.createWriter(os, styleCollection);
			try {
				styleWriter.run(new HeadlessTaskMonitor());
				String jsonString = os.toString();
				os.close();
				
				return jsonString;
			} catch (Exception e) {
				e.printStackTrace();
				throw new WebApplicationException("Could not serialize style.", 505);
			}

		} else {

			try {
				return styleSerializer.serializeStyle(getLexicon().getAllVisualProperties(), style);
			} catch (IOException e) {
				e.printStackTrace();
				throw new WebApplicationException(e, 500);
			}
		}
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void createStyle(InputStream is) {
		final ObjectMapper objMapper = new ObjectMapper();
		JsonNode rootNode;
		try {
			rootNode = objMapper.readValue(is, JsonNode.class);
			VisualStyle style = this.visualStyleMapper.buildVisualStyle(factoryManager, vsFactory, getLexicon(),
					rootNode);
			vmm.addVisualStyle(style);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e, 500);
		}
	}


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
			e.printStackTrace();
			throw new WebApplicationException(e, 500);
		}
	}


	@PUT
	@Path("/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void updateStyleTitle(@PathParam("name") String name, InputStream is) {
		final VisualStyle style = getStyleByName(name);
		final ObjectMapper objMapper = new ObjectMapper();
		JsonNode rootNode;
		try {
			rootNode = objMapper.readValue(is, JsonNode.class);
			this.visualStyleMapper.updateStyleName(style, getLexicon(), rootNode);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e, 500);
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