package org.cytoscape.rest.internal.datamapper;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
		// This should be an array of objects
		for(final JsonNode entry:rootNode) {
			final Long primaryKey = entry.get(CyIdentifiable.SUID).asLong();
			final CyRow row = table.getRow(primaryKey);
			if(row == null) {
				continue;
			}
			
			JsonNode value = entry.get("value");
			setValue(table.getColumn(columnName).getType(), value, row, columnName);
		}
	}
	

	public void updateTableValues(final JsonNode rootNode, final CyTable table) {
		JsonNode keyCol = rootNode.get("key");
		if(keyCol == null) {
			throw new NotFoundException("Key column name is required.");
		}
		final String keyColName = keyCol.asText();
		
		// Check such column exists or not.
		CyColumn col = table.getColumn(keyColName);
		if(col==null) {
			throw new NotFoundException("No such column in the table: " + keyColName);
		}
		
		JsonNode dataKeyCol = rootNode.get("dataKey");
		if(dataKeyCol == null) {
			throw new NotFoundException("Data key name is required.");
		}
		final String dataKeyColName = dataKeyCol.asText();
		
		// Data should be an array object.
		JsonNode data = rootNode.get("data");
		if(data == null) {
			throw new NotFoundException("Data field is required.");
		}
		
		// This should be an array of objects
		for(final JsonNode entry:data) {
			
			final JsonNode keyValue = entry.get(dataKeyColName);
			if(keyValue == null) {
				continue;
			}
			final Collection<CyRow> machingRows = table.getMatchingRows(keyColName, keyValue.asText());
			
			if(machingRows.isEmpty()) {
				continue;
			}
			
			for (CyRow row : machingRows) {
				final Iterator<String> fields = entry.fieldNames();
				while (fields.hasNext()) {
					final String field = fields.next();
					final JsonNode value = entry.get(field);
					final Class<?> type = testValueType(value);
					
					if (table.getColumn(field) == null) {
						// Need to create new column
						table.createColumn(field, type, false);
					}
					setValue(type, value, row, field);
				}
			}
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


	private final Class<?> testValueType(final JsonNode value) {
		if (value.isArray()) {
			return List.class;
		} else if (value.isBoolean()) {
			return Boolean.class;
		} else if (value.isInt()) {
			return Integer.class;
		} else if (value.isLong()) {
			return Long.class;
		} else if (value.isFloatingPointNumber()) {
			return Double.class;
		} else {
			return String.class;
		}
	}
}
