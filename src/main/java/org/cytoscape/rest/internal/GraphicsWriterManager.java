package org.cytoscape.rest.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.PresentationWriterFactory;

public class GraphicsWriterManager {

	private final Map<String, PresentationWriterFactory> factories;

	public GraphicsWriterManager() {
		factories = new HashMap<>();
	}

	public PresentationWriterFactory getFactory(final String fileType) {
		return factories.get(fileType);
	}

	@SuppressWarnings("rawtypes")
	public void addFactory(PresentationWriterFactory factory, Map properties) {
		final CyFileFilter ff = factory.getFileFilter();
		final Set<String> ext = ff.getExtensions();
		
		final String firstExt = ext.iterator().next();
		
		factories.put(firstExt, factory);
	}

	@SuppressWarnings("rawtypes")
	public void removeFactory(PresentationWriterFactory factory, Map properties) {
		final CyFileFilter ff = factory.getFileFilter();
		final Set<String> ext = ff.getExtensions();
		factories.remove(ext.iterator().next());
	}
}
