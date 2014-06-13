package org.cytoscape.rest.internal.serializer;

import java.io.IOException;
import java.util.Map;

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
		jgen.writeStringField("mapping_type", "discrete");
		jgen.writeStringField("mappingColumn", mapping.getMappingColumnName());
		jgen.writeStringField("mappingColumnType", mapping.getMappingColumnType().getSimpleName());
		jgen.writeStringField("visualProperty", mapping.getVisualProperty().getIdString());
		final Map valueMap = mapping.getAll();
		jgen.writeArrayFieldStart("map");
		for(Object key:valueMap.keySet()) {
			jgen.writeStringField((String) key, mapping.getVisualProperty().toSerializableString(valueMap.get(key)));
		}
		jgen.writeEndArray();
		jgen.writeEndObject();
	}
}