package org.cytoscape.rest.internal.model;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("A Group of Cytoscape Nodes and Edges")
public class GroupModel {
	
	@ApiModelProperty(value="SUID of the Node representing the group.")
	public long SUID;
	
	@ApiModelProperty(value="The collapsed value of this group. If this is `true`, only "
			+ "the Node representing this group will be visible, and all the nodes and edges "
			+ "contained by it will not.")
	public boolean collapsed;
	
	@ApiModelProperty(value="The Nodes contained by this Group represented by SUIDs")
	public List<Long> nodes;
	
	@ApiModelProperty(value="The Edges contained by this Group represented by SUIDs")
	public List<Long> internal_edges;

	@ApiModelProperty(value="Edges from outside this Group that connect to its nodes, represented by SUIDs")
	public List<Long> external_edges;
}
