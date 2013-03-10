package org.cytoscape.rest.internal;

//import org.cytoscape.rest.internal.net.server.CytoBridgePostResponder;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.rest.internal.net.server.CytoBridgeGetResponder;
import org.cytoscape.rest.internal.net.server.CytoBridgePostResponder;
import org.cytoscape.rest.internal.net.server.CytoscapeGetResponder;
import org.cytoscape.rest.internal.task.StartGrizzlyServerTaskFactory;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.events.AddedNodeViewsListener;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.osgi.framework.BundleContext;

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

		final CytoscapeGetResponder cytoscapeGetResp = new CytoscapeGetResponder(netMan);

		CySwingApplication swingApp = getService(bc, CySwingApplication.class);
		final CytoBridgeAction cytoBridgeAction = new CytoBridgeAction(swingApp, myManager);

		registerService(bc, cytoBridgeAction, CyAction.class, new Properties());
		myManager.setListener(cytoBridgeAction);

		// Thread serverThread = new Thread() {
		// private LocalHttpServer server;
		//
		// @Override
		// public void run() {
		// server = new LocalHttpServer(2609,
		// Executors.newSingleThreadExecutor());
		// server.addPostResponder(cytoPostResp);
		// server.addGetResponder(cytoGetResp);
		// server.addGetResponder(cytoscapeGetResp);
		// server.run();
		// }
		// };
		// serverThread.setDaemon(true);
		// Executors.newSingleThreadExecutor().execute(serverThread);

		
//		final DataService dataService = new DataService(applicationManager, netFact, netMan);
		
		final CommandManager commandManager = new CommandManager();
		// Get all compatible tasks
		registerServiceListener(bc, commandManager, "addCommand", "removeCommand", TaskFactory.class);

//		// Add task to run
//		final StartHttpServerTaskFactory startHttpServerTaskFactory = new StartHttpServerTaskFactory(bc,
//				applicationManager, netFact, netMan, commandManager);
//		final Properties startHttpServerTaskFactoryProps = new Properties();
//		startHttpServerTaskFactoryProps.setProperty(PREFERRED_MENU, "Apps");
//		startHttpServerTaskFactoryProps.setProperty(MENU_GRAVITY, "1.2");
//		startHttpServerTaskFactoryProps.setProperty(TITLE, "Start Server");
//		registerService(bc, startHttpServerTaskFactory, TaskFactory.class, startHttpServerTaskFactoryProps);

		final StartGrizzlyServerTaskFactory startGrizzlyServerTaskFactory = new StartGrizzlyServerTaskFactory(netMan, netFact);
		final Properties startGrizzlyServerTaskFactoryProps = new Properties();
		startGrizzlyServerTaskFactoryProps.setProperty(PREFERRED_MENU, "Apps");
		startGrizzlyServerTaskFactoryProps.setProperty(MENU_GRAVITY, "1.2");
		startGrizzlyServerTaskFactoryProps.setProperty(TITLE, "Start REST Server");
		registerService(bc, startGrizzlyServerTaskFactory, TaskFactory.class, startGrizzlyServerTaskFactoryProps);
	
		
	}
}
