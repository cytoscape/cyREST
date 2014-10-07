package org.cytoscape.rest.internal.serializer;

import java.io.IOException;
import java.util.List;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.rest.internal.resource.JsonTags;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class TableSerializer extends JsonSerializer<CyTable> {

	@Override
	public Class<CyTable> handledType() {
		return CyTable.class;
	}

	@Override
	public void serialize(CyTable table, JsonGenerator generator, SerializerProvider provider) throws IOException,
			JsonProcessingException {
		generator.useDefaultPrettyPrinter();

		generator.writeStartObject();

		generator.writeNumberField(CyIdentifiable.SUID, table.getSUID());
		generator.writeStringField(JsonTags.TITLE, table.getTitle());
		generator.writeBooleanField(JsonTags.PUBLIC, table.isPublic());
		generator.writeStringField(JsonTags.MUTABLE, table.getMutability().name());
		generator.writeStringField(JsonTags.PRIMARY_KEY, table.getPrimaryKey().getName());

		final List<CyRow> rows = table.getAllRows();
		generator.writeObjectField(JsonTags.ROWS, rows);

		generator.writeEndObject();
	}
}