package org.cytoscape.rest.internal.model;

import java.util.List;

import io.swagger.annotations.ApiModel;

@ApiModel
public class VisualPropertyValuesModel {
	public String visualProperty;
	public List<Object> values;
}
