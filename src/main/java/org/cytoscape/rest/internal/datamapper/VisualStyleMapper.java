package org.cytoscape.rest.internal.datamapper;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.rest.internal.MappingFactoryManager;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;

import com.fasterxml.jackson.databind.JsonNode;

public class VisualStyleMapper {

	private static final String TITLE = "title";
	private static final String MAPPINGS = "mappings";
	private static final String DEFAULTS = "defaults";

	private static final String MAPPING_TYPE = "type";
	private static final String MAPPING_DISCRETE = "discrete";
	private static final String MAPPING_PASSTHROUGH = "passthrough";
	private static final String MAPPING_CONTINUOUS = "continuous";

	private static final String MAPPING_COLUMN = "column";
	private static final String MAPPING_COLUMN_TYPE = "column_type";
	private static final String MAPPING_VP = "visual_property";

	public VisualStyle buildVisualStyle(final MappingFactoryManager factoryManager, final VisualStyleFactory factory,
			final VisualLexicon lexicon, final JsonNode rootNode) {

		final JsonNode title = rootNode.get(TITLE);
		final VisualStyle style = factory.createVisualStyle(title.textValue());

		final JsonNode defaults = rootNode.get(DEFAULTS);
		final JsonNode mappings = rootNode.get(MAPPINGS);

		parseDefaults(defaults, lexicon, style);
		parseMappings(mappings, style, lexicon, factoryManager);

		return style;
	}

	@SuppressWarnings("rawtypes")
	private final void parseDefaults(JsonNode defaults, VisualLexicon lexicon, VisualStyle style) {
		for (final JsonNode vpNode : defaults) {
			String vpName = vpNode.get("name").textValue();
			final VisualProperty vp = getVisualProperty(vpName, lexicon);
			if (vp == null) {
				continue;
			}
			style.setDefaultValue(vp, vp.parseSerializableString(vpNode.get("value").textValue()));

		}
	}

	private final void parseMappings(JsonNode mappings, VisualStyle style, VisualLexicon lexicon,
			MappingFactoryManager factoryManager) {
		for (final JsonNode mapping : mappings) {
			final String type = mapping.get(MAPPING_TYPE).textValue();
			final String column = mapping.get(MAPPING_COLUMN).textValue();
			final String colType = mapping.get(MAPPING_COLUMN_TYPE).textValue();
			final String vpName = mapping.get(MAPPING_VP).textValue();

			final VisualProperty vp = getVisualProperty(vpName, lexicon);
			final Class<?> columnType = getColumnClass(colType);
			if (vp == null || columnType == null) {
				return;
			}

			System.out.println("++++++++++++++++++++++++++++ type= " + type);

			if (type.equals(MAPPING_DISCRETE)) {
				parseDiscrete();
			} else if (type.equals(MAPPING_CONTINUOUS)) {
				parseContinuous();
			} else if (type.equals(MAPPING_PASSTHROUGH)) {
				System.out.println("PT ++++++++++++++++++++++++++++");
				final VisualMappingFunctionFactory factory = factoryManager.getFactory(PassthroughMapping.class);
				PassthroughMapping pMapping = parsePassthrough(column, columnType, vp, factory);
				style.addVisualMappingFunction(pMapping);
			}
		}

	}

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

	private final Class<?> getColumnClass(final String type) {
		if (type.equals(Double.class.getSimpleName())) {
			return Double.class;
		} else if (type.equals(Long.class.getSimpleName())) {
			return Long.class;
		} else if (type.equals(Integer.class.getSimpleName())) {
			return Integer.class;
		} else if (type.equals(Float.class.getSimpleName())) {
			return Float.class;
		} else if (type.equals(Boolean.class.getSimpleName())) {
			return Boolean.class;
		} else if (type.equals(String.class.getSimpleName())) {
			return String.class;
		} else {
			return null;
		}
	}

	private final void parseDiscrete() {

	}

	private final void parseContinuous() {

	}

	private final PassthroughMapping parsePassthrough(String columnName, Class<?> type, VisualProperty<?> vp,
			VisualMappingFunctionFactory pFactory) {

		return (PassthroughMapping) pFactory.createVisualMappingFunction(columnName, type, vp);

	}

}
