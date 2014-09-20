package org.cytoscape.rest.internal.datamapper;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.rest.internal.MappingFactoryManager;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;

import com.fasterxml.jackson.databind.JsonNode;

public class VisualStyleMapper {

	private static final String TITLE = "title";
	private static final String MAPPINGS = "mappings";
	private static final String DEFAULTS = "defaults";

	public static final String MAPPING_TYPE = "mappingType";
	private static final String MAPPING_DISCRETE = "discrete";
	private static final String MAPPING_PASSTHROUGH = "passthrough";
	private static final String MAPPING_CONTINUOUS = "continuous";

	public static final String MAPPING_COLUMN = "mappingColumn";
	public static final String MAPPING_COLUMN_TYPE = "mappingColumnType";
	public static final String MAPPING_VP = "visualProperty";

	private static final String MAPPING_DISCRETE_MAP = "map";
	private static final String MAPPING_DISCRETE_KEY = "key";
	private static final String MAPPING_DISCRETE_VALUE = "value";

	public VisualStyle buildVisualStyle(final MappingFactoryManager factoryManager, final VisualStyleFactory factory,
			final VisualLexicon lexicon, final JsonNode rootNode) {

		final JsonNode title = rootNode.get(TITLE);
		final VisualStyle style = factory.createVisualStyle(title.textValue());

		final JsonNode defaults = rootNode.get(DEFAULTS);
		final JsonNode mappings = rootNode.get(MAPPINGS);

		parseDefaults(defaults, style, lexicon);
		parseMappings(mappings, style, lexicon, factoryManager);
		return style;
	}

	public void buildMappings(final VisualStyle style, final MappingFactoryManager factoryManager,
			final VisualLexicon lexicon, final JsonNode mappings) {
		parseMappings(mappings, style, lexicon, factoryManager);
	}
	
	public void updateStyleName(final VisualStyle style,
			final VisualLexicon lexicon, final JsonNode rootNode) {
		final String newTitle = rootNode.get(TITLE).textValue();
		style.setTitle(newTitle);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private final void parseDefaults(final JsonNode defaults, final VisualStyle style, final VisualLexicon lexicon) {
		for (final JsonNode vpNode : defaults) {
			String vpName = vpNode.get(MAPPING_VP).textValue();
			final VisualProperty vp = getVisualProperty(vpName, lexicon);
			final JsonNode value = vpNode.get("value");
			if (vp == null || value == null ) {
				continue;
			}

			Object parsedValue = null;
			if(value.isTextual()) {
				parsedValue = vp.parseSerializableString(value.asText());
			} else {
				parsedValue = vp.parseSerializableString(value.toString());
			}
			
			style.setDefaultValue(vp, parsedValue);
		}
	}


	@SuppressWarnings("rawtypes")
	private final void parseMappings(JsonNode mappings, VisualStyle style, VisualLexicon lexicon,
			MappingFactoryManager factoryManager) {
		
		for (final JsonNode mapping : mappings) {
			final String type = mapping.get(MAPPING_TYPE).textValue();
			final String column = mapping.get(MAPPING_COLUMN).textValue();
			final String colType = mapping.get(MAPPING_COLUMN_TYPE).textValue();
			final String vpName = mapping.get(MAPPING_VP).textValue();

			final VisualProperty vp = getVisualProperty(vpName, lexicon);
			final Class<?> columnType = MapperUtil.getColumnClass(colType);
			if (vp == null || columnType == null) {
				continue;
			}

			VisualMappingFunction newMapping = null;
			if (type.equals(MAPPING_DISCRETE)) {
				final VisualMappingFunctionFactory factory = factoryManager.getFactory(DiscreteMapping.class);
				newMapping = parseDiscrete(column, columnType, vp, factory, mapping.get(MAPPING_DISCRETE_MAP));
			} else if (type.equals(MAPPING_CONTINUOUS)) {
				final VisualMappingFunctionFactory factory = factoryManager.getFactory(ContinuousMapping.class);
				newMapping = parseContinuous(column, columnType, vp, factory, mapping);
			} else if (type.equals(MAPPING_PASSTHROUGH)) {
				final VisualMappingFunctionFactory factory = factoryManager.getFactory(PassthroughMapping.class);
				newMapping = parsePassthrough(column, columnType, vp, factory);
			}

			if (newMapping != null) {
				style.addVisualMappingFunction(newMapping);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private final VisualProperty getVisualProperty(String vpName, VisualLexicon lexicon) {
		VisualProperty vp = null;

		if (vpName.startsWith("NODE")) {
			vp = lexicon.lookup(CyNode.class, vpName);
		} else if (vpName.startsWith("EDGE")) {
			vp = lexicon.lookup(CyEdge.class, vpName);
		} else if (vpName.startsWith("NETWORK")) {
			vp = lexicon.lookup(CyNetwork.class, vpName);
		}
		return vp;
	}


	private final Object parseKeyValue(final Class<?> type, final String value) {
		if (type == Double.class) {
			return Double.parseDouble(value);
		} else if (type == Long.class) {
			return Long.parseLong(value);
		} else if (type == Integer.class) {
			return Integer.parseInt(value);
		} else if (type == Float.class) {
			return Float.parseFloat(value);
		} else if (type == Boolean.class) {
			return Boolean.parseBoolean(value);
		} else if (type == String.class) {
			return value;
		} else {
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private final DiscreteMapping parseDiscrete(String columnName, Class<?> type, VisualProperty<?> vp,
			VisualMappingFunctionFactory factory, JsonNode discreteMapping) {
		DiscreteMapping mapping = (DiscreteMapping) factory.createVisualMappingFunction(columnName, type, vp);

		final Map map = new HashMap();
		for (JsonNode pair : discreteMapping) {
			final Object key = parseKeyValue(type, pair.get(MAPPING_DISCRETE_KEY).textValue());
			if (key != null) {
				map.put(key, vp.parseSerializableString(pair.get(MAPPING_DISCRETE_VALUE).textValue()));
			}
		}
		mapping.putAll(map);
		return mapping;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private final ContinuousMapping parseContinuous(String columnName, Class<?> type, VisualProperty<?> vp,
			VisualMappingFunctionFactory factory, JsonNode mappingNode) {

		final ContinuousMapping mapping = (ContinuousMapping) factory.createVisualMappingFunction(columnName, type, vp);
		for(JsonNode point:mappingNode.get("points")) {
			JsonNode val = point.get("value");
			JsonNode lesser = point.get("lesser");
			JsonNode equal = point.get("equal");
			JsonNode greater = point.get("greater");
			
			final BoundaryRangeValues newPoint = 
					new BoundaryRangeValues(vp.parseSerializableString(lesser.asText()), 
							vp.parseSerializableString(equal.asText()), 
							vp.parseSerializableString(greater.asText()));
			mapping.addPoint(val.asDouble(), newPoint);
		}
		
		return mapping;
	}

	@SuppressWarnings("rawtypes")
	private final PassthroughMapping parsePassthrough(String columnName, Class<?> type, VisualProperty<?> vp,
			VisualMappingFunctionFactory factory) {

		return (PassthroughMapping) factory.createVisualMappingFunction(columnName, type, vp);
	}
}
