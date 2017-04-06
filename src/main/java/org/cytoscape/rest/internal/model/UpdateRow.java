package org.cytoscape.rest.internal.model;

import java.util.Map;

import io.swagger.annotations.ApiModel;

@ApiModel
public class UpdateRow {
	public String key;
	public String dataKey;
	public Map<String, String> data;
}
