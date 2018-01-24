package org.cytoscape.rest.internal.model;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ObjectVisualPropertyValueModel {
	
	@ApiModelProperty(value="SUID of the Object")
	public Long SUID;
	
	@ApiModelProperty(value="List of the Objects Visual Properties")
	public List<VisualPropertyValueModel> view;
}
