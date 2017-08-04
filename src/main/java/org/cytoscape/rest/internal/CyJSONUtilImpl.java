package org.cytoscape.rest.internal;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.json.CyJSONUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CyJSONUtilImpl implements CyJSONUtil{

	private final Gson gson;

	private final JsonSerializer<CyRow> cyRowSerializer = new JsonSerializer<CyRow>() {
		@Override
		public JsonElement serialize(CyRow arg0, Type arg1, JsonSerializationContext arg2) {
			JsonObject object = new JsonObject();
			for (Map.Entry<String, Object> entry : arg0.getAllValues().entrySet()) {
				if (entry.getValue() instanceof List) {
					JsonArray listObject = new JsonArray();
					for (Object value : (List<?>)entry.getValue()) {
						if (value instanceof Number) {
							listObject.add(new JsonPrimitive((Number) value));
						} else if (value instanceof String) {
							listObject.add(new JsonPrimitive((String) value));
						} else if (value instanceof Boolean) {
							listObject.add(new JsonPrimitive((Boolean) value));
						}
					}
					object.add(entry.getKey(), listObject);
				} else {
					setProperty(object, entry.getKey(), entry.getValue());
				}
			}
			return object;
		}
		
		private void setProperty(JsonObject object, String key, Object value) {
			if (value instanceof Number) {
				object.addProperty(key, (Number)value); 
			} else if (value instanceof String) {
				object.addProperty(key, (String)value); 
			} else if (value instanceof Boolean) {
				object.addProperty(key, (Boolean) value);
			}
		}
	};
	
	private final JsonSerializer<CyTable> cyTableSerializer = new JsonSerializer<CyTable>() {
		@Override
		public JsonElement serialize(CyTable arg0, Type arg1, JsonSerializationContext arg2) {
			JsonObject object = new JsonObject();
			JsonArray listObject = new JsonArray();
			for (CyRow row : arg0.getAllRows()) {
				listObject.add(arg2.serialize(row));
			}
			object.add("rows", listObject);
			return object;
		}
	};
	
	public CyJSONUtilImpl() {
		gson = new GsonBuilder().registerTypeAdapter(CyRow.class, cyRowSerializer)
				.registerTypeAdapter(CyTable.class, cyTableSerializer)
				.setPrettyPrinting().create();
	}

	@Override
	public String toJson(CyIdentifiable cyIdentifiable) {
		return gson.toJson(cyIdentifiable.getSUID());
	}

	@Override
	public String toJson(Collection<CyIdentifiable> collection) {
		final List<Long> list = collection.stream()
				.map(obj->obj.getSUID())
				.collect(Collectors.toList());
		return gson.toJson(list);
	}

	@Override
	public String toJson(CyNetwork cyNetwork, CyNode cyNode) {
		CyRow row = cyNetwork.getRow(cyNode);
		return toJson(row);
	}

	@Override
	public String toJson(CyNetwork cyNetwork, CyEdge cyEdge) {
		CyRow row = cyNetwork.getRow(cyEdge);
		return toJson(row);
	}

	@Override
	public String toJson(CyNetwork cyNetwork) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toJson(CyTable cyTable) {
		return gson.toJson(cyTable);
	}

	@Override
	public String toJson(CyColumn cyColumn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toJson(CyRow cyRow) {
		return gson.toJson(cyRow, CyRow.class);
	}

}
