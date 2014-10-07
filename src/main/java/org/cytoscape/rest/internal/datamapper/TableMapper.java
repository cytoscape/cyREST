package org.cytoscape.rest.internal.datamapper;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.NotFoundException;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.rest.internal.resource.JsonTags;

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
		final String columnName = rootNode.get(JsonTags.COLUMN_NAME).textValue();
		final Class<?> type = MapperUtil.getColumnClass(rootNode.get(JsonTags.COLUMN_TYPE).textValue());
		
		if(table.getColumn(columnName) !=null) {
			throw new IllegalArgumentException("Column already exists: " + columnName);
		}
		
		// Optional: fields
		boolean isImmutable = false;
		boolean isList = false;
		final JsonNode immutable = rootNode.get(JsonTags.COLUMN_IMMUTABLE);
		final JsonNode list = rootNode.get(JsonTags.COLUMN_IS_LIST);
		if(list != null) {
			isList = list.asBoolean();
		}
		if(immutable != null) {
			isImmutable = immutable.asBoolean();
		}
	
		if(isList) {			
			table.createListColumn(columnName, type, isImmutable);
		} else {
			table.createColumn(columnName, type, isImmutable);
		}
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
	

	/**
	 * This is for PUT method for default tables.
	 * 
	 * @param rootNode a JSON array.
	 * @param table CyTable to be updated.
	 * 
	 */
	
	private static final String KEY = "key";
	private static final String DATA_KEY = "dataKey";
	private static final String DATA = "data";
	
	public void updateTableValues(final JsonNode rootNode, final CyTable table) {
		// Validate body
		final JsonNode data = rootNode.get(DATA);
		if(data == null) {
			throw new NotFoundException("Data array is missing.");
		}
		if(!data.isArray()) {
			throw new IllegalArgumentException("Data should be an array.");
		}
		
		final JsonNode keyCol = rootNode.get(KEY);
		final String keyColName;
		if(keyCol == null) {
			// If not specified, simply use SUID.
			keyColName = CyIdentifiable.SUID;
		} else {
			keyColName = keyCol.asText();
		}

		System.out.println("Key Column = " + keyColName);
		
		// Check such column exists or not.
		final CyColumn col = table.getColumn(keyColName);
		if(col == null) {
			throw new NotFoundException("No such column in the table: " + keyColName);
		}
		
		final JsonNode dataKeyCol = rootNode.get(DATA_KEY);
		final String dataKeyColName;
		if(dataKeyCol == null) {
			dataKeyColName = CyIdentifiable.SUID;
		} else {
			dataKeyColName = dataKeyCol.asText();
		}

		
		System.out.println("Data Key = " + dataKeyColName);
		
		// This should be an array of objects
		for(final JsonNode entry:data) {
			final JsonNode keyValue = entry.get(dataKeyColName);
			if(keyValue == null) {
				// Skip the entry if there is no mapping key value.
				continue;
			}

			final Object key = getValue(keyValue, col.getType());
			if(key == null) {
				// Key is invalid.
				System.out.println("Invalid key!!!!!!!!!: ");
				continue;
			}
			final Collection<CyRow> machingRows = table.getMatchingRows(keyColName, key);
			
			if(machingRows.isEmpty()) {
				System.out.println("EMPTY!!!!!!!!!: ");
				continue;
			}
			
			for (final CyRow row : machingRows) {
				final Iterator<String> fields = entry.fieldNames();
				while (fields.hasNext()) {
					final String field = fields.next();
					final JsonNode value = entry.get(field);
					System.out.println("Original: " + field + " = " + value);
					if(value == null) {
						continue;
					}
					
					CyColumn column = table.getColumn(field);
					if (column == null) {
						// Need to create new column.
						final Class<?> type = getValueType(value);
						if(type == List.class) {
							// List is not supported.
							continue;
						}
						table.createColumn(field, type, false);
						column = table.getColumn(field);
					}
					
					try {
						setValue(column.getType(), value, row, field);
						System.out.println(field + " = " + value);
					} catch (Exception e) {
						// Simply ignore invalid value
						e.printStackTrace();
						continue;
					}
				}
			}
		}
	}


	private final Object getValue(final JsonNode value, final Class<?> type) {
		if (type == String.class) {
			return value.asText();
		} else if (type == Boolean.class) {
			return value.asBoolean();
		} else if (type == Double.class) {
			return value.asDouble();
		} else if (type == Integer.class) {
			return value.asInt();
		} else if (type == Long.class) {
			return value.asLong();
		} else if (type == Float.class) {
			return value.asDouble();
		} else {
			return null;
		}
	}


	private final void setValue(final Class<?> type, final JsonNode value, final CyRow row, final String columnName) {
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


	/**
	 * Check data type.  All numbers will be set to Double.
	 * 
	 * @param value
	 * @return
	 * 
	 */
	private final Class<?> getValueType(final JsonNode value) {
		if (value.isArray()) {
			return List.class;
		} else if (value.isBoolean()) {
			return Boolean.class;
		} else if (value.isNumber()) {
			return Double.class;
		} else {
			return String.class;
		}
	}
}
