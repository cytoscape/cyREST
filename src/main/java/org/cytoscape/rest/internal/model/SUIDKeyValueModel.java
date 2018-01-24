package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class SUIDKeyValueModel {
	@ApiModelProperty(value="SUID of the object.")
	public Long SUID;
	@ApiModelProperty(value="Value, represented as a JSON primitive.")
	public Object value;
}
