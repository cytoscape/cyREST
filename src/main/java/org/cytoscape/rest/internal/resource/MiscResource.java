package org.cytoscape.rest.internal.resource;

import java.util.Properties;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.cytoscape.rest.internal.model.CytoscapeVersionModel;
import org.cytoscape.rest.internal.model.ServerStatusModel;
import org.cytoscape.rest.internal.task.AllAppsStartedListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

/**
 * Resource to provide general status of the Cytoscape REST server. 
 * 
 * 
 * @servicetag Server status
 * 
 */
@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.REST_SERVICE_TAG,CyRESTSwagger.CyRESTSwaggerConfig.CYTOSCAPE_SYSTEM_TAG})
@Singleton
@Path("/v1")
public class MiscResource extends AbstractResource {
	
	private static final String RESOURCE_URN = "";

	public static final int NOT_FOUND_ERROR= 1;
	public static final int INTERNAL_METHOD_ERROR = 2;
	
	@Override
	public String getResourceURI() {
		return RESOURCE_URN;
	}
	
	private final static Logger logger = LoggerFactory.getLogger(MiscResource.class);
	
	@Override
	public Logger getResourceLogger() {
		return logger;
	}
	
	@Inject
	@NotNull
	private AllAppsStartedListener allAppsStartedListener;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@ApiOperation(value="Cytoscape RESTful API server status",
		notes="Returns the status of the server if operational, including version information and available memory and "
				+ "processor resources.")
	@ApiResponses(value = { 
			@ApiResponse(code = 500, message = "CyREST service is unavailable"),
			@ApiResponse(code = 200, message = "Server Status", response = ServerStatusModel.class),
	})
	public ServerStatusModel getStatus() {
		ServerStatusModel output = new ServerStatusModel();
		output.setAllAppsStarted(allAppsStartedListener.getAllAppsStarted());
		return output;
	}

	@GET
	@Path("/gc")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Run Java garbage collection",
	notes="Manually call Java's System.gc() to free up unused memory. This process happens automatically, but may be useful to call explicitly for testing or evaluation purposes.")
	 @ApiResponses(value = { 
	            @ApiResponse(code = 204, message = "Successful Garbage Collection")
	    })
	public Response runGarbageCollection() {
		Runtime.getRuntime().gc();
		return Response.noContent().build();
	}

	@GET
	@Path("/version")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get Cytoscape and REST API version", notes="Returns the version of the current Cytoscape REST API."
			)
	public CytoscapeVersionModel getCytoscapeVersion() {

		if (props == null) {
			//throw new InternalServerErrorException("Could not find CyProperty object.");
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INTERNAL_METHOD_ERROR, 
					"Could not find CyProperty object.", 
					logger, null);
		}
		
		final Properties properties = (Properties) this.props.getProperties();
		final Object versionNumber = properties.get("cytoscape.version.number");
		if (versionNumber != null) {
			return new CytoscapeVersionModel(API_VERSION, versionNumber.toString());
		} else {
			//throw new NotFoundException("Could not find Cytoscape version number property.");
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					NOT_FOUND_ERROR, 
					"Could not find Cytoscape version number property.", 
					getResourceLogger(), null);
		}
	}

	//TODO Why is this here? It doesn't change anything, and appears to be the same as GET.
	@PUT
	@Path("/ui/show-details")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Show Level of Graphics Details", hidden=true,
	notes="### Unimplemented\n\nDo not rely on this function.")
	public CytoscapeVersionModel updateShowGraphicsDetailsOption() {

		if (props == null) {
			//throw new InternalServerErrorException("Could not find CyProperty object.");
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					RESOURCE_URN, 
					INTERNAL_METHOD_ERROR, 
					"Could not find CyProperty object.", 
					logger, null);
		}

		final Properties property = (Properties) this.props.getProperties();
		final Object versionNumber = property.get("cytoscape.version.number");
		if (versionNumber != null) {
			return new CytoscapeVersionModel(API_VERSION, versionNumber.toString());
		} else {
			//throw new NotFoundException("Could not find Cytoscape version number property.");
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					NOT_FOUND_ERROR, 
					"Could not find Cytoscape version number property.", 
					getResourceLogger(), null);
		}
	}
}
