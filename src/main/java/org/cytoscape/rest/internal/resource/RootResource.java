package org.cytoscape.rest.internal.resource;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Root of the REST API server.
 * 
 */
@Singleton
@Path("/")
@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.REST_SERVICE_TAG})
public class RootResource extends AbstractResource {

	private static final String[] VERSION_LIST = { API_VERSION };
	private static final Map<String, String[]> VERSION_MAP = new HashMap<String, String[]>();
	static {
		VERSION_MAP.put("availableApiVersions", VERSION_LIST);
	}

	/**
	 * @summary Get available REST API versions
	 * 
	 * @return List of available REST API versions. Currently, v1 is the only
	 *         available version.
	 * 
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get available REST API versions", notes="Returns a list of available REST API versions. Currently, v1 is the only available version")
	public Map<String, String[]> getVersions() {
		return VERSION_MAP;
	}

}
