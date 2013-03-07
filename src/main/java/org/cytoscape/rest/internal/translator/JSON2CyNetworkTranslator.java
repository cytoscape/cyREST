package org.cytoscape.rest.internal.translator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class JSON2CyNetworkTranslator {

	private final CyNetworkFactory networkFactory;

	public JSON2CyNetworkTranslator(final CyNetworkFactory networkFactory) {
		this.networkFactory = networkFactory;
	}

	public CyNetwork translate(final String json) throws JsonParseException, IOException {

		final CyNetwork network = networkFactory.createNetwork();
		final Map<Long, CyNode> nodeSUIDMap = new HashMap<Long, CyNode>();

		final JsonFactory factory = new JsonFactory();
		final JsonParser parser = factory.createJsonParser(json);

		while (parser.nextToken() != null) {
			final String fieldname = parser.getCurrentName() == null ? "" : parser.getCurrentName();
			
			if (fieldname.equals(CyJsonToken.NODES.getName())) {
				parser.nextToken();
				while (parser.nextToken() != JsonToken.END_ARRAY) {
					handleCell(parser, network, nodeSUIDMap);
				}
			} else if (fieldname.equals(CyJsonToken.EDGES.getName())) {
				parser.nextToken();
				while (parser.nextToken() != JsonToken.END_ARRAY) {
					handleEdge(parser, network, nodeSUIDMap);
				}
			}
		}

		return network;
	}
	
	private final void handleEdge(JsonParser parser, CyNetwork network, final Map<Long, CyNode> nodeSUIDMap)
			throws JsonParseException, IOException {
		
		Long source = null;
		Long target = null;
		Boolean isDirected = true;
		
		final List<Object[]> tableValues = new ArrayList<Object[]>();
		
		while (parser.nextToken() != JsonToken.END_OBJECT) {
			//JsonToken token = parser.nextToken();
			
			// Find source and target
			while(parser.nextToken() != JsonToken.START_OBJECT) {
				//JsonToken t = parser.nextToken();
				JsonToken t = parser.getCurrentToken();
				final String fieldName = parser.getCurrentName();
				System.out.println("This is s or t: " + fieldName);
				//parser.nextToken();
				//System.out.println("This is t: " + parser.getCurrentName());
				if (fieldName.equals("source") && t == JsonToken.VALUE_NUMBER_INT) {
					source = parser.getLongValue();
				} else if (fieldName.equals("target") && t == JsonToken.VALUE_NUMBER_INT) {
					target = parser.getLongValue();
				} else if(fieldName.equals("isDirected") && t == JsonToken.VALUE_FALSE) {
					isDirected = parser.getBooleanValue();
				}
				
			}
			String type = "";
			String valueString = "";
			Object[] nameTypeValueArray = new Object[3];
			final String fieldName = parser.getCurrentName();

			while (parser.nextToken() != JsonToken.END_OBJECT) {
				JsonToken t = parser.nextToken();
				final String textValue = parser.getText();
				final String attributeFieldName = parser.getCurrentName();
				if (attributeFieldName.equals("type")) {
					type = textValue;
				} else if (attributeFieldName.equals("value")) {
					valueString = textValue;
				} else if (t == JsonToken.START_ARRAY) {
					System.out.println("LIST ATTR");
				}
			}

			System.out.println(fieldName + " = " + valueString);
			nameTypeValueArray[0] = fieldName;
			nameTypeValueArray[1] = type;
			nameTypeValueArray[2] = valueString;
			tableValues.add(nameTypeValueArray);

		}

		if (source == null || target == null)
			throw new IllegalStateException("Found brken entry: edge without source/targe.");

		final CyEdge edge = network.addEdge(nodeSUIDMap.get(source), nodeSUIDMap.get(target), isDirected);
		
		CyTable table = network.getDefaultEdgeTable();
		// Map table data
		for (Object[] entry : tableValues) {
			setTableData(parser, network, table, edge, entry[0], entry[1], entry[2]);
		}

		
		
	}

	private void handleCell(JsonParser parser, CyNetwork network, final Map<Long, CyNode> nodeSUIDMap)
			throws JsonParseException, IOException {
		Long suid = null;
		final List<Object[]> tableValues = new ArrayList<Object[]>();

		while (parser.nextToken() != JsonToken.END_OBJECT) {
			JsonToken token = parser.nextToken();
			// System.out.println("This is node: " + parser.getCurrentName());
			String type = "";
			String valueString = "";
			Object[] nameTypeValueArray = new Object[3];
			final String fieldName = parser.getCurrentName();

			while (parser.nextToken() != JsonToken.END_OBJECT) {
				JsonToken t = parser.nextToken();
				final String textValue = parser.getText();
				final String attributeFieldName = parser.getCurrentName();
				if (fieldName.equals(CyIdentifiable.SUID) && t == JsonToken.VALUE_NUMBER_INT) {
					suid = parser.getLongValue();
				} else if (attributeFieldName.equals("type")) {
					type = textValue;

				} else if (attributeFieldName.equals("value")) {
					valueString = textValue;
				} else if (t == JsonToken.START_ARRAY) {
					System.out.println("LIST ATTR");
				}
			}

			System.out.println(fieldName + " = " + valueString);
			nameTypeValueArray[0] = fieldName;
			nameTypeValueArray[1] = type;
			nameTypeValueArray[2] = valueString;
			tableValues.add(nameTypeValueArray);

		}

		if (suid == null)
			throw new IllegalStateException("Found brken entry: node without SUID.");

		final CyNode newNode = network.addNode();
		nodeSUIDMap.put(suid, newNode);
		CyTable table = network.getDefaultNodeTable();
		// Map table data
		for (Object[] entry : tableValues) {
			setTableData(parser, network, table, newNode, entry[0], entry[1], entry[2]);
		}

	}

	public void setTableData(JsonParser parser, CyNetwork network, CyTable table, CyIdentifiable object, final Object columnName,
			final Object type, final Object value) {
		if (type == null || type.toString().length() == 0 || columnName == null || columnName.toString().length() == 0)
			return;

		CyColumn column = table.getColumn(columnName.toString());
		final CyRow row = network.getRow(object);
		Class<?> columnType = null;
		if (type.equals("string")) {
			columnType = String.class;
		} else if(type.equals("integer")) {
			columnType = Integer.class;
		} else if(type.equals("boolean")) {
			columnType = Boolean.class;
		}
		if(columnType == null)
			return;
		
		if(column == null)
			table.createColumn(columnName.toString(), columnType, false);
		
		if(value != null && value.toString().length() != 0)
			row.set(columnName.toString(), getValue(columnType, value.toString()));
		
	}
	
	private final Object getValue(Class<?> type, String value) {
		if(type == String.class)
			return value;
		else if(type == Boolean.class)
			return Boolean.parseBoolean(value);
		else {
			return value;
		}
	}

}
