package org.cytoscape.rest.internal.resource;

import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.cytoscape.rest.internal.BundleResourceProvider;

import com.google.inject.Inject;

@Path("/v1/swaggerUI")
public class SwaggerUI {
	
	@Inject
	BundleResourceProvider bundleResourceProvider;
	
	@GET
	@Path("{path:.*}")
	public InputStream serveUI(@PathParam("path") String path) throws IOException {
		return bundleResourceProvider.getResourceInputStream(path);
	}
}
