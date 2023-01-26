package org.cytoscape.rest.internal;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.io.write.CyNetworkViewWriterFactory;

/**
 * 
 * OSGi service listener to pick CyNetworkViewWriterFactories.
 *
 */
public class CyNetworkViewWriterFactoryManager {

	// ID of the CX writer service
	public static final String CX_WRITER_ID = "cxNetworkWriterFactory";
	public static final String CX2_WRITER_ID = "cx2NetworkWriterFactory";
	
	private final Map<String, CyNetworkViewWriterFactory> factories;

	public CyNetworkViewWriterFactoryManager() {
		factories = new HashMap<>();
	}

	public CyNetworkViewWriterFactory getFactory(final String fileType) {
		return factories.get(fileType);
	}

	@SuppressWarnings("rawtypes")
	public void addFactory(final CyNetworkViewWriterFactory factory, final Map properties) {
		final String id = (String) properties.get("id");
		if(id != null) {
			factories.put(id, factory);
		}
	}

	@SuppressWarnings("rawtypes")
	public void removeFactory(final CyNetworkViewWriterFactory factory, Map properties) {
		final String id = (String) properties.get("id");
		
		if(id != null) {
			properties.remove(id);
		}
	}
}
