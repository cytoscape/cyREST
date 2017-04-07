package org.cytoscape.rest.internal.serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.rest.internal.datamapper.VisualStyleMapper;
import org.cytoscape.view.model.DiscreteRange;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.values.VisualPropertyValue;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualPropertyDependency;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;

public class VisualStyleSerializer {

	private final DiscreteMappingSerializer discSerializer = new DiscreteMappingSerializer();
	private final PassthroughMappingSerializer passhthroughSerializer = new PassthroughMappingSerializer();
	private final ContinuousMappingSerializer continuousSerializer = new ContinuousMappingSerializer();

	public final String serializeDefaults(final Collection<VisualProperty<?>> vps, final VisualStyle style) throws IOException {
		final JsonFactory factory = new JsonFactory();
		
		// Sort by field name
		final SortedMap<String, VisualProperty<?>> names = new TreeMap<String, VisualProperty<?>>();
		for(final VisualProperty<?> vp:vps) {
			names.put(vp.getIdString(), vp);
		}
		

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = factory.createGenerator(stream);
		generator.useDefaultPrettyPrinter();
		
		generator.writeStartObject();
		addDefaults(generator, names, style);
		generator.writeEndObject();
		
		generator.close();
		result = stream.toString("UTF-8");
		stream.close();
		return result;
	}

	/*
	public final String serializeVisualProperties(final Set<VisualProperty<?>> vps) throws IOException {
		final JsonFactory factory = new JsonFactory();
		
		// Sort by field name
		final SortedMap<String, VisualProperty<Object>> names = new TreeMap<>();
		for(final VisualProperty vp:vps) {
			names.put(vp.getIdString(), vp);
		}
		

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = factory.createGenerator(stream);
		generator.useDefaultPrettyPrinter();
		
		generator.writeStartArray();
		for(final String vpName:names.keySet()) {
			final VisualProperty<Object> vp = names.get(vpName);
			serializeVisualProperty(generator, vp);
		}
		generator.writeEndArray();
		
		generator.close();
		result = stream.toString("UTF-8");
		stream.close();
		return result;
	}
	*/


	public final String serializeVisualProperty(final VisualProperty vp) throws IOException {
		final JsonFactory factory = new JsonFactory();

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = factory.createGenerator(stream);
		generator.useDefaultPrettyPrinter();
	
		serializeVisualProperty(generator, vp);
		generator.close();
		result = stream.toString("UTF-8");
		stream.close();
		return result;
	}
	
	private final void serializeVisualProperty(final JsonGenerator generator, final VisualProperty<Object> vp) throws IOException {
		generator.writeStartObject();
		generator.writeStringField("visualProperty", vp.getIdString());
		generator.writeStringField("name", vp.getDisplayName());
		generator.writeStringField("targetDataType", vp.getTargetDataType().getSimpleName());
		generator.writeStringField("default", vp.toSerializableString(vp.getDefault()));
		generator.writeEndObject();
	}


	public final String serializeDefault(final VisualProperty<Object> vp, final VisualStyle style) throws IOException {
		final JsonFactory factory = new JsonFactory();

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = factory.createGenerator(stream);
		generator.useDefaultPrettyPrinter();
		
		generator.writeStartObject();
		generator.writeStringField(VisualStyleMapper.MAPPING_VP, vp.getIdString());
		generator.writeFieldName("value");
		
		Object value = style.getDefaultValue(vp);
		if(value == null) {
			// Use VP default is Style default is not available
			value = vp.getDefault();
		}
		writeValue(vp, value, generator);
		generator.writeEndObject();
		
		generator.close();
		result = stream.toString("UTF-8");
		stream.close();
		return result;
	}


	public final String serializeDiscreteRange(final VisualProperty<Object> vp, final DiscreteRange<Object> range) throws IOException {
		final JsonFactory factory = new JsonFactory();

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = factory.createGenerator(stream);
		generator.useDefaultPrettyPrinter();
		
		generator.writeStartObject();
		
		generator.writeStringField(VisualStyleMapper.MAPPING_VP, vp.getIdString());
		generator.writeArrayFieldStart("values");
		for(Object obj: range.values()) {
			VisualPropertyValue vpv = (VisualPropertyValue) obj;
			generator.writeString(vpv.getSerializableString());
		}
		generator.writeEndArray();
		
		generator.writeEndObject();
		
		generator.close();
		result = stream.toString("UTF-8");
		stream.close();
		return result;
	}

	public final String serializeStyle(final Collection<VisualProperty<?>> vps, final VisualStyle style) throws IOException {
		final SortedMap<String, VisualProperty<?>> names = new TreeMap<String, VisualProperty<?>>();
		for(final VisualProperty<?> vp:vps) {
			names.put(vp.getIdString(), vp);
		}
		
		final JsonFactory factory = new JsonFactory();
		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = factory.createGenerator(stream);
		generator.useDefaultPrettyPrinter();
		
		generator.writeStartObject();
		generator.writeStringField("title", style.getTitle());
		addDefaults(generator, names, style);
		addMappings(generator, style);
		generator.writeEndObject();
		
		generator.close();
		result = stream.toString("UTF-8");
		stream.close();
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private void addDefaults(JsonGenerator generator, final SortedMap<String, VisualProperty<?>> names, VisualStyle style) throws IOException {
		generator.writeArrayFieldStart("defaults");
		for(final String name:names.keySet()) {
			final VisualProperty<Object> vp = (VisualProperty<Object>) names.get(name);
			Object newValue = style.getDefaultValue(vp);
			if(newValue == null) {
				newValue = vp.getDefault();
			}
			generator.writeStartObject();
			generator.writeStringField(VisualStyleMapper.MAPPING_VP, vp.getIdString());
			generator.writeFieldName("value");
			writeValue(vp, newValue, generator);
			generator.writeEndObject();
		}
		generator.writeEndArray();
	}
	
	
	@SuppressWarnings("rawtypes")
	private void addMappings(JsonGenerator generator, VisualStyle style) throws JsonProcessingException, IOException {
		// Mappings
		generator.writeArrayFieldStart("mappings");
		for(VisualMappingFunction mapping:style.getAllVisualMappingFunctions()) {
			if(mapping instanceof DiscreteMapping) {
				discSerializer.serialize((DiscreteMapping) mapping, generator, null);
			} else if(mapping instanceof ContinuousMapping) {
				continuousSerializer.serialize((ContinuousMapping) mapping, generator, null);
			} else if(mapping instanceof PassthroughMapping) {
				passhthroughSerializer.serialize((PassthroughMapping) mapping, generator, null);
			}
		}
		generator.writeEndArray();
	}
	
	private final void writeValue(final VisualProperty<Object> vp, final Object value, final JsonGenerator generator)
			throws IOException {
		if(value == null) {
			return;
		}
		
		final Class<?> type = vp.getRange().getType();
		
		if (type == String.class) {
			generator.writeString(value.toString());
		} else if (type == Boolean.class) {
			generator.writeBoolean((Boolean) value);
		} else if (type == Double.class) {
			generator.writeNumber((Double) value);
		} else if (type == Integer.class) {
			generator.writeNumber((Integer) value);
		} else if (type == Long.class) {
			generator.writeNumber((Long) value);
		} else if (type == Float.class) {
			generator.writeNumber((Double) value);
		} else {
			generator.writeString(vp.toSerializableString(value));
		}
	}
	
	
	public final String serializeViews(final Collection<? extends View<? extends CyIdentifiable>> views, 
			final Collection<VisualProperty<?>> visualProperties) throws IOException {
		final SortedMap<String, VisualProperty<?>> names = new TreeMap<String, VisualProperty<?>>();
		// Sort by field name
		for(final VisualProperty<?> vp:visualProperties) {
			names.put(vp.getIdString(), vp);
		}
		
		final JsonFactory factory = new JsonFactory();

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = factory.createGenerator(stream);
		generator.useDefaultPrettyPrinter();
	
		generator.writeStartArray();
		for(final View<? extends CyIdentifiable> view:views) {
			
			generator.writeStartObject();
			generator.writeNumberField(CyIdentifiable.SUID, view.getModel().getSUID());
			generator.writeArrayFieldStart("view");
			
			addKeyValuePair(generator, names, view);
			
			generator.writeEndArray();
			
			generator.writeEndObject();
			
		}
		generator.writeEndArray();
		
		generator.close();
		result = stream.toString("UTF-8");
		stream.close();
		return result;
	}
	
	
	public final String serializeView(final View<? extends CyIdentifiable> view, final Collection<VisualProperty<?>> visualProperties) throws IOException {

		final SortedMap<String, VisualProperty<?>> names = new TreeMap<String, VisualProperty<?>>();
		// Sort by field name
		for(final VisualProperty<?> vp:visualProperties) {
			names.put(vp.getIdString(), vp);
		}
		
		final JsonFactory factory = new JsonFactory();

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = factory.createGenerator(stream);
		generator.useDefaultPrettyPrinter();
		
		generator.writeStartArray();
		addKeyValuePair(generator, names, view);
		generator.writeEndArray();
		
		generator.close();
		result = stream.toString("UTF-8");
		stream.close();
		return result;
	}
	
	
	public final String serializeSingleVisualProp(final View<? extends CyIdentifiable> view, 
			final VisualProperty<?> vp) throws IOException {
		final SortedMap<String, VisualProperty<?>> names = new TreeMap<>();
		names.put(vp.getIdString(), vp);
		
		final JsonFactory factory = new JsonFactory();

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = factory.createGenerator(stream);
		generator.useDefaultPrettyPrinter();
		
		generator.writeStartObject();
		generator.writeStringField(VisualStyleMapper.MAPPING_VP, vp.getIdString());
		generator.writeFieldName("value");
		writeValue((VisualProperty<Object>) vp, view.getVisualProperty(vp), generator);
		generator.writeEndObject();
		
		generator.close();
		result = stream.toString("UTF-8");
		stream.close();
		return result;
	}
	
	
	private final void addKeyValuePair(final JsonGenerator generator, final SortedMap<String, VisualProperty<?>> names,
			final View<? extends CyIdentifiable> view) throws IOException {
		
		for(final String name:names.keySet()) {
			final VisualProperty<Object> vp = (VisualProperty<Object>) names.get(name);
			generator.writeStartObject();
			generator.writeStringField(VisualStyleMapper.MAPPING_VP, vp.getIdString());
			generator.writeFieldName("value");
			writeValue(vp, view.getVisualProperty(vp), generator);
			generator.writeEndObject();
		}
	}


	public final String serializeDependecies(final Set<VisualPropertyDependency<?>> dependencies) throws IOException {

		// Sort by field name
		final SortedMap<String, VisualPropertyDependency<?>> names = new TreeMap<String, VisualPropertyDependency<?>>();
		for(final VisualPropertyDependency<?> dep:dependencies) {
			names.put(dep.getIdString(), dep);
		}
		
		final JsonFactory factory = new JsonFactory();

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = factory.createGenerator(stream);
		generator.useDefaultPrettyPrinter();
		
		generator.writeStartArray();
		for(String key: names.keySet()) {
			addDependency(generator, names.get(key));
		}
		generator.writeEndArray();
		
		generator.close();
		result = stream.toString("UTF-8");
		stream.close();
		return result;
	}
	
	private final void addDependency(final JsonGenerator generator, final VisualPropertyDependency<?> dep) throws IOException {
		generator.writeStartObject();
		
		generator.writeStringField(VisualStyleMapper.VP_DEPENDENCY, dep.getIdString());
		generator.writeBooleanField("enabled", dep.isDependencyEnabled());
		
		generator.writeEndObject();
		
	}
}
