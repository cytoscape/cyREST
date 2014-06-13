package org.cytoscape.rest.internal.serializer;

import java.io.IOException;

import org.cytoscape.view.vizmap.VisualStyle;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class StyleSerializer extends JsonSerializer<VisualStyle> {

	@Override
	public Class<VisualStyle> handledType() {
		return VisualStyle.class;
	}

	@Override
	public void serialize(VisualStyle style, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.useDefaultPrettyPrinter();

		jgen.writeStartObject();
		Object lexicon = provider.getConfig().getAttributes().getAttribute("lexicon");	
		
		jgen.writeEndObject();
	}
	
	private final void serializeDefaults(final VisualStyle style, final JsonGenerator jgen) {
	}

}
