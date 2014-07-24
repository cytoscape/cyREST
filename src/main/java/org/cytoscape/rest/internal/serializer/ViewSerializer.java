package org.cytoscape.rest.internal.serializer;

import java.io.IOException;
import java.util.Collection;

import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@SuppressWarnings("rawtypes")
public class ViewSerializer extends JsonSerializer<View> {

	private Collection<VisualProperty<Object>> vps;

	public ViewSerializer(Collection<VisualProperty<Object>> vps) {
		this.vps = vps;
	}

	@Override
	public void serialize(View view, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.useDefaultPrettyPrinter();

		jgen.writeObjectFieldStart("view");
		for (final VisualProperty<Object> vp : vps) {
			final Object value = view.getVisualProperty(vp);
			String strValue = vp.toSerializableString(value);
			jgen.writeStringField(vp.getIdString(), strValue);
		}
		jgen.writeEndObject();
	}
}