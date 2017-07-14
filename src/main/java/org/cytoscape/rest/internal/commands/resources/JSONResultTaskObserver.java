package org.cytoscape.rest.internal.commands.resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.cytoscape.ci.CIErrorFactory;
import org.cytoscape.ci.model.CIError;
import org.cytoscape.rest.internal.CIErrorFactoryImpl;
import org.cytoscape.rest.internal.CyRESTConstants;
import org.cytoscape.rest.internal.commands.handlers.MessageHandler;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskObserver;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.json.JSONResult;
import org.glassfish.grizzly.http.util.HttpStatus;

import com.google.gson.Gson;

class JSONResultTaskObserver extends CommandResourceTaskObserver implements TaskObserver
{
	URI logLocation;
	/**
	 * @param commandResource
	 */
	JSONResultTaskObserver(MessageHandler messageHandler, URI logLocation) {
		super(messageHandler);
		this.logLocation = logLocation;
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
		} else {
			Gson gson = new Gson();
			String stringResult = task.getResults(String.class);
			jsonResultStrings.add(gson.toJson(stringResult, String.class));
		}
	}

	@Override
	public void allFinished(FinishStatus finishStatus) {
		if (finishStatus.getType() == FinishStatus.Type.CANCELLED) {
			CIErrorFactory ciErrorFactory = new CIErrorFactoryImpl(logLocation);
			CIError ciError;
			if (finishStatus.getTask() != null && finishStatus.getTask() instanceof TunableValidator) {
				StringBuilder stringBuilder = new StringBuilder();
				if (((TunableValidator)finishStatus.getTask()).getValidationState(stringBuilder) == TunableValidator.ValidationState.INVALID)
				{
					ciError = ciErrorFactory.getCIError(
							HttpStatus.INTERNAL_SERVER_ERROR_500.getStatusCode(), 
							CyRESTConstants.cyRESTCIRoot + ":handle-json-command" + CyRESTConstants.cyRESTCIErrorRoot +":2", 
							"Task Cancelled. Could not validate Tunable inputs: " + stringBuilder.toString());
				} else {
					ciError = ciErrorFactory.getCIError(
							HttpStatus.INTERNAL_SERVER_ERROR_500.getStatusCode(), 
							CyRESTConstants.cyRESTCIRoot + ":handle-json-command" + CyRESTConstants.cyRESTCIErrorRoot +":2", 
							"Task Cancelled. All inputs were validated.");
				}
			} else {
				ciError = ciErrorFactory.getCIError(
						HttpStatus.INTERNAL_SERVER_ERROR_500.getStatusCode(), 
						CyRESTConstants.cyRESTCIRoot + ":handle-json-command" + CyRESTConstants.cyRESTCIErrorRoot +":2", 
						"Task Cancelled.");
			}
			ciErrors.add(ciError);
		}
		else if (finishStatus.getType() == FinishStatus.Type.FAILED) {
			CIErrorFactory ciErrorFactory = new CIErrorFactoryImpl(logLocation);
			CIError ciError;
			if (finishStatus.getException() != null) {
				 ciError = ciErrorFactory.getCIError(HttpStatus.INTERNAL_SERVER_ERROR_500.getStatusCode(), 
						CyRESTConstants.cyRESTCIRoot + ":handle-json-command" + CyRESTConstants.cyRESTCIErrorRoot +":2", 
						finishStatus.getException().getMessage()
						);
			} else {
				 ciError = ciErrorFactory.getCIError(HttpStatus.INTERNAL_SERVER_ERROR_500.getStatusCode(), 
							CyRESTConstants.cyRESTCIRoot + ":handle-json-command" + CyRESTConstants.cyRESTCIErrorRoot +":2", 
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