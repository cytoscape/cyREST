package org.cytoscape.rest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.cytoscape.app.event.AppsFinishedStartingEvent;
import org.cytoscape.rest.internal.task.AllAppsStartedListener;
import org.junit.Test;

public class AllAppsStartedListenerTest 
{
	
	@Test
	public void testAppsStartedListener() 
	{
		AllAppsStartedListener listener = new AllAppsStartedListener();
		assertFalse(listener.getAllAppsStarted());
		AppsFinishedStartingEvent event = new AppsFinishedStartingEvent(new Object());
		listener.handleEvent(event);
		assertTrue(listener.getAllAppsStarted());
	}
}
