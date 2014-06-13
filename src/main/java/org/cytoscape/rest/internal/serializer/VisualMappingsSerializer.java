package org.cytoscape.rest.internal.serializer;

import java.io.IOException;
import java.util.Collection;

import org.cytoscape.view.vizmap.VisualMappingFunction;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class VisualMappingsSerializer extends JsonSerializer<Collection<VisualMappingFunction<?,?>>> {

	@Override
	public void serialize(Collection<VisualMappingFunction<?,?>> mappings, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {

		jgen.writeStartArray();
		for(final VisualMappingFunction<?,?> mapping:mappings) {
			jgen.writeObject(mapping);
		}
		jgen.writeEndArray();
	}

}
