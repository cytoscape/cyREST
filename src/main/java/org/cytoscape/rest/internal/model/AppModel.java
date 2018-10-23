package org.cytoscape.rest.internal.model;

import org.osgi.framework.Bundle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel()
public class AppModel {

	public static final String OSGI_BUNDLE_STATUS_ALLOWABLE_VALUES = Bundle.UNINSTALLED + "," +
			Bundle.INSTALLED + "," + 
			Bundle.RESOLVED + "," + 
			Bundle.STARTING + "," + 
			Bundle.STOPPING + "," + 
			Bundle.ACTIVE + ","
			;

	public static final String OSGI_BUNDLE_STATUS_DESCRIPTION_LIST = 	"This should be one of the following:\n" +
			"* " + Bundle.UNINSTALLED + " UNINSTALLED\n" +
			"* " + Bundle.INSTALLED + " INSTALLED\n" + 
			"* " + Bundle.RESOLVED + " RESOLVED\n" + 
			"* " + Bundle.STARTING + " STARTING\n" + 
			"* " + Bundle.STOPPING + " STOPPING\n" + 
			"* " + Bundle.ACTIVE + " ACTIVE\n"
			;

	public static final String OSGI_BUNDLE_STATUS_DESCRIPTION = "The state of the App bundle. " + OSGI_BUNDLE_STATUS_DESCRIPTION_LIST;





	@ApiModelProperty(value="If this App is implemented as an [OSGi bundle](https://osgi.org/javadoc/r4v43/core/org/osgi/framework/Bundle.html), this field contains its `Bundle-Name` header", required=false)
	public String bundleName;
	@ApiModelProperty(value="If this App is implemented as an [OSGi bundle](https://osgi.org/javadoc/r4v43/core/org/osgi/framework/Bundle.html), this field contains its `Bundle-SymbolicName` header", required=false)
	public String bundleSymbolicName;
	@ApiModelProperty(value="If this App is implemented as an [OSGi bundle](https://osgi.org/javadoc/r4v43/core/org/osgi/framework/Bundle.html), this field contains its `Bundle-Version` header", required=false)
	public String bundleVersion;
	@ApiModelProperty(value="If this App is implemented as an [OSGi bundle](https://osgi.org/javadoc/r4v43/core/org/osgi/framework/Bundle.html), this field contains its state. " + OSGI_BUNDLE_STATUS_DESCRIPTION_LIST, required=false, allowableValues="1,2,4,8,16,32")
	public int bundleState;


}
