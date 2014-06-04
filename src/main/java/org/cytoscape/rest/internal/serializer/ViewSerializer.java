package org.cytoscape.rest.internal.serializer;

import java.util.Collection;

import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;

public class ViewSerializer {
	
	public String serializeView(final View<?> view, final Collection<VisualProperty<?>> properties) {
		
		for(final VisualProperty<?> vp: properties) {
			final Object value = view.getVisualProperty(vp);
		}
		return null;
	}

}
