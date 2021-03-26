package org.cytoscape.rest.internal.integrationtest;

import java.io.File;

import javax.ws.rs.core.Response;

import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.ci.model.CIResponse;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.rest.internal.model.NetworkSUIDModel;
import org.cytoscape.rest.internal.model.ServerStatusModel;
import org.cytoscape.rest.internal.resource.MiscResource;
import org.cytoscape.rest.internal.resource.NetworkResource;
import org.cytoscape.rest.internal.resource.SessionResource;
import org.cytoscape.service.util.CyServiceRegistrar;

public class CyRESTTests {
	
	private static final String SAMPLE_DATA_DIR = "sampleData";
	
	final CyServiceRegistrar serviceRegistrar;
	final File sampleDataDirectory;
	final CyEventHelper cyEventHelper;
	
	
	public CyRESTTests(final CyServiceRegistrar serviceRegistrar) throws Exception {
		this.serviceRegistrar = serviceRegistrar;
		final CyApplicationConfiguration applicationCfg = serviceRegistrar.getService(CyApplicationConfiguration.class);

		if (applicationCfg != null) {
			sampleDataDirectory =  new File(applicationCfg.getInstallationDirectoryLocation().getAbsolutePath() + File.separatorChar + SAMPLE_DATA_DIR + File.separatorChar);
		} else {
			throw new Exception("No CyApplicationConfiguration available. Unable to run tests.");
		}
		
		cyEventHelper = serviceRegistrar.getService(CyEventHelper.class);
	}
	
	public void throwExceptionIfFalse(final String message, boolean value) throws Exception {
		if (value == false) {
			throw new Exception(message); 
		}
	}
	
	@CyRESTTest
	public void testRoot() throws Exception {
		MiscResource miscResource = serviceRegistrar.getService(MiscResource.class);
		ServerStatusModel serverStatus = miscResource.getStatus();
		throwExceptionIfFalse("All apps should be started.", serverStatus.getAllAppsStarted() == true);
	}
	
	@CyRESTTest
	public void flushPayloadEvents() throws Exception {
		cyEventHelper.flushPayloadEvents();
	}
	
	@CyRESTTest
	public void loadGalFilteredApplyStyle() throws Exception {
		SessionResource sessionResource = serviceRegistrar.getService(SessionResource.class);
		sessionResource.getSessionFromFile(sampleDataDirectory.getAbsolutePath() + "/galFiltered.cys");
		
		cyEventHelper.flushPayloadEvents();
		
		NetworkResource networkResource = serviceRegistrar.getService(NetworkResource.class);
		Response response = networkResource.getCurrentNetwork();
		
		cyEventHelper.flushPayloadEvents();
		
		CIResponse<NetworkSUIDModel> ciResponse = (CIResponse<NetworkSUIDModel>) response.getEntity();
		long networkSuid = ciResponse.data.networkSUID;
		
		System.out.println("galFiltered currentNetwork " + networkSuid);
		
	}
}
