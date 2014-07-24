package org.cytoscape.rest.internal.datamapper;

import javax.ws.rs.NotFoundException;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Create & Update table objects.
 * 
 *
 */
public class TableMapper {

	public void updateColumnName(final JsonNode rootNode, final CyTable table) {
		final String currentName = rootNode.get("old_name").textValue();
		final CyColumn column = table.getColumn(currentName);
		
		if (column == null) {
			throw new NotFoundException("Column does not exist.");
		}
		final String newName = rootNode.get("new_name").textValue();
		if (newName == null) {
			throw new NotFoundException("New column name is required.");
		}
		column.setName(newName);
	}

	public void createNewColumn(final JsonNode rootNode, final CyTable table) {
		// Extract required fields
		final String columnName = rootNode.get("name").textValue();
		final Class<?> type = MapperUtil.getColumnClass(rootNode.get("type").textValue());
		
		// Optional: mutability
		boolean isImmutable = false;
		final JsonNode immutable = rootNode.get("immutable");
		if(immutable != null) {
			isImmutable = immutable.asBoolean();
		}
		table.createColumn(columnName, type, isImmutable);
	}

	public void updateColumnValues(final JsonNode rootNode, final CyTable table, final String columnName) {
		// Extract required fields
		
		// This should be an array of objects
		for(JsonNode entry:rootNode) {
			final Long primaryKey = entry.get(CyIdentifiable.SUID).asLong();
			final CyRow row = table.getRow(primaryKey);
			if(row == null) {
				continue;
			}
			
			JsonNode value = entry.get("value");
			setValue(table.getColumn(columnName).getType(), value, row, columnName);
		}
	}
	
	private final void setValue(final Class<?> type, JsonNode value, final CyRow row, final String columnName) {
		if (type == String.class) {
			row.set(columnName, value.asText());
		} else if (type == Boolean.class) {
			row.set(columnName, value.asBoolean());
		} else if (type == Double.class) {
			row.set(columnName, value.asDouble());
		} else if (type == Integer.class) {
			row.set(columnName, value.asInt());
		} else if (type == Long.class) {
			row.set(columnName, value.asLong());
		} else if (type == Float.class) {
			row.set(columnName, value.asDouble());
		}
	}
}
