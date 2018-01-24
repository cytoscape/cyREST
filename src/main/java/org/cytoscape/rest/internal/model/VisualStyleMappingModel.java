package org.cytoscape.rest.internal.model;

import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class VisualStyleMappingModel {
	
	@ApiModelProperty(value="The type of Mapping", required = true, allowableValues="discreet,continuous,passthrough")
	public String mappingType;
	
	@ApiModelProperty(value="Table column this Mapping gets values from", required = true)
	public String mappingColumn;
	
	@ApiModelProperty(value="The type of the `mappingColumn`, represented as the simple name of its Java class.", required = true)
	public String mappingColumnType;
	
	@ApiModelProperty(value="Unique internal name of the Visual Property this mapping is applied to.", required = true)
	public String visualProperty;
	
	@ApiModelProperty(required = false, value="Map for `discreet` Mappings.", example="")
	public List<DiscreteMappingKeyValueModel> map;
	
	@ApiModelProperty(required = false, value="Points for `continuous` Mappings.", example="")
	public List<PointModel> points;
	
}
