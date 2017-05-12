package org.cytoscape.rest.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class BundleResourceProvider {
	
	private final Bundle bundle;
	
	public BundleResourceProvider(BundleContext bundleContext) {
		bundle = bundleContext.getBundle();
	}
	
	public InputStream getResourceInputStream(String resourcePath) throws IOException {
		URL url = bundle.getResource(resourcePath);
		return url.openConnection().getInputStream();
	}
}
