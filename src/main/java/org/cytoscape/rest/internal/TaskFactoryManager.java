package org.cytoscape.rest.internal;

import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.task.NetworkCollectionTaskFactory;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.work.TaskFactory;

public interface TaskFactoryManager {
	
	/**
	 * Get task factory by ID meta data.
	 * 
	 * @param id
	 * @return Matching TaskFactory
	 */
	TaskFactory getTaskFactory(final String id);
	
	NetworkTaskFactory getNetworkTaskFactory(final String id);
	
	NetworkCollectionTaskFactory getNetworkCollectionTaskFactory(final String id);

	InputStreamTaskFactory getInputStreamTaskFactory(final String id);

}
