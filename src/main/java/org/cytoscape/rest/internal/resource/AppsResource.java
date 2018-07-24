package org.cytoscape.rest.internal.resource;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.cytoscape.ci.model.CIResponse;
import org.cytoscape.rest.internal.CyRESTConstants;
import org.cytoscape.rest.internal.model.AppModel;
import org.cytoscape.rest.internal.model.CountModel;
import org.cytoscape.rest.internal.task.AutomationAppTracker;
import org.osgi.framework.Bundle;

import com.google.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Resource to provide the status of installed Cytoscape apps. 
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
	notes="Returns installed Cytoscape Apps that have CyREST accessible Functions or Commands. If the `bundleState` parameter is used, this operation will only return Apps with an OSGi bundle state matching the passed parameter.")
	public List<AppModel> getAppList(@ApiParam(value=AppModel.OSGI_BUNDLE_STATUS_DESCRIPTION, allowableValues = AppModel.OSGI_BUNDLE_STATUS_ALLOWABLE_VALUES, required = false) @QueryParam("bundleState") Integer bundleState) {
		ArrayList<AppModel> list = new ArrayList<AppModel>();
		for (Bundle bundle : appTracker.getAppBundles()) {
			AppModel appModel = new AppModel();
			Object bundleNameObject = bundle.getHeaders().get(CyRESTConstants.BUNDLE_NAME);
			if (bundleNameObject != null) {
				appModel.bundleName = bundleNameObject.toString();
			}
			appModel.bundleSymbolicName = bundle.getSymbolicName();
			appModel.bundleVersion = bundle.getVersion().toString();
			appModel.bundleState = bundle.getState();
			if (bundleState == null || appModel.bundleState == bundleState) {
				list.add(appModel);
			}
		}
		return list;
	
	}
	
	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@ApiOperation(value="Get the number of Automation Accessible Cytoscape Apps",
	notes="Returns the number of installed Cytoscape Apps that have CyREST accessible Functions or Commands.  If the `bundleState` parameter is used, this operation will only count Apps with an OSGi bundle state matching the passed parameter.")
	public CIResponse<CountModel> getAppCount(@ApiParam(value=AppModel.OSGI_BUNDLE_STATUS_DESCRIPTION, allowableValues = AppModel.OSGI_BUNDLE_STATUS_ALLOWABLE_VALUES, required=false) @QueryParam("bundleState") Integer bundleState) {
		long count = 0l;
		for (Bundle bundle : appTracker.getAppBundles()) {
			if (bundleState == null || bundle.getState() == bundleState) {
				count++;
			}
		}
		CountModel data = new CountModel(count);
		return ciResponseFactory.getCIResponse(data);
	}
}
