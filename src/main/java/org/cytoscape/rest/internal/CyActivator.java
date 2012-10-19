package org.cytoscape.rest.internal;

//import org.cytoscape.rest.internal.net.server.CytoBridgePostResponder;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.rest.internal.net.server.CytoBridgeGetResponder;
import org.cytoscape.rest.internal.net.server.CytoBridgePostResponder;
import org.cytoscape.rest.internal.net.server.LocalHttpServer;
import org.cytoscape.rest.internal.servlet.HelloWorldServlet;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.events.AddedNodeViewsListener;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {

		// Importing Services:
		CyNetworkFactory netFact = getService(bc, CyNetworkFactory.class);
		CyNetworkManager netMan = getService(bc, CyNetworkManager.class);
		CyNetworkViewFactory netViewFact = getService(bc, CyNetworkViewFactory.class);
		CyNetworkViewManager netViewMan = getService(bc, CyNetworkViewManager.class);
		VisualMappingManager visMan = getService(bc, VisualMappingManager.class);
		CyApplicationManager applicationManager = getService(bc, CyApplicationManager.class);

		CyTableFactory tabFact = getService(bc, CyTableFactory.class);
		CyTableManager tabMan = getService(bc, CyTableManager.class);

		CyLayoutAlgorithmManager layMan = getService(bc, CyLayoutAlgorithmManager.class);

		// Create instance of NetworkManager with the factories/managers
		NetworkManager myManager = new NetworkManager(netFact, netViewFact, netMan, netViewMan, tabFact, tabMan);

		TaskManager tm = getService(bc, TaskManager.class);
		CyProperty cyPropertyServiceRef = getService(bc, CyProperty.class, "(cyPropertyName=cytoscape3.props)");

		NodeViewListener listen = new NodeViewListener(visMan, layMan, tm, cyPropertyServiceRef);
		registerService(bc, listen, AddedNodeViewsListener.class, new Properties());

		final CytoBridgeGetResponder cytoGetResp = new CytoBridgeGetResponder(myManager);
		final CytoBridgePostResponder cytoPostResp = new CytoBridgePostResponder(myManager);

		Thread serverThread = new Thread() {

			private LocalHttpServer server;

			@Override
			public void run() {
				server = new LocalHttpServer(2609, Executors.newSingleThreadExecutor());
				server.addPostResponder(cytoPostResp);
				server.addGetResponder(cytoGetResp);
				server.run();
			}
		};
		serverThread.setDaemon(true);
		Executors.newSingleThreadExecutor().execute(serverThread);

//		try {
//			startServer(bc, applicationManager);
//		} catch (ServletException e) {
//			e.printStackTrace();
//		} catch (NamespaceException e) {
//			e.printStackTrace();
//		}

	}

	private final void startServer(BundleContext bc, CyApplicationManager applicationManager) throws ServletException,
			NamespaceException {
		final ServiceReference m_httpServiceRef = bc.getServiceReference(HttpService.class.getName());
		if (m_httpServiceRef != null) {
			final HttpService httpService = (HttpService) bc.getService(m_httpServiceRef);
			if (httpService != null) {
				// create a default context to share between registrations
				final HttpContext httpContext = httpService.createDefaultHttpContext();
				// register the hello world servlet
				final Dictionary initParams = new Hashtable();
				initParams.put("from", "HttpService");
				httpService.registerServlet("/helloworld/hs", new HelloWorldServlet("/helloworld/hs", applicationManager), initParams,
						httpContext);
				httpService.registerServlet("/", new HelloWorldServlet("/", applicationManager), initParams, httpContext);
				// register images as resources
				httpService.registerResources("/images", "/images", httpContext);

				System.out.println("Servlet Start!");
			}
		}
	}

}
