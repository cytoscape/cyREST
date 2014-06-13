package org.cytoscape.rest.internal.serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualStyle;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class VisualStyleSerializer {

	public final String serializeDefaults(final Collection<VisualProperty<?>> vps, final VisualStyle style) throws IOException {

		// Sort by field name
		final SortedMap<String, VisualProperty<?>> names = new TreeMap<String, VisualProperty<?>>();
		for(final VisualProperty<?> vp:vps) {
			names.put(vp.getIdString(), vp);
		}
		
		final JsonFactory factory = new JsonFactory();

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = null;
		generator = factory.createGenerator(stream);
		generator.writeStartObject();
		for(final String name:names.keySet()) {
			final VisualProperty<Object> vp = (VisualProperty<Object>) names.get(name);
			generator.writeFieldName(name);
			writeValue(vp, style.getDefaultValue(vp), generator);
		}
		generator.writeEndObject();
		generator.close();
		result = stream.toString();
		stream.close();
		return result;
	}
	
	public final String serializeMappings(final Collection<VisualMappingFunction<?, ?>> mappings, final VisualStyle style) throws IOException {
		final JsonFactory factory = new JsonFactory();

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = null;
		generator = factory.createGenerator(stream);
		generator.writeStartObject();
		for(final VisualMappingFunction<?, ?> mapping:mappings) {
		}
		generator.writeEndObject();
		generator.close();
		result = stream.toString();
		stream.close();
		return result;
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
}
