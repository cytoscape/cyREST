package org.cytoscape.rest.internal.jaxrs;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.rest.internal.translator.CyNetwork2CytoscapeJSTranslator;
import org.cytoscape.rest.internal.translator.CyNetwork2JSONTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Path("/data/")
public class DataService {

	private final static Logger logger = LoggerFactory.getLogger(DataService.class);

	// private CyApplicationManager applicationManager;

	@Context
	private CyNetworkManager networkManager;

	@Context
	private CyNetworkFactory networkFactory;

	private final CyNetwork2JSONTranslator network2jsonTranslator;
	private final CyNetwork2CytoscapeJSTranslator network2jsTranslator;

	// private final JSON2CyNetworkTranslator json2CyNetworkTranslator;

	public DataService() {
		// this.applicationManager = applicationManager;
		// this.networkManager = networkManager;

		network2jsonTranslator = new CyNetwork2JSONTranslator();
		network2jsTranslator = new CyNetwork2CytoscapeJSTranslator();

		// json2CyNetworkTranslator = new
		// JSON2CyNetworkTranslator(networkFactory);
	}

	@GET
	@Path("/network/{suid}/")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCustomer(@PathParam("suid") String suid) {
		System.out.println("----Get network by SUID: " + suid);

		Long networkSUID = null;
		try {
			networkSUID = Long.parseLong(suid);
		} catch (NumberFormatException ex) {
			logger.warn("Invalid SUID: " + suid, ex);
			return "";
		}

		CyNetwork network = networkManager.getNetwork(networkSUID);
		return network2jsonTranslator.translate(network);
	}

	@GET
	@Path("/network/js/{suid}/")
	@Produces(MediaType.APPLICATION_JSON)
	public String getNetwork4js(@PathParam("suid") String suid) {
		System.out.println("----Get network by SUID for js: " + suid);

		Long networkSUID = null;
		try {
			networkSUID = Long.parseLong(suid);
		} catch (NumberFormatException ex) {
			logger.warn("Invalid SUID: " + suid, ex);
			return "";
		}

		CyNetwork network = networkManager.getNetwork(networkSUID);

		String result = null;

		System.out.println("Creating JSON for js...");
		try {
			result = network2jsTranslator.translate(network);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@POST
	@Path("/create/network/{name}/")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public String postCustomer(String json, @PathParam("name") String name) {

		final CyNetwork network = networkFactory.createNetwork();
		network.getRow(network).set(CyNetwork.NAME, name);
		networkManager.addNetwork(network);
		String result = "Input is " + json;
		return result;
	}
}
