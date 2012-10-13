package org.cytoscape.rest;

import org.cytoscape.model.CyIdentifiable;

public interface Translator<T, V extends CyIdentifiable> {

	T translate(final V cytoscapeObject);
}
