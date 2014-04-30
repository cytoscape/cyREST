package org.cytoscape.rest.internal.serializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.transform.Source;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

public class CyTableSerializer {
	
	public String toCSV(final CyTable table) throws Exception {

		final StringBuilder builder = new StringBuilder();
		final Collection<CyColumn> columns = table.getColumns();
		final int colLength = columns.size();
		final List<CyRow> rows = table.getAllRows();
		int idx = 1;
		
		final List<String> columnNames = new ArrayList<String>();
		columnNames.add(CyIdentifiable.SUID);
		
//		if(isEdge) {
//			columnNames.add("source.suid");
//			columnNames.add("target.suid");
//		}
		for (final CyColumn column : columns) {
			if(column.getName().equals(CyIdentifiable.SUID) == false) {
				columnNames.add(column.getName());
			}
		}
		
		// Add header
		for (final String columnName : columnNames) {
			builder.append(columnName);
			if (idx == colLength) {
				builder.append("\n");
			} else {
				builder.append(",");
			}
			idx++;
		}

		for (final CyRow row : rows) {
			idx = 1;
			final Long suid = row.get(CyIdentifiable.SUID, Long.class);
			builder.append(suid + ",");
			
//			if(isEdge) {
//				// FIXME
//				builder.append(rowfdsf.get(CyIdentifiable.SUID, Long.class) + ",");
//				builder.append(row.get(CyIdentifiable.SUID, Long.class) + ",");
//			}
			for (final String columnName : columnNames) {
				if(columnName.equals(CyIdentifiable.SUID)) {
					continue;
				}
				final CyColumn column = table.getColumn(columnName);
				final Class<?> type = column.getType();
				if (type != List.class) {
					final Object value = row.get(column.getName(), type);
					if(value != null) {
						builder.append(value.toString());
					}
				} else {
					final List<?> listValue = row.getList(column.getName(), column.getListElementType());	
					builder.append(listToString(listValue));
				}
				if (idx == colLength) {
					builder.append("\n");
				} else {
					builder.append(",");
				}
				idx++;
			}
		}
		return builder.toString();
	}
	
	private final void addRow() {
		
	}

	private final String listToString(final List<?> listCell) {
		final StringBuilder listBuilder = new StringBuilder();

		for (final Object item : listCell) {
			if(item != null) {
				listBuilder.append(item.toString());
				listBuilder.append("|");
			}
		}
		final String cellString = listBuilder.toString();
		if(cellString != null && cellString.isEmpty() == false) {
			return cellString.substring(0, cellString.length() - 1);
		} else {
			return "";
		}
	}
}