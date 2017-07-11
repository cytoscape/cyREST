package org.cytoscape.rest.internal.commands.resources;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.commands.handlers.MessageHandler;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskObserver;
import org.ops4j.pax.logging.spi.PaxAppender;
import org.ops4j.pax.logging.spi.PaxLevel;
import org.ops4j.pax.logging.spi.PaxLoggingEvent;

public class StringResultTaskObserver extends CommandResourceTaskObserver implements PaxAppender, TaskObserver {
	
	
	

	
	public StringResultTaskObserver (MessageHandler messageHandler) {
		super(messageHandler);
	}

	@Override
	public void taskFinished(ObservableTask t) {
		final Object res = t.getResults(String.class);
		if (res != null)
			messageHandler.appendResult(res);
	}


	@Override
	public void allFinished(FinishStatus status) {
		if (status.getType().equals(FinishStatus.Type.SUCCEEDED))
			messageHandler.appendMessage("Finished");
		else if (status.getType().equals(FinishStatus.Type.CANCELLED))
			messageHandler.appendWarning("Cancelled by user");
		else if (status.getType().equals(FinishStatus.Type.FAILED)) {
			if (status.getException() != null) {
				messageHandler.appendError("Failed: "
						+ status.getException().getMessage());
				taskException = new CustomFailureException("Failed: "
						+ status.getException().getMessage());
			} else {
				messageHandler.appendError("Failed");
				taskException = new CustomFailureException();
			}
		}
		synchronized(this) {
			finished = true;
			notify();
		}
	}
	
	public class CustomFailureException extends WebApplicationException {
		public CustomFailureException() {
			super(500);
		}

		public CustomFailureException(String message) {
			super(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(message).type("text/plain").build());
		}
	}
	
	public void doAppend(PaxLoggingEvent event) {
		// System.out.println(event.getLevel().toInt() + ": " + event.getMessage());
		// Get prefix
		// Handle levels
		

		PaxLevel level = event.getLevel();
		if (level.toInt() == 40000)
			messageHandler.appendError(event.getMessage());
		else if (level.toInt() == 30000)
			messageHandler.appendWarning(event.getMessage());
		else
			messageHandler.appendMessage(event.getMessage());
	}
}
