package org.cytoscape.rest.internal.datamapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class RowSerializer extends JsonSerializer<CyRow> {

	@Override
	public void serialize(final CyRow row, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {

		final CyTable table = row.getTable();
		final Map<String, Object> values = row.getAllValues();

		for (final String key : values.keySet()) {
			final Object value = values.get(key);
			if (value == null)
				continue;

			Class<?> type = table.getColumn(key).getType();
			if (type == List.class) {
				type = table.getColumn(key).getListElementType();
				writeList(type, key, (List<?>) value, jgen);
			} else {
				write(type, key, value, jgen);
			}
		}
	}

	private void writeList(final Class<?> type, String fieldName, List<?> values, JsonGenerator jgen)
			throws JsonGenerationException, IOException {

		jgen.writeFieldName(fieldName);
		jgen.writeStartArray();

		for (Object value : values)
			writeValue(type, value, jgen);

		jgen.writeEndArray();
	}

	private void write(final Class<?> type, String fieldName, Object value, JsonGenerator jgen)
			throws JsonGenerationException, IOException {
		jgen.writeFieldName(fieldName);
		writeValue(type, value, jgen);
	}

	private final void writeValue(final Class<?> type, Object value, JsonGenerator jgen)
			throws JsonGenerationException, IOException {
		jgen.writeStartObject();
		if (type == String.class) {
			jgen.writeStringField("type", "string");
			jgen.writeStringField("value", (String) value);
		} else if (type == Boolean.class) {
			jgen.writeStringField("type", "boolean");
			jgen.writeBooleanField("value", (Boolean) value);
		} else if (type == Double.class) {
			jgen.writeStringField("type", "double");
			jgen.writeNumberField("value", (Double) value);
		} else if (type == Integer.class) {
			jgen.writeStringField("type", "integer");
			jgen.writeNumberField("value", (Integer) value);
		} else if (type == Long.class) {
			jgen.writeStringField("type", "long");
			jgen.writeNumberField("value", (Long) value);
		} else if (type == Float.class) {
			// Handle float as double.
			jgen.writeStringField("type", "double");
			jgen.writeNumberField("value", (Float) value);
		}
		jgen.writeEndObject();
	}

	public Class<CyRow> handledType() {
		return CyRow.class;
	}
}