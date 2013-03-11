package org.cytoscape.rest.internal.translator;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class CytoscapeJSModule extends SimpleModule {
	
	public CytoscapeJSModule() {
		// TODO: provide correct artifact information.
		super("CytoscapeJSModule", new Version(1, 0, 0, null, null, null));
		addSerializer(new CyNetwork2CytoscapeJS());
	}

}
