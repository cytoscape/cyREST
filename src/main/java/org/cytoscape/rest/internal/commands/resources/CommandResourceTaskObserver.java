package org.cytoscape.rest.internal.commands.resources;

import org.cytoscape.rest.internal.commands.handlers.MessageHandler;
import org.cytoscape.rest.internal.commands.resources.StringResultTaskObserver.CustomFailureException;
import org.cytoscape.work.TaskObserver;

public abstract class CommandResourceTaskObserver implements TaskObserver {
	
	protected CustomFailureException taskException;
	
	public CustomFailureException getTaskException() {
		return taskException;
	}
	
	protected boolean finished;
	
	public boolean isFinished() {
		return finished;
	}
	
	public MessageHandler getMessageHandler() {
		return messageHandler;
	}

	protected MessageHandler messageHandler;
	
	public CommandResourceTaskObserver(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
		finished = false;
	}

}
