package org.cytoscape.rest.internal.commands.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.cytoscape.ci.CIErrorFactory;
import org.cytoscape.ci.model.CIError;
import org.cytoscape.rest.internal.CyRESTConstants;
import org.cytoscape.rest.internal.commands.handlers.MessageHandler;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskObserver;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.json.JSONResult;
import org.slf4j.Logger;

class JSONResultTaskObserver extends CommandResourceTaskObserver implements TaskObserver
{
	private final Logger logger;
	
	CIErrorFactory ciErrorFactory;
	/**
	 * @param commandResource
	 */
	JSONResultTaskObserver(MessageHandler messageHandler, CIErrorFactory ciErrorFactory, Logger logger) {
		super(messageHandler);
		this.ciErrorFactory = ciErrorFactory;
		this.logger = logger;
	}

	List<CIError> ciErrors = new ArrayList<CIError>();
	boolean succeeded = false;
	
	final List<String> jsonResultStrings = new ArrayList<String>();

	@Override
	public void taskFinished(ObservableTask task) {
		Class<? extends JSONResult> jsonResultClass = null;
		List<Class<?>> classList = task.getResultClasses();
		if (classList != null) {
			for (Class<?> clazz : classList) {
				if  (JSONResult.class.isAssignableFrom(clazz)) {
					jsonResultClass = (Class<? extends JSONResult>) clazz;
				}
			}
		}
		
		if (jsonResultClass != null)	{
			JSONResult jsonResult = task.getResults(jsonResultClass);
			jsonResultStrings.add(jsonResult.getJSON());
		}
	}

	@Override
	public void allFinished(FinishStatus finishStatus) {
		if (finishStatus.getType() == FinishStatus.Type.CANCELLED) {
			CIError ciError;
			if (finishStatus.getTask() != null && finishStatus.getTask() instanceof TunableValidator) {
				StringBuilder stringBuilder = new StringBuilder();
				if (((TunableValidator)finishStatus.getTask()).getValidationState(stringBuilder) == TunableValidator.ValidationState.INVALID)
				{
					ciError = ciErrorFactory.getCIError(
							Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
							CyRESTConstants.getErrorURI(CommandResource.JSON_COMMAND_RESOURCE_URI, 2), 
							"Task Cancelled. Could not validate Tunable inputs: " + stringBuilder.toString());
				} else {
					ciError = ciErrorFactory.getCIError(
							Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
							CyRESTConstants.getErrorURI(CommandResource.JSON_COMMAND_RESOURCE_URI, 2), 
							"Task Cancelled. All inputs were validated.");
				}
			} else {
				ciError = ciErrorFactory.getCIError(
						Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						CyRESTConstants.getErrorURI(CommandResource.JSON_COMMAND_RESOURCE_URI, 2), 
						"Task Cancelled.");
			}
			ciErrors.add(ciError);
		}
		else if (finishStatus.getType() == FinishStatus.Type.FAILED) {
			CIError ciError;
			if (finishStatus.getException() != null) {
				 ciError = ciErrorFactory.getCIError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						 CyRESTConstants.getErrorURI(CommandResource.JSON_COMMAND_RESOURCE_URI, 2), 
						finishStatus.getException().getMessage() == null ? finishStatus.getException().toString() : finishStatus.getException().getMessage()
						);
				 finishStatus.getException().printStackTrace(System.err);
			} else {
				 ciError = ciErrorFactory.getCIError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						 CyRESTConstants.getErrorURI(CommandResource.JSON_COMMAND_RESOURCE_URI, 2), 
							"Task Failed with No Exception: " + (finishStatus.getTask() != null ? finishStatus.getTask().getClass().getName() : "no attached class")
							);
			}
			ciErrors.add(ciError);
		} 
		else if (finishStatus.getType() == FinishStatus.Type.SUCCEEDED) {
			succeeded |= true;
		}
		synchronized(this) {
			finished = true;
			notify();
		}
	}
}