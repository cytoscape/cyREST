package org.cytoscape.rest;


import org.cytoscape.work.Task;

public interface GetCytoscapeObjectTask<T> extends Task {
	
	T getObject(final String name);

}
