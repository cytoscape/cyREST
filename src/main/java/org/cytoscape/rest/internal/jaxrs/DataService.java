package org.cytoscape.rest.internal.jaxrs;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.rest.TaskFactoryManager;
import org.cytoscape.rest.internal.task.RestTaskManager;
import org.cytoscape.rest.internal.translator.CyNetwork2CytoscapeJSTranslator;
import org.cytoscape.rest.internal.translator.CyNetwork2JSONTranslator;
import org.cytoscape.task.NetworkCollectionTaskFactory;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Path("/data/")
public class DataService {

	private final static Logger logger = LoggerFactory.getLogger(DataService.class);

	@Context
	private CyApplicationManager applicationManager;

	@Context
	private CyNetworkManager networkManager;

	@Context
	private CyNetworkFactory networkFactory;

	@Context
	private TaskFactoryManager tfManager;

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
	public String getNetworkBySUID(@PathParam("suid") String suid) {
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

		final CyNetwork network = networkManager.getNetwork(networkSUID);
		return network2jsTranslator.translate(network);
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

	@GET
	@Path("/execute/{taskId}/{suid}")
	@Produces(MediaType.APPLICATION_JSON)
	public String runNetworkTask(@PathParam("suid") String suid, @PathParam("taskId") String taskName) {
		if (taskName != null) {
			TaskFactory tf = tfManager.getTaskFactory(taskName);
			if (tf != null) {
				TaskIterator ti;
				if (tf instanceof NetworkTaskFactory) {
					System.out.println("Got network tf: " + taskName);
					NetworkTaskFactory ntf = (NetworkTaskFactory) tf;
					final Long networkSUID = parseSUID(suid);
					final CyNetwork network = networkManager.getNetwork(networkSUID);
					ti = ntf.createTaskIterator(network);
				} else {
					ti = tf.createTaskIterator();
				}

				TaskManager tm = new RestTaskManager();
				tm.execute(ti);
				System.out.println("Finished Task: " + taskName);
			}
			return "{ \"currentNetwork\": " + applicationManager.getCurrentNetwork().getSUID() + "}";
		} else {
			return "Could not execute task.";
		}
	}

	@GET
	@Path("/execute/networks/{taskId}/{suid}")
	@Produces(MediaType.APPLICATION_JSON)
	public String runNetworkCollectionTask(@PathParam("suid") String suid, @PathParam("taskId") String taskName) {
		if (taskName != null) {
			NetworkCollectionTaskFactory tf = tfManager.getNetworkCollectionTaskFactory(taskName);
			System.out.println("Got TF: " + taskName);
			if (tf != null) {
				TaskIterator ti;

				System.out.println("Got network collection tf: " + taskName);
				NetworkCollectionTaskFactory ntf = (NetworkCollectionTaskFactory) tf;
				final Long networkSUID = parseSUID(suid);
				final CyNetwork network = networkManager.getNetwork(networkSUID);
				List<CyNetwork> networks = new ArrayList<CyNetwork>();
				networks.add(network);
				ti = ntf.createTaskIterator(networks);

				TaskManager tm = new RestTaskManager();
				tm.execute(ti);
				System.out.println("Finished Task: " + taskName);
			}
			return "OK";
		} else {
			return "Could not execute task.";
		}
	}


	private final Long parseSUID(final String stringID) {
		Long networkSUID = null;
		try {
			networkSUID = Long.parseLong(stringID);
		} catch (NumberFormatException ex) {
			logger.warn("Invalid SUID: " + stringID, ex);
			throw new IllegalArgumentException("Could not parse SUID: " + stringID);
		}

		return networkSUID;
	}
}
