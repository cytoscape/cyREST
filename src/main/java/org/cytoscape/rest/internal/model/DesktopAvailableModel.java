package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Cytoscape Desktop Availability")
public class DesktopAvailableModel {
	@ApiModelProperty(value="This is `true` if Cytoscape Desktop is available and `false` if Cytoscape is running in headless mode (not available yet).")
	public boolean isDesktopAvailable;
}
