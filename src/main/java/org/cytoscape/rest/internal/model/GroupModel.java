package org.cytoscape.rest.internal.model;

import java.util.List;

public class GroupModel {
	public long SUID;
	public boolean collapsed;
	public List<Long> nodes;
	public List<Long> internal_edges;
	public List<Long> external_edges;
}
