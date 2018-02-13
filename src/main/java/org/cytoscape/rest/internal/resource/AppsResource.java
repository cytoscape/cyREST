package org.cytoscape.rest.internal.resource;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cytoscape.rest.internal.model.AppModel;
import org.cytoscape.rest.internal.model.ServerStatusModel;
import org.cytoscape.rest.internal.task.AutomationAppTracker;
import org.osgi.framework.Bundle;

import com.google.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Resource to provide the status of installed Cytoscape apps. 
 * 
 * 
 * @servicetag Server status
 * 
 */
@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.APPS_TAG})
@Singleton
@Path("/v1/apps")
public class AppsResource extends AbstractResource {

	@Inject
	AutomationAppTracker appTracker;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@ApiOperation(value="Get installed Automation Accessible Cytoscape Apps",
	notes="Returns installed Cytoscape Apps that have CyREST accessible Functions or Commands, as a list of App names.")
	public List<AppModel> getAppList() {
		ArrayList<AppModel> list = new ArrayList<AppModel>();
		for (Bundle bundle : appTracker.getAppBundles()) {
			AppModel appModel = new AppModel();
			appModel.symbolicName = bundle.getSymbolicName();
			appModel.version = bundle.getVersion().toString();
			appModel.state = bundle.getState();
			list.add(appModel);
		}
		return list;
	
	}
}
