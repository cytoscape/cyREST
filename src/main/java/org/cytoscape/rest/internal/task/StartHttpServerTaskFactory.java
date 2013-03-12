package org.cytoscape.rest.internal.task;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.rest.internal.TaskFactoryManagerImpl;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.osgi.framework.BundleContext;

public class StartHttpServerTaskFactory extends AbstractTaskFactory {

	private final BundleContext bc;
	private final CyApplicationManager applicationManager;
	private final CyNetworkFactory networkFactory;
	private final CyNetworkManager networkManager;
	private final TaskFactoryManagerImpl commandManager;

	public StartHttpServerTaskFactory(final BundleContext bc, final CyApplicationManager applicationManager,
			final CyNetworkFactory networkFactory, final CyNetworkManager networkManager, final TaskFactoryManagerImpl commandManager) {
		this.bc = bc;
		this.applicationManager = applicationManager;
		this.networkFactory = networkFactory;
		this.networkManager = networkManager;
		this.commandManager = commandManager;
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new StartHttpServerTask(bc, applicationManager, networkFactory, networkManager, commandManager));
	}

}
