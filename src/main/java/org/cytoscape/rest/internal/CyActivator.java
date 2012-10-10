package org.cytoscape.rest.internal;

//import org.cytoscape.rest.internal.net.server.CytoBridgePostResponder;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.rest.internal.net.server.CytoBridgeGetResponder;
import org.cytoscape.rest.internal.net.server.CytoBridgePostResponder;
import org.cytoscape.rest.internal.net.server.LocalHttpServer;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.events.AddedNodeViewsListener;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.osgi.framework.BundleContext;


import java.util.Properties;
import java.util.concurrent.Executors;

public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {

		CyNetworkFactory netFact = getService(bc,CyNetworkFactory.class);
		CyNetworkManager netMan = getService(bc,CyNetworkManager.class);
		CyNetworkViewFactory netViewFact = getService(bc,CyNetworkViewFactory.class);
		CyNetworkViewManager netViewMan = getService(bc,CyNetworkViewManager.class);
		VisualMappingManager visMan = getService(bc,VisualMappingManager.class);
		
		CyTableFactory tabFact = getService(bc,CyTableFactory.class);
		CyTableManager tabMan = getService(bc,CyTableManager.class);
		
		CyLayoutAlgorithmManager layMan = getService(bc,CyLayoutAlgorithmManager.class);
		
		//Create instance of NetworkManager with the factories/managers
		NetworkManager myManager = new NetworkManager(netFact, netViewFact, netMan, netViewMan, tabFact, tabMan);
		
		TaskManager tm = getService(bc, TaskManager.class);
		CyProperty cyPropertyServiceRef = getService(bc,CyProperty.class,"(cyPropertyName=cytoscape3.props)");
		
		NodeViewListener listen = new NodeViewListener(visMan, layMan, tm, cyPropertyServiceRef);
		registerService(bc,listen,AddedNodeViewsListener.class, new Properties());
		
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
		
	}
}
