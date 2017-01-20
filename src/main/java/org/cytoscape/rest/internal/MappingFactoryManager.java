package org.cytoscape.rest.internal;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;

public class MappingFactoryManager {

	private final Map<Class<?>, VisualMappingFunctionFactory> factories;

	public MappingFactoryManager() {
		factories = new HashMap<Class<?>, VisualMappingFunctionFactory>();
	}

	public VisualMappingFunctionFactory getFactory(Class<?> mappingType) {
		return factories.get(mappingType);
	}

	@SuppressWarnings("rawtypes")
	public void addFactory(VisualMappingFunctionFactory factory, Map properties) {
		factories.put(factory.getMappingFunctionType(), factory);
	}

	@SuppressWarnings("rawtypes")
	public void removeFactory(VisualMappingFunctionFactory factory, Map properties) {
		factories.remove(factory.getMappingFunctionType());
	}
}