package org.cytoscape.rest.internal.translator;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class CytoscapejsModule extends SimpleModule {

	private static final long serialVersionUID = -3553426112109820245L;

	public CytoscapejsModule() {
		super("CytoscapejsModule", new Version(1, 0, 0, null, null, null));
		addSerializer(new CyNetwork2CytoscapejsJson());
	}
}