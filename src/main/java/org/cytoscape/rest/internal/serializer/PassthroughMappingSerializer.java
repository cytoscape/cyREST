package org.cytoscape.rest.internal.serializer;

import java.io.IOException;

import org.cytoscape.rest.internal.datamapper.VisualStyleMapper;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@SuppressWarnings("rawtypes")
public class PassthroughMappingSerializer extends JsonSerializer<PassthroughMapping> {

	@Override
	public Class<PassthroughMapping> handledType() {
		return PassthroughMapping.class;
	}

	@Override
	public void serialize(PassthroughMapping mapping, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.useDefaultPrettyPrinter();
		jgen.writeStartObject();
		jgen.writeStringField(VisualStyleMapper.MAPPING_TYPE, "passthrough");
		jgen.writeStringField(VisualStyleMapper.MAPPING_COLUMN, mapping.getMappingColumnName());
		jgen.writeStringField(VisualStyleMapper.MAPPING_COLUMN_TYPE, mapping.getMappingColumnType().getSimpleName());
		jgen.writeStringField(VisualStyleMapper.MAPPING_VP, mapping.getVisualProperty().getIdString());
		jgen.writeEndObject();
	}
}
