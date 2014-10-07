package org.cytoscape.rest.internal.serializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class VisualStyleModule extends SimpleModule {
	
	private static final long serialVersionUID = -4690460168404145341L;

	static final String FORMAT_VERSION_TAG = "format_version";
	static final String FORMAT_VERSION = "1.0";

	public VisualStyleModule() {
		super("VisualStyleSerializer", new Version(1, 0, 0, null, null, null));
		addSerializer(new DiscreteMappingSerializer());
		addSerializer(new ContinuousMappingSerializer());
		addSerializer(new PassthroughMappingSerializer());
	}
}