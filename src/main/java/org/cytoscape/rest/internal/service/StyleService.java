package org.cytoscape.rest.internal.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.rest.internal.CyActivator.WriterListener;
import org.cytoscape.rest.internal.serializer.VisualStyleModule;
import org.cytoscape.rest.internal.serializer.VisualStyleSerializer;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.Visualizable;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.TaskMonitor;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
	
	private final ObjectMapper styleMapper;
	
	
	public StyleService() {
		super();
		this.styleMapper = new ObjectMapper();
		this.styleMapper.registerModule(new VisualStyleModule());
	}
	
	private final VisualStyle getStyleByName(final String name) {
		final Collection<VisualStyle> styles = vmm.getAllVisualStyles();
		for(final VisualStyle style: styles) {
			if(style.getTitle().equals(name)) {
				return style;
			}
		}

		throw new NotFoundException("Could not find Visual Style: " + name);
	}
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStyleNames() {
		final Collection<VisualStyle> styles = vmm.getAllVisualStyles();
		final List<String> styleNames = new ArrayList<String>();
		for(final VisualStyle style: styles) {
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


	private final VisualLexicon getLexicon() {
		final Set<VisualLexicon> lexicon = this.vmm.getAllVisualLexicon();
		if(lexicon.isEmpty()) {
			throw new WebApplicationException("Could not find lexicon.", 500);
		}
		return lexicon.iterator().next();
	}
	@GET
	@Path("/{name}/{type}/defaults")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDefaults(@PathParam("name") String name, @PathParam("type") String type) {
		return getVp(name, type, null);
	}


	@GET
	@Path("/{name}/{type}/defaults/{vpName}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDefaultValue(@PathParam("name") String name, @PathParam("type") String type, @PathParam("vpName") String vpName) {
		return getVp(name, type, vpName);
	}


	@GET
	@Path("/{name}/{type}/mappings")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMappings(@PathParam("name") String name) {
		System.out.println("############# MAPPINGS");
		final VisualStyle style = getStyleByName(name);
		final Collection<VisualMappingFunction<?, ?>> mappings = style.getAllVisualMappingFunctions();
		try {
			return styleMapper.writeValueAsString(mappings);
		} catch (JsonProcessingException e) {
			throw new WebApplicationException(e, 500);
		}
	}


	@GET
	@Path("/{name}/{type}/mappings/{vp}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMapping(@PathParam("name") String name, @PathParam("type") String type, @PathParam("vp") String vp) {
		final VisualStyle style = getStyleByName(name);
		final VisualLexicon lexicon = getLexicon();
		VisualProperty<?> visualProp;
		if(type.equals("node")) {
			visualProp = lexicon.lookup(CyNode.class, vp);
		} else if(type.equals("edge")) {
			visualProp = lexicon.lookup(CyEdge.class, vp);
		} else if(type.equals("network")) {
			visualProp = lexicon.lookup(CyNetwork.class, vp);
		} else {
			throw new WebApplicationException("Incompatible data type.", 500);
		}
		final VisualMappingFunction<?, ?> mapping = style.getVisualMappingFunction(visualProp);
		try {
			return styleMapper.writeValueAsString(mapping);
		} catch (JsonProcessingException e) {
			throw new WebApplicationException(e, 500);
		}
	}
	
	private final String getVp(String name, String type, String vpName ) {
		
		final VisualStyle style = getStyleByName(name);
		final VisualLexicon lexicon = getLexicon();
		VisualProperty<?> vpClass = null;
		if(type.equals("node")) {
			vpClass = BasicVisualLexicon.NODE;
		} else if(type.equals("edge")) {
			vpClass = BasicVisualLexicon.EDGE;
		} else if(type.equals("network")) {
			vpClass = BasicVisualLexicon.NETWORK;
		} else {
			throw new WebApplicationException("Incompatible data type.", 500);
		}
		final Collection<VisualProperty<?>> vps;
		if(vpName == null) {
			vps = lexicon.getAllDescendants(vpClass);
		} else {
			vps = new HashSet<VisualProperty<?>>();
			vps.add(lexicon.lookup(vpClass.getTargetDataType(), vpName));
		}
		try {
			return styleSerializer.serializeDefaults(vps, style);
		} catch (IOException e) {
			throw new WebApplicationException(e, 500);
		}
	}
	
	
	///////////// Update Default Values ///////////////
	
	@PUT
	@Path("/{name}/{type}/defaults")
	@Produces(MediaType.APPLICATION_JSON)
	public void updateDefaults(@PathParam("name") String name, @PathParam("type") String type, InputStream is) {
		final VisualStyle style = getStyleByName(name);
		final Set<VisualLexicon> lexicon = this.vmm.getAllVisualLexicon();
		if(lexicon.isEmpty()) {
			throw new WebApplicationException("Could not find lexicon.", 500);
		}
		final VisualLexicon lex = lexicon.iterator().next();
		
		Class<?> objectType;
		if(type.equals("node")) {
			objectType = CyNode.class;
		} else if(type.equals("edge")) {
			objectType = CyEdge.class;
		} else if(type.equals("network")) {
			objectType = CyNetwork.class;
		} else {
			throw new WebApplicationException("Incompatible data type.", 500);
		}
		
		final ObjectMapper objMapper = new ObjectMapper();
		try {
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
			updateVisualProperties(rootNode, style, lex, objectType);
		} catch (JsonParseException e) {
			throw new WebApplicationException(e, 500);
		} catch (JsonMappingException e) {
			throw new WebApplicationException(e, 500);
		} catch (IOException e) {
			throw new WebApplicationException(e, 500);
		}
	}
	
	private final void updateVisualProperties(final JsonNode rootNode, VisualStyle style, VisualLexicon lexicon, Class<?> type) {
		final Iterator<String> fieldNames = rootNode.fieldNames();

		while (fieldNames.hasNext()) {
			final String fieldName = fieldNames.next();
			final VisualProperty<Object> vp = (VisualProperty<Object>) lexicon.lookup(type, fieldName);
			if(vp == null) {
				continue;
			}
			style.setDefaultValue(vp, vp.parseSerializableString(rootNode.get(fieldName).asText()));
		}
	}


//	@GET
//	@Path("/{name}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public String getStyle(@PathParam("name") String name) {
//		final VisualStyle style = getStyleByName(name);
//		final Set<VisualStyle> styles = new HashSet<VisualStyle>();
//		styles.add(style);
//		
//		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
//		CyWriter writer = this.writerListener.getFactory().createWriter(stream, styles);
//		String jsonString = null;
//		try {
//			writer.run(tm);
//			jsonString = stream.toString();
//			stream.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return jsonString;
//	}
}
