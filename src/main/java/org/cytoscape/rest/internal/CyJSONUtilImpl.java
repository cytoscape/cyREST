package org.cytoscape.rest.internal;

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
import org.cytoscape.rest.internal.resource.JsonTags;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class CyJSONUtilImpl implements CyJSONUtil{

	private final Gson gson;

	private JsonObject serialize(CyRow cyRow) {
		JsonObject object = new JsonObject();
		for (Map.Entry<String, Object> entry : cyRow.getAllValues().entrySet()) {
			object.add(entry.getKey(), serializeCell(entry.getValue()));
		}
		return object;
	}

	private JsonElement serializeCell(Object object) {
		if (object instanceof List) {
			JsonArray listObject = new JsonArray();
			for (Object value : (List<?>)object) {
				listObject.add(getPrimitive(value));
			}
			return listObject;
		} else {
			return getPrimitive(object);
		}
	}

	private JsonElement getPrimitive(Object object) {
		if ( object instanceof Number) {
			return new JsonPrimitive((Number) object);
		} else if (object instanceof String) {
			return new JsonPrimitive((String) object);
		} else if (object instanceof Boolean) {
			return new JsonPrimitive((Boolean) object);
		} else if (object == null) {
			return JsonNull.INSTANCE;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public CyJSONUtilImpl() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeNulls().setPrettyPrinting();
		gson = gsonBuilder.create();
	}

	@Override
	public String toJson(CyIdentifiable cyIdentifiable) {
		return gson.toJson(cyIdentifiable.getSUID());
	}

	@Override
	public String cyIdentifiablesToJson(Collection<? extends CyIdentifiable> collection) {
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
		JsonObject object = serialize(row);
		object.addProperty("source", cyEdge.getSource().getSUID());
		object.addProperty("target", cyEdge.getTarget().getSUID());
		return gson.toJson(object);
	}

	@Override
	public String toJson(CyNetwork cyNetwork) {
		CyRow row = cyNetwork.getRow(cyNetwork);
		return toJson(row);
	}

	@Override
	public String toJson(CyTable cyTable, boolean includeDefinition, boolean includeRows) {
		JsonObject object = new JsonObject();
		if (includeDefinition) {
			object.addProperty("SUID", cyTable.getSUID());
			object.addProperty("title", cyTable.getTitle());
			object.addProperty("public", cyTable.isPublic());
			object.addProperty("mutable", cyTable.getMutability().name());
			object.addProperty("primaryKey", cyTable.getPrimaryKey().getName());
		}
		if (includeRows) {
			JsonArray listObject = new JsonArray();
			for (CyRow row : cyTable.getAllRows()) {
				listObject.add(serialize(row));
			}
			object.add("rows", listObject);
		}
		return gson.toJson(object);
	}

	@Override
	public String toJson(CyColumn cyColumn, boolean includeDefinition, boolean includeValues) {
		JsonObject object = new JsonObject();
		if (includeDefinition) {
			object.addProperty(JsonTags.COLUMN_NAME, cyColumn.getName());
			object.addProperty(JsonTags.COLUMN_TYPE, cyColumn.getType().getSimpleName());
			object.addProperty(JsonTags.COLUMN_IMMUTABLE,cyColumn.isImmutable());
			object.addProperty(JsonTags.PRIMARY_KEY, cyColumn.isPrimaryKey());
		}
		if (includeValues) {
			JsonArray listObject = new JsonArray();
			for (CyRow row : cyColumn.getTable().getAllRows()) {
				listObject.add(serializeCell(row.get(cyColumn.getName(), cyColumn.getType())));
			}
			object.add("values", listObject);
		}
		return gson.toJson(object);
	}

	@Override
	public String toJson(CyRow cyRow) {
		return gson.toJson(serialize(cyRow));
	}

	@Override
	public String cyColumnsToJson(Collection<CyColumn> collection) {
		JsonArray listObject = new JsonArray();

		for (CyColumn column : collection) {
			listObject.add(new JsonPrimitive(column.getName()));
		}
		return gson.toJson(listObject);
	}

}
