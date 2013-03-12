package org.cytoscape.rest.internal.task;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.CyShutdownEvent;
import org.cytoscape.application.events.CyShutdownListener;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.rest.TaskFactoryManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class StartGrizzlyServerTaskFactory extends AbstractTaskFactory implements CyShutdownListener {

	private final CyNetworkManager networkManager;
	private final CyNetworkFactory networkFactory;
	private final TaskFactoryManager tfManager;
	private final CyApplicationManager applicationManager;

	public StartGrizzlyServerTaskFactory(CyNetworkManager networkManager, final CyNetworkFactory networkFactory,
			final TaskFactoryManager tfManager, final CyApplicationManager applicationManager) {
		this.networkManager = networkManager;
		this.networkFactory = networkFactory;
		this.tfManager = tfManager;
		this.applicationManager = applicationManager;
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new StartGrizzlyServerTask(networkManager, networkFactory, tfManager, applicationManager));
	}

	@Override
	public void handleEvent(CyShutdownEvent ev) {
		// TODO stop server here.
	}

}
