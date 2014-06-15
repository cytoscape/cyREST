package org.cytoscape.rest.internal.serializer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class RowSerializer extends JsonSerializer<CyRow> {

	private static final Pattern REPLACE_INVALID_JS_CHAR_PATTERN = Pattern.compile("^[^a-zA-Z_]+|[^a-zA-Z_0-9]+");


	@Override
	public void serialize(final CyRow row, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {

		final CyTable table = row.getTable();
		final Map<String, Object> cells = row.getAllValues();
		final Set<String> columnNames = cells.keySet();
	
		jgen.writeStartObject();
		
		for(final String columnName : columnNames) {
			final Object value = cells.get(columnName);
			// Skip if there is no value.
			if (value == null)
				continue;

			Class<?> type = table.getColumn(columnName).getType();
			if (type == List.class) {
				type = table.getColumn(columnName).getListElementType();
				writeList(type, columnName, (List<?>) value, jgen);
			} else {
				jgen.writeFieldName(replaceColumnName(columnName));
				writeValue(type, value, jgen);
			}
		}
		
		jgen.writeEndObject();
	}


	private final void writeList(final Class<?> type, String columnName, List<?> values, JsonGenerator jgen)
			throws JsonGenerationException, IOException {
		
		jgen.writeArrayFieldStart(replaceColumnName(columnName));
		for (final Object value : values) {
			writeValue(type, value, jgen);
		}
		jgen.writeEndArray();
	}
	
	private final String replaceColumnName(final String columnName) {
		final Matcher matcher = REPLACE_INVALID_JS_CHAR_PATTERN.matcher(columnName);
		return matcher.replaceAll("_");
	}

	private final void writeValue(final Class<?> type, Object value, JsonGenerator jgen)
			throws JsonGenerationException, IOException {
		if (type == String.class) {
			jgen.writeString(value.toString());
		} else if (type == Boolean.class) {
			jgen.writeBoolean((Boolean) value);
		} else if (type == Double.class) {
			jgen.writeNumber((Double) value);
		} else if (type == Integer.class) {
			jgen.writeNumber((Integer) value);
		} else if (type == Long.class) {
			jgen.writeNumber((Long) value);
		} else if (type == Float.class) {
			jgen.writeNumber((Double) value);
		}
	}
	
	@Override
	public Class<CyRow> handledType() {
		return CyRow.class;
	}
}