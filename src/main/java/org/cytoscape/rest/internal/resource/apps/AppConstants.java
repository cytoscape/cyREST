package org.cytoscape.rest.internal.resource.apps;

import javax.ws.rs.Path;

public final class AppConstants {
	public static final String APP_TAG = "App";
	
	public static final String APPS_ROOT = "/v1/apps/";

	public static final boolean hasValidAppRoot(Class<?> inputClass)
	{
		Path[] paths = inputClass.getAnnotationsByType(Path.class);
		if (paths.length == 1) {
			for (Path path : paths){
				if (path.value().startsWith(APPS_ROOT)){
					return true;
				}
			}
		}
		return false;
	}
}
