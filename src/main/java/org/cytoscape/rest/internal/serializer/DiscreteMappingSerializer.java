package org.cytoscape.rest.internal.serializer;

import java.io.IOException;
import java.util.Map;

import org.cytoscape.rest.internal.datamapper.VisualStyleMapper;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@SuppressWarnings("rawtypes")
public class DiscreteMappingSerializer extends JsonSerializer<DiscreteMapping> {

	@Override
	public Class<DiscreteMapping> handledType() {
		return DiscreteMapping.class;
	}

	@Override
	public void serialize(DiscreteMapping mapping, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.useDefaultPrettyPrinter();
		
		jgen.writeStartObject();
		
		jgen.writeStringField(VisualStyleMapper.MAPPING_TYPE, "discrete");
		jgen.writeStringField(VisualStyleMapper.MAPPING_COLUMN, mapping.getMappingColumnName());
		jgen.writeStringField(VisualStyleMapper.MAPPING_COLUMN_TYPE, mapping.getMappingColumnType().getSimpleName());
		jgen.writeStringField(VisualStyleMapper.MAPPING_VP, mapping.getVisualProperty().getIdString());
		
		serializeMapping(mapping, jgen);
		
		jgen.writeEndObject();
	}


	@SuppressWarnings("unchecked")
	private final void serializeMapping(final DiscreteMapping mapping, JsonGenerator jgen) throws IOException {
		final Map valueMap = mapping.getAll();
	
		final VisualProperty<Object> vp = mapping.getVisualProperty();
		
		jgen.writeArrayFieldStart("map");
		
		for(final Object key:valueMap.keySet()) {
			final Object value = valueMap.get(key);
			jgen.writeStartObject();
			jgen.writeStringField("key", key.toString());
			jgen.writeStringField("value", vp.toSerializableString(value));
			jgen.writeEndObject();
		}
		jgen.writeEndArray();
	}
}