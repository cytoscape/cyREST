package org.cytoscape.rest.internal.serializer;

import java.io.IOException;
import java.util.List;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

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
		generator.writeStringField("title", table.getTitle());
		generator.writeBooleanField("public", table.isPublic());
		generator.writeStringField("mutable", table.getMutability().name());
		generator.writeStringField("primary_key", table.getPrimaryKey().getName());

		final List<CyRow> rows = table.getAllRows();
		generator.writeObjectField("rows", rows);
		generator.writeEndObject();
	}
}