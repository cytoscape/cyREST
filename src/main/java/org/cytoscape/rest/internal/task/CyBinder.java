package org.cytoscape.rest.internal.task;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.rest.TaskFactoryManager;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class CyBinder extends AbstractBinder {

	private final CyNetworkManager networkManager;
	private final CyNetworkFactory networkFactory;
	private final TaskFactoryManager tfManager;
	private final CyApplicationManager applicationManager;

	public CyBinder(final CyNetworkManager networkManager, final CyNetworkFactory networkFactory,
			final TaskFactoryManager tfManager, final CyApplicationManager applicationManager) {
		this.networkManager = networkManager;
		this.networkFactory = networkFactory;
		this.tfManager = tfManager;
		this.applicationManager = applicationManager;
	}

	@Override
	protected void configure() {
		bind(networkManager).to(CyNetworkManager.class);
		bind(networkFactory).to(CyNetworkFactory.class);
		bind(tfManager).to(TaskFactoryManager.class);
		bind(applicationManager).to(CyApplicationManager.class);
	}

}
