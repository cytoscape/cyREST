package org.cytoscape.rest.internal.serializer;

import org.cytoscape.model.CyNetwork;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class GroupModule extends SimpleModule {

	private static final long serialVersionUID = -1590853632349015561L;

	public GroupModule(CyNetwork network) {
		super("GroupModule", new Version(1, 0, 0, null, null, null));
		addSerializer(new GroupSerializer(network));
	}
}
