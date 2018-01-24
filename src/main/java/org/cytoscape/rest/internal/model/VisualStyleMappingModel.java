package org.cytoscape.rest.internal.model;

import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class VisualStyleMappingModel {
	
	@ApiModelProperty(required = true, allowableValues="discreet,continuous,passthrough")
	public String mappingType;
	
	@ApiModelProperty(required = true)
	public String mappingColumn;
	
	@ApiModelProperty(required = true)
	public String mappingColumnType;
	
	@ApiModelProperty(required = true)
	public String visualProperty;
	
	@ApiModelProperty(required = false, value="Map for `discreet` mappings.", example="")
	public List<DiscreteMappingKeyValueModel> map;
	
	@ApiModelProperty(required = false, value="Points for `continuous` mappings.", example="")
	public List<PointModel> points;
	
}
