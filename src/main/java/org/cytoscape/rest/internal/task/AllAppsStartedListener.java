package org.cytoscape.rest.internal.task;

import org.cytoscape.app.event.AppsFinishedStartingEvent;
import org.cytoscape.app.event.AppsFinishedStartingListener;

public class AllAppsStartedListener implements AppsFinishedStartingListener{

	private boolean allAppsStarted = false;
	
	public boolean getAllAppsStarted() {
		return allAppsStarted;
	}

	@Override
	public void handleEvent(AppsFinishedStartingEvent arg0) {
		allAppsStarted = true;
	}

}
