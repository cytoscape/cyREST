package org.cytoscape.rest.internal.task;

import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class StartGrizzlyServerTaskFactory extends AbstractTaskFactory {

	private final CyNetworkManager networkManager;
	private final CyNetworkFactory networkFactory;

	public StartGrizzlyServerTaskFactory(CyNetworkManager networkManager, final CyNetworkFactory networkFactory) {
		this.networkManager = networkManager;
		this.networkFactory = networkFactory;
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new StartGrizzlyServerTask(networkManager, networkFactory));
	}

}
