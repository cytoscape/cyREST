package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class NewColumnParameterModel {
	@ApiModelProperty(value = "New Column Name", required=true)
	public String name;
	@ApiModelProperty(value = "New Column Data Type", required=true)
	public ModelConstants.ColumnTypePrimitive type; //"data type, Double, String, Boolean, Long, Integer",
	@ApiModelProperty(value="If true, make this column immutable.", required=false)
	public Boolean immutable; //": "Optional: boolean value to specify immutable or not",
	@ApiModelProperty(value="If true, make this a List column for the given type.", required=false)
	public Boolean list; //": "Optional.  If true, return create List column for the given type."
	@ApiModelProperty(value="If true, make this a local column.", required=false)
	public Boolean local;// "local": "Optional.  If true, it will be a local column"
}
