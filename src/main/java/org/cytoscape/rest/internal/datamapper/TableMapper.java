package org.cytoscape.rest.internal.datamapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
	
	@SuppressWarnings("serial")
	public final class ColumnNotFoundException extends Exception {
		public ColumnNotFoundException(String string) {
			super(string);
		}
	}
	
	public void updateColumnName(final JsonNode rootNode, final CyTable table) throws ColumnNotFoundException {
		final JsonNode currentNameTag = rootNode.get(JsonTags.COLUMN_NAME_OLD);
		if(currentNameTag == null) {
			throw new IllegalArgumentException("Original column name is missing.");
		}

		final JsonNode newNameTag = rootNode.get(JsonTags.COLUMN_NAME_NEW);
		if(newNameTag == null) {
			throw new IllegalArgumentException("New column name is missing.");
		}

		final String currentName = currentNameTag.asText();
		if(currentName == null || currentName.isEmpty()) {
			throw new IllegalArgumentException("Original column name is missing.");
		}
		final String newName = newNameTag.asText();
		if(newName == null || newName.isEmpty()) {
			throw new IllegalArgumentException("New column name is missing.");
		}

		final CyColumn column = table.getColumn(currentName);
		if (column == null) {
			// Caught in TableResource.updateColumnName()
			throw new ColumnNotFoundException("Column does not exist.");
		}
		column.setName(newName);
	}

	public void createNewColumn(final JsonNode rootNode, 
			final CyTable table, final CyTable localTable) {
		// Extract required fields
		final String columnName = rootNode.get(JsonTags.COLUMN_NAME).textValue();
		final Class<?> type = MapperUtil.getColumnClass(rootNode.get(JsonTags.COLUMN_TYPE).textValue(), true);

		if(table.getColumn(columnName) !=null) {
			throw new IllegalArgumentException("Column already exists: " + columnName);
		}

		// Optional: fields
		boolean isImmutable = false;
		boolean isList = false;
		boolean isLocal = false;
		final JsonNode immutable = rootNode.get(JsonTags.COLUMN_IMMUTABLE);
		final JsonNode list = rootNode.get(JsonTags.COLUMN_IS_LIST);
		final JsonNode local = rootNode.get(JsonTags.COLUMN_IS_LOCAL);
		if(list != null) {
			isList = list.asBoolean();
		}
		if(immutable != null) {
			isImmutable = immutable.asBoolean();
		}
		if(local != null) {
			isLocal = local.asBoolean();
		}

		if(isList) {	
			if(isLocal) {
				localTable.createListColumn(columnName, type, isImmutable);
			} else {
				table.createListColumn(columnName, type, isImmutable);
			}
		} else {
			if(isLocal) {
				localTable.createColumn(columnName, type, isImmutable);
			} else {
				table.createColumn(columnName, type, isImmutable);
			}
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


	public void updateAllColumnValues(final String defaultValue, final CyTable table, final String columnName) {
		// This should be an array of objects
		final Class<?> dataType = table.getColumn(columnName).getType();
		for(final CyRow row : table.getAllRows()) {
			assignValue(defaultValue, dataType, row, columnName);
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

	public void updateTableValues(final JsonNode rootNode, final CyTable table) throws ColumnNotFoundException {
		// Validate body
		final JsonNode data = rootNode.get(DATA);
		if(data == null) {
			throw new IllegalArgumentException("Data array is missing.");
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

		// Check such column exists or not.
		final CyColumn col = table.getColumn(keyColName);
		if(col == null) {
			throw new ColumnNotFoundException("No such column in the table: " + keyColName);
		}

		final JsonNode dataKeyCol = rootNode.get(DATA_KEY);
		final String dataKeyColName;
		if(dataKeyCol == null) {
			dataKeyColName = CyIdentifiable.SUID;
		} else {
			dataKeyColName = dataKeyCol.asText();
		}

		// This should be an array of objects
		for(final JsonNode entry:data) {
			final JsonNode keyValue = entry.get(dataKeyColName);
			if(keyValue == null) {
				// Skip the entry if there is no mapping key value.
				continue;
			}

			final Object key = MapperUtil.getValue(keyValue, col.getType());
			if(key == null) {
				// Key is invalid.
				continue;
			}
			final Collection<CyRow> machingRows = table.getMatchingRows(keyColName, key);

			if(machingRows.isEmpty()) {
				continue;
			}

			for (final CyRow row : machingRows) {
				final Iterator<String> fields = entry.fieldNames();
				while (fields.hasNext()) {
					final String field = fields.next();
					final JsonNode value = entry.get(field);
					if(value == null) {
						continue;
					}
					
					CyColumn column = null;
					synchronized (this) {
						column = table.getColumn(field);
						if (column == null) {
							// Need to create new column.
							final Class<?> type = getValueType(value);
							if (type == List.class) {
								boolean uniform = true;
								Class<?> listType = null;
								for (JsonNode element : value) {
									if (listType == null) {
										listType = getValueType(element);
									} else {
										if (!listType.equals(getValueType(element))) {
											uniform = false;
											continue;
										}
									}
								}
								if (listType != null && uniform) {
									table.createListColumn(field, listType, false);
									column = table.getColumn(field);
								} else {
									// List is unusable
									continue;
								}
							} else {
								table.createColumn(field, type, false);
								column = table.getColumn(field);
							}
						}
					}

					try {
						setValue(column.getType(), value, row, field);
					} catch (Exception e) {
						// Simply ignore invalid value
						e.printStackTrace();
						continue;
					}
				}
			}
		}
	}




	private final void setValue(final Class<?> type, final JsonNode value, final CyRow row, final String columnName) {
		if (value.isNull()) {
			row.set(columnName, null);
			return;
		}
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
		} else if (type == List.class) {
			Class<?> listType = row.getTable().getColumn(columnName).getListElementType();
			updateList(listType, value, row, columnName);
		}
	}

	private final <K> void updateList(Class<K> listType, JsonNode values, final CyRow row, final String columnName ) {
		List<K> list = row.getList(columnName, listType);
		if (list == null) {
			row.set(columnName, new ArrayList<K>());
			list = row.getList(columnName, listType);
		}
		list.clear();
		for (Iterator<JsonNode> i = values.iterator(); i.hasNext(); ) {
			JsonNode value= i.next();
			if (listType == String.class) {
				list.add((K) value.asText());
			} else if (listType == Boolean.class) {
				list.add((K)new Boolean(value.asBoolean()));
			} else if (listType == Double.class) {
				list.add((K)new Double(value.asDouble()));
			} else if (listType == Integer.class) {
				list.add((K)new Integer(value.asInt()));
			} else if (listType == Long.class) {
				list.add((K)new Long(value.asLong()));
			} else if (listType == Float.class) {
				list.add((K)new Float(value.asDouble()));
			}
		}
	}

	private final void assignValue(final String value, final Class<?> type, final CyRow row, final String columnName) {
		if (type == String.class) {
			row.set(columnName, value.toString());
		} else if (type == Boolean.class) {
			row.set(columnName, Boolean.parseBoolean(value));
		} else if (type == Double.class) {
			row.set(columnName, Double.parseDouble(value));
		} else if (type == Integer.class) {
			row.set(columnName, Integer.parseInt(value));
		} else if (type == Long.class) {
			row.set(columnName, Long.parseLong(value));
		} else if (type == Float.class) {
			row.set(columnName, Double.parseDouble(value));
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
