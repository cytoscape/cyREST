package org.cytoscape.rest.internal.serializer;

import java.io.IOException;

import org.cytoscape.view.vizmap.mappings.ContinuousMapping;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@SuppressWarnings("rawtypes")
public class ContinuousMappingSerializer extends JsonSerializer<ContinuousMapping> {

	@Override
	public Class<ContinuousMapping> handledType() {
		return ContinuousMapping.class;
	}

	@Override
	public void serialize(ContinuousMapping mapping, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.useDefaultPrettyPrinter();
		
		jgen.writeStartObject();
		jgen.writeStringField("mapping_type", mapping.getClass().getSimpleName());
		jgen.writeStringField("mappingColumn", mapping.getMappingColumnName());
		jgen.writeStringField("mappingColumnType", mapping.getMappingColumnType().getSimpleName());
		jgen.writeStringField("visualProperty", mapping.getVisualProperty().getIdString());
		jgen.writeEndObject();
	}
}
