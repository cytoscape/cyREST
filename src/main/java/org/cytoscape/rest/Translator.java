package org.cytoscape.rest;

import org.cytoscape.model.CyIdentifiable;

/**
 * Convert given Cytoscape object into other forms.
 *
 * @param <T> Converted object type.
 * @param <V> Cytoscape Objects, i.e., CyNetwork, CyNode, CyEdge, or CyTable.
 */
public interface Translator<T, V extends CyIdentifiable> {

	/**
	 * Convert Cytoscape object into the specified data type.
	 * The return value can be the following:
	 *  - JSON (GraphSON)
	 *  - XML
	 *  - CSV
	 *  
	 * @param cytoscapeObject Original object to be converted into other object
	 * @return Converted graph objects.
	 */
	T translate(final V cytoscapeObject);
	
	
}
