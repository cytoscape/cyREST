package org.cytoscape.rest.internal.datamapper;

import javax.ws.rs.NotFoundException;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyTable;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Create & Update table objects.
 * 
 *
 */
public class TableMapper {

	public void updateColumnName(final JsonNode rootNode, final CyColumn column) {
		if (column == null) {
			throw new NotFoundException("Column does not exist.");
		}
		final String newName = rootNode.get("name").textValue();
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
}
