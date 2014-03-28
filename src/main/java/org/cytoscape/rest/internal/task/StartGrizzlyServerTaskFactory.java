package org.cytoscape.rest.internal.task;

import org.cytoscape.application.events.CyShutdownEvent;
import org.cytoscape.application.events.CyShutdownListener;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.glassfish.hk2.utilities.Binder;

public class StartGrizzlyServerTaskFactory extends AbstractTaskFactory implements CyShutdownListener {

	private final Binder binder;

	public StartGrizzlyServerTaskFactory(final Binder binder) {
		this.binder = binder;
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new StartGrizzlyServerTask(binder));
	}

	@Override
	public void handleEvent(CyShutdownEvent ev) {
		// TODO stop server here.
	}

}
