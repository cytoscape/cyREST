package org.cytoscape.rest.internal.datamapper;

import java.io.IOException;

import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualStyle;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JsVisualStyleSerializer extends JsonSerializer<VisualStyle> {

	@Override
	public void serialize(final VisualStyle vs, JsonGenerator jg, SerializerProvider provider) throws IOException,
			JsonProcessingException {

		jg.useDefaultPrettyPrinter();

		jg.writeStartObject();
		
		// Title of Visual Style
		jg.writeStringField("title", vs.getTitle());
		
		// Mappings and Defaults are stored as array.
		jg.writeArrayFieldStart("style");
		
		// Node Mapping
		jg.writeStartObject();
		
		jg.writeStringField("selector", "node");
		
		jg.writeObjectFieldStart("css");
		jg.writeStringField("background-color", vs.getDefaultValue(BasicVisualLexicon.NODE_FILL_COLOR).toString());
		jg.writeEndObject();
		
		jg.writeEndObject();
		
		jg.writeEndArray();
		
		jg.writeEndObject();
	}
	
	@Override
	public Class<VisualStyle> handledType() {
		return VisualStyle.class;
	}
}