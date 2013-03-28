package org.cytoscape.rest.internal.translator;

import org.cytoscape.rest.internal.datamapper.JsCyNetworkSerializer;
import org.cytoscape.rest.internal.datamapper.JsCyNetworkViewSerializer;
import org.cytoscape.rest.internal.datamapper.JsNodeViewSerializer;
import org.cytoscape.rest.internal.datamapper.JsRowSerializer;
import org.cytoscape.rest.internal.datamapper.JsVisualStyleSerializer;
import org.cytoscape.rest.internal.datamapper.JsVsiaulStyleSetSerializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class CytoscapejsModule extends SimpleModule {

	private static final long serialVersionUID = -3553426112109820245L;

	public CytoscapejsModule() {
		super("CytoscapejsModule", new Version(1, 0, 0, null, null, null));
		addSerializer(new JsCyNetworkSerializer());
		addSerializer(new JsRowSerializer());
		addSerializer(new JsCyNetworkViewSerializer());
		addSerializer(new JsNodeViewSerializer());
		
		// For Visual Style
		addSerializer(new JsVisualStyleSerializer());
		addSerializer(new JsVsiaulStyleSetSerializer());
	}
}