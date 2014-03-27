package org.cytoscape.rest.internal.task;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task monitor for non-interactive task execution.
 *
 */
public class HeadlessTaskMonitor implements TaskMonitor {
	
	private static final Logger logger = LoggerFactory.getLogger(HeadlessTaskMonitor.class);

	private String taskName = "";

	public void setTask(final Task task) {
		this.taskName = "Task (" + task.toString() + ")";
	}

	
	@Override
	public void setTitle(final String title) {
		logger.info(taskName + " title: " + title);
	}

	
	@Override
	public void setStatusMessage(final String statusMessage) {
		logger.info(taskName + " status: " + statusMessage);
	}

	
	@Override
	public void setProgress(final double progress) {
		int prog = (int) Math.floor(progress * 100);
		logger.info(taskName + " % completed: " + prog);
	}


	@Override
	public void showMessage(Level level, String message) {
		// TODO Auto-generated method stub
		
	}
}
