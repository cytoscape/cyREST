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
import org.osgi.service.log.LogLevel;

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
		String message = event.getMessage();
		switch(event.getLevel().toLevel()) {
		case AUDIT:
		case ERROR:
			messageHandler.appendError(message);
			break;
		case WARN:
			messageHandler.appendWarning(message);
			break;
		default:
			messageHandler.appendMessage(message);
			break;
		}
	}
}
