package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Visual Property Dependency")
public class VisualPropertyDependencyModel {
	@ApiModelProperty(value="The name of the Visual Property Dependency", example="arrowColorMatchesEdge")
	public String visualPropertyDependency;
	@ApiModelProperty(value="```true``` if this dependency is enabled.")
    public boolean enabled;
}
