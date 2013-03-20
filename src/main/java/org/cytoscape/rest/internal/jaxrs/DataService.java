package org.cytoscape.rest.internal.jaxrs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.rest.DataMapper;
import org.cytoscape.rest.TaskFactoryManager;
import org.cytoscape.rest.internal.datamapper.CyNetwork2CytoscapejsMapper;
import org.cytoscape.rest.internal.datamapper.CyNetworkView2CytoscapejsMapper;
import org.cytoscape.rest.internal.task.RestTaskManager;
import org.cytoscape.rest.internal.translator.CyNetwork2JSONTranslator;
import org.cytoscape.task.NetworkCollectionTaskFactory;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Path("/v1")
public class DataService {

	private final static Logger logger = LoggerFactory.getLogger(DataService.class);
		
	private static final String NETWORKS = "networks";
	private static final String TABLES = "tables";

	@Context
	private CyApplicationManager applicationManager;

	@Context
	private CyNetworkManager networkManager;

	@Context
	private CyNetworkFactory networkFactory;

	@Context
	private TaskFactoryManager tfManager;

	private final CyNetwork2JSONTranslator network2jsonTranslator;
	
	private final DataMapper<CyNetwork> cytoscapejs;
	private final DataMapper<CyNetworkView> cytoscapejsView;

	public DataService() {
		this.cytoscapejs = new CyNetwork2CytoscapejsMapper();
		this.cytoscapejsView = new CyNetworkView2CytoscapejsMapper();
		
		network2jsonTranslator = new CyNetwork2JSONTranslator();
	}
	
	private final CyNetwork getNetwork(final String title) {
		final Set<CyNetwork> networks = networkManager.getNetworkSet();
		
		for(final CyNetwork network : networks) {
			final String networkTitle = network.getRow(network).get(CyNetwork.NAME, String.class);
			if(networkTitle == null)
				continue;
			if(networkTitle.equals(title))
				return network;
		}
		
		// Not found
		return null;
	}

	@GET
	@Path("/" + NETWORKS + "/{suid}/")
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
	@Path("/" + NETWORKS + "/{title}.json/")
	@Produces(MediaType.APPLICATION_JSON)
	public String getNetworkByTitle(@PathParam("title") String title) {
		System.out.println("----Get network by Title: " + title);

		final CyNetwork network = getNetwork(title);
		if(network == null)
			throw new WebApplicationException(404);

		return cytoscapejs.writeAsString(network);
	}
	
	@GET
	@Path("/" + NETWORKS + "/views/")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCurrentNetworkView() {
		final CyNetworkView curView = applicationManager.getCurrentNetworkView();
		System.out.println("----Get current network VIEW");
		
		if(curView == null)
			throw new WebApplicationException(404);

		return cytoscapejsView.writeAsString(curView);
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
		return cytoscapejs.writeAsString(network);
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
			NetworkTaskFactory tf = tfManager.getNetworkTaskFactory(taskName);
			System.out.println("TF from Map: " + tf);

			if (tf != null) {
				TaskIterator ti;

				System.out.println("## Got network tf: " + taskName);
				NetworkTaskFactory ntf = (NetworkTaskFactory) tf;
				final Long networkSUID = parseSUID(suid);
				final CyNetwork network = networkManager.getNetwork(networkSUID);
				ti = ntf.createTaskIterator(network);

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

	@GET
	@Path("/execute/{taskId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String runStatelessTask(@PathParam("taskId") String taskName) {
		if (taskName != null) {
			TaskFactory tf = tfManager.getTaskFactory(taskName);
			if (tf != null) {
				TaskIterator ti = tf.createTaskIterator();

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
