package org.cytoscape.rest;

import org.cytoscape.task.NetworkCollectionTaskFactory;
import org.cytoscape.work.TaskFactory;

public interface TaskFactoryManager {
	
	/**
	 * Get task factory by ID metadata.
	 * 
	 * @param id
	 * @return Matching TaskFactory
	 */
	TaskFactory getTaskFactory(final String id);
	
	NetworkCollectionTaskFactory getNetworkCollectionTaskFactory(final String id);


}
