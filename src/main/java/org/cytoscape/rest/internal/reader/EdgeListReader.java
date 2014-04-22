package org.cytoscape.rest.internal.reader;

import java.io.InputStream;

import org.cytoscape.io.read.AbstractCyNetworkReader;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskMonitor;

public class EdgeListReader extends AbstractCyNetworkReader{

	public EdgeListReader(InputStream inputStream, CyNetworkViewFactory cyNetworkViewFactory,
			CyNetworkFactory cyNetworkFactory, CyNetworkManager cyNetworkManager,
			CyRootNetworkManager cyRootNetworkManager) {
		super(inputStream, cyNetworkViewFactory, cyNetworkFactory, cyNetworkManager, cyRootNetworkManager);
		// TODO Auto-generated constructor stub
	}

	@Override
	public CyNetworkView buildCyNetworkView(CyNetwork network) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
