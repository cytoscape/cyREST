package org.cytoscape.rest.internal.task;

import java.util.Dictionary;
import java.util.Hashtable;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.rest.internal.CommandManager;
import org.cytoscape.rest.internal.servlet.ExecuteTaskServlet;
import org.cytoscape.rest.internal.servlet.GetNetworkServlet;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

public class StartHttpServerTask extends AbstractTask {

	private final BundleContext bc;
	private final CyApplicationManager applicationManager;
	private final CyNetworkFactory networkFactory;
	private final CyNetworkManager networkManager;
	private final CommandManager cm;

	public StartHttpServerTask(final BundleContext bc, final CyApplicationManager applicationManager,
			final CyNetworkFactory networkFactory, final CyNetworkManager networkManager, CommandManager commandManager) {
		this.bc = bc;
		this.applicationManager = applicationManager;
		this.networkFactory = networkFactory;
		this.networkManager = networkManager;
		this.cm = commandManager;
	}

	@Override
	public void run(TaskMonitor tm) throws Exception {

		ServiceReference sRef = bc.getServiceReference(HttpService.class.getName());
		if (sRef != null) {
			HttpService service = (HttpService) bc.getService(sRef);
			// create a default context to share between registrations
			final HttpContext httpContext = service.createDefaultHttpContext();
			// register the hello world servlet
			final Dictionary initParams = new Hashtable();
			initParams.put("from", "HttpService");
			service.registerServlet("/get/network", new GetNetworkServlet(applicationManager, networkFactory,
					networkManager), initParams, httpContext);

			// Run task
			service.registerServlet("/exec", new ExecuteTaskServlet(cm, applicationManager), initParams, httpContext);

			// register images as resources
			System.out.println("Servlet Start!");
		}

	}
}
