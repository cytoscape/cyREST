package org.cytoscape.rest.internal.serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyRow;
import org.cytoscape.rest.internal.resource.JsonTags;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

public class GraphObjectSerializer {

	private static final Pattern REPLACE_INVALID_JS_CHAR_PATTERN = Pattern.compile("^[^a-zA-Z_]+|[^a-zA-Z_0-9]+");

	public final String serializeGraphObject(final CyIdentifiable obj, final CyRow row) throws IOException {

		final JsonFactory factory = new JsonFactory();

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = null;
		generator = factory.createGenerator(stream);
		generator.writeStartObject();
		generator.writeObjectFieldStart("data");
		serializeRow(generator, obj, row);
		generator.writeEndObject();
		generator.writeEndObject();
		generator.close();
		result = stream.toString("UTF-8");
		stream.close();
		return result;
	}

	public final String serializeRow(final CyRow row) throws IOException {

		final JsonFactory factory = new JsonFactory();

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = null;
		generator = factory.createGenerator(stream);
		generator.writeStartObject();
		serializeSingleRow(generator, row);
		generator.writeEndObject();
		generator.close();
		result = stream.toString("UTF-8");
		stream.close();
		return result;
	}

	public final String serializeCell(final CyRow row, final String columnName) throws IOException {

		final CyColumn column = row.getTable().getColumn(columnName);
		if (column == null) {
			throw new IOException("No such column: " + columnName);
		}

		final Object value = row.get(columnName, column.getType());
		final JsonFactory factory = new JsonFactory();

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = null;
		generator = factory.createGenerator(stream);
		generator.writeStartObject();
		serializeCell(generator, column, value);
		generator.writeEndObject();
		generator.close();
		result = stream.toString("UTF-8");
		stream.close();
		return result;
	}

	public final String serializeAllRows(final Collection<CyRow> rows) throws IOException {

		final JsonFactory factory = new JsonFactory();

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = null;
		generator = factory.createGenerator(stream);

		generator.writeStartArray();
		for (final CyRow row : rows) {
			generator.writeStartObject();
			serializeSingleRow(generator, row);
			generator.writeEndObject();
		}
		generator.writeEndArray();

		generator.close();
		result = stream.toString("UTF-8");
		stream.close();
		return result;
	}


	public final String serializeColumns(final Collection<CyColumn> columns) throws IOException {

		final JsonFactory factory = new JsonFactory();

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = null;
		generator = factory.createGenerator(stream);

		generator.writeStartArray();
		for (final CyColumn column : columns) {
			generator.writeStartObject();
			generator.writeStringField(JsonTags.COLUMN_NAME, column.getName());
			generator.writeStringField(JsonTags.COLUMN_TYPE, column.getType().getSimpleName());
			generator.writeBooleanField(JsonTags.COLUMN_IMMUTABLE, column.isImmutable());
			generator.writeBooleanField(JsonTags.PRIMARY_KEY, column.isPrimaryKey());
			generator.writeEndObject();
		}
		generator.writeEndArray();

		generator.close();
		result = stream.toString("UTF-8");
		stream.close();
		return result;
	}

	public final String serializeColumnValues(final CyColumn column, final Collection<Object> values) throws IOException {

		final JsonFactory factory = new JsonFactory();

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = null;
		generator = factory.createGenerator(stream);

		try {
		generator.writeStartObject();
		generator.writeStringField(JsonTags.COLUMN_NAME, column.getName());
		generator.writeFieldName(JsonTags.COLUMN_VALUES);
		generator.writeStartArray();
		for (final Object value : values) {
			if (column.getType() == List.class) {
				writeList(column.getListElementType(), (List<?>) value, generator);
			} else {
				writeValue(column.getType(), value, generator);
			}
			writeValue(column.getType(), value, generator);
		}
		generator.writeEndArray();
		generator.writeEndObject();

		generator.close();
		result = stream.toString("UTF-8");
		stream.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	private final void serializeSingleRow(final JsonGenerator generator, final CyRow row) throws IOException {
		final Collection<CyColumn> columns = row.getTable().getColumns();
		final Map<String, Object> values = row.getAllValues();
		for (final CyColumn col : columns) {
			final Object value = values.get(col.getName());
			if (value == null)
				continue;
			Class<?> type = col.getType();
			final String columnName = col.getName();
			generator.writeFieldName(replaceColumnName(columnName));
			if (type == List.class) {
				writeList(col.getListElementType(), (List<?>) value, generator);
			} else {
				writeValue(type, value, generator);
			}
		}
	}

	private final void serializeRow(JsonGenerator generator, final CyIdentifiable obj, final CyRow row)
			throws IOException {
		final Collection<CyColumn> columns = row.getTable().getColumns();
		final Map<String, Object> values = row.getAllValues();
		if (obj instanceof CyEdge) {
			final Long sourceId = ((CyEdge) obj).getSource().getSUID();
			final Long targetId = ((CyEdge) obj).getSource().getSUID();
			generator.writeNumberField("source", sourceId);
			generator.writeNumberField("target", targetId);
		} else {
			generator.writeNumberField("id", obj.getSUID());
		}
		for (final CyColumn col : columns) {
			final Object value = values.get(col.getName());
			if (value == null)
				continue;

			Class<?> type = col.getType();
			final String columnName = col.getName();
			generator.writeFieldName(replaceColumnName(columnName));
			if (type == List.class) {
				writeList(col.getListElementType(), (List<?>) value, generator);
			} else {
				writeValue(type, value, generator);
			}
		}
	}

	private final void serializeCell(final JsonGenerator generator, final CyColumn col, Object value)
			throws IOException {
		Class<?> type = col.getType();
		final String columnName = col.getName();
		generator.writeFieldName(replaceColumnName(columnName));
		if (type == List.class) {
			writeList(col.getListElementType(), (List<?>) value, generator);
		} else {
			writeValue(type, value, generator);
		}
	}

	private final void writeList(final Class<?> type, final List<?> values, final JsonGenerator jgen)
			throws JsonGenerationException, IOException {
		
		if(values == null) {
			return;
		}
		
		jgen.writeStartArray();
		for (final Object value : values)
			writeValue(type, value, jgen);
		jgen.writeEndArray();
	}

	private final String replaceColumnName(final String columnName) {
		final Matcher matcher = REPLACE_INVALID_JS_CHAR_PATTERN.matcher(columnName);
		return matcher.replaceAll("_");
	}

	private final void writeValue(final Class<?> type, final Object value, final JsonGenerator generator)
			throws IOException {
		if(value == null) {
			return;
		}
		
		if (type == String.class) {
			generator.writeString(value.toString());
		} else if (type == Boolean.class) {
			generator.writeBoolean((Boolean) value);
		} else if (type == Double.class) {
			generator.writeNumber((Double) value);
		} else if (type == Integer.class) {
			generator.writeNumber((Integer) value);
		} else if (type == Long.class) {
			generator.writeNumber((Long) value);
		} else if (type == Float.class) {
			generator.writeNumber((Double) value);
		}
	}
}