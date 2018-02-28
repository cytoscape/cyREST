package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel()
public class AppModel {
	
	@ApiModelProperty(value="If this App is implemented as an [OSGi bundle](https://osgi.org/javadoc/r4v43/core/org/osgi/framework/Bundle.html), this field contains its `Bundle-Name` header", required=false)
	public String bundleName;
	@ApiModelProperty(value="If this App is implemented as an [OSGi bundle](https://osgi.org/javadoc/r4v43/core/org/osgi/framework/Bundle.html), this field contains its `Bundle-SymbolicName` header", required=false)
	public String bundleSymbolicName;
	@ApiModelProperty(value="If this App is implemented as an [OSGi bundle](https://osgi.org/javadoc/r4v43/core/org/osgi/framework/Bundle.html), this field contains its `Bundle-Version` header", required=false)
	public String bundleVersion;
	@ApiModelProperty(value="If this App is implemented as an [OSGi bundle](https://osgi.org/javadoc/r4v43/core/org/osgi/framework/Bundle.html), this field contains its state.", required=false, allowableValues="1,2,4,8,16,32")
	public int bundleState;
}
