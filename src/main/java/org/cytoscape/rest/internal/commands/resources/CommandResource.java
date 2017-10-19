package org.cytoscape.rest.internal.commands.resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.cytoscape.ci.CIErrorFactory;
import org.cytoscape.ci.CIExceptionFactory;
import org.cytoscape.ci.CIResponseFactory;
import org.cytoscape.ci.model.CIError;
import org.cytoscape.command.AvailableCommands;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.rest.internal.CIErrorFactoryImpl;
import org.cytoscape.rest.internal.CyRESTConstants;
import org.cytoscape.rest.internal.commands.handlers.MessageHandler;
import org.cytoscape.rest.internal.commands.handlers.TextHTMLHandler;
import org.cytoscape.rest.internal.commands.handlers.TextPlainHandler;
import org.cytoscape.rest.internal.resource.CyRESTSwagger;
import org.cytoscape.rest.internal.task.LogLocation;
import org.cytoscape.work.SynchronousTaskManager;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import io.swagger.annotations.ApiParam;

/**
 * 
 * JAX-RS Resource for all Command-related API
 * 
 *
 */
@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.COMMANDS_TAG})
@Singleton
@Path("/v1/commands")
public class CommandResource
{
	private static final Logger logger = LoggerFactory.getLogger(CommandResource.class);
	
	@Inject
	@LogLocation
	protected URI logLocation;

	@Inject
	@NotNull
	private AvailableCommands available;

	@Inject
	@NotNull
	private CommandExecutorTaskFactory ceTaskFactory;

	@Inject
	@NotNull
	private SynchronousTaskManager<?> taskManager;

	@Inject
	protected CIResponseFactory ciResponseFactory;
	
	@Inject
	protected CIErrorFactory ciErrorFactory;
	
	@Inject
	protected CIExceptionFactory ciExceptionFactory;
	
	@GET
	@Path("/")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(value="List all available command namespaces",
	notes="Method handling HTTP GET requests to enumerate all namespaces. The"
			+ " returned list will be sent to the client as \"text/plain\" media type.")
	public String enumerateNamespaces() {
		final MessageHandler handler = new TextPlainHandler();
		final List<String> namespaces = available.getNamespaces();

		handler.appendCommand("Available namespaces:");
		for (final String namespace : namespaces) {
			handler.appendMessage("  " + namespace);
		}

		return handler.getMessageString();
	}

	@GET
	@Path("/")
	@Produces(MediaType.TEXT_HTML)
	@ApiOperation(value="List all available command namespaces",
	notes="Method handling HTTP GET requests to enumerate all namespaces. The"
			+ " returned list will be sent to the client as \"text/html\" media type.")
	public String enumerateNamespacesHtml() {
		final MessageHandler handler = new TextHTMLHandler();
		final List<String> namespaces = available.getNamespaces();

		handler.appendCommand("Available namespaces:");
		for (final String namespace : namespaces) {
			handler.appendMessage(namespace);
		}

		return handler.getMessageString();
	}


	/**
	 * Method to enumerate all commands for a given namespace
	 * 
	 * @param namespace
	 * @return list of commands as text/plain
	 */
	@GET
	@Path("/{namespace}")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(value="List all available commands in a namespace",
	notes="Method to enumerate all commands for a given namespace. The"
			+ " returned list will be sent to the client as \"text/plain\" media type.")
	public String enumerateCommands(@PathParam("namespace") String namespace) {
		final MessageHandler handler = new TextPlainHandler();
		final List<String> commands = available.getCommands(namespace);

		if (commands == null || commands.size() == 0)
			throw new WebApplicationException(404);

		handler.appendCommand("Available commands for '" + namespace + "':");
		for (final String command : commands) {
			handler.appendMessage("  " + command);
		}
		return handler.getMessageString();
	}


	/**
	 * Method to enumerate all commands for a given namespace
	 * 
	 * @param namespace
	 * @return list of commands as text/html
	 */
	@GET
	@Path("/{namespace}")
	@Produces(MediaType.TEXT_HTML)
	@ApiOperation(value="List all available commands in a namespace",
	notes="Method to enumerate all commands for a given namespace. The"
			+ " returned list will be sent to the client as \"text/html\" media type.")
	public String enumerateHTMLCommands(@PathParam("namespace") String namespace) {
		final MessageHandler handler = new TextHTMLHandler();
		final List<String> commands = available.getCommands(namespace);

		if (commands == null || commands.size() == 0)
			throw new WebApplicationException(404);

		handler.appendCommand("Available commands for '" + namespace + "':");
		for (final String command : commands) {
			handler.appendMessage("  " + command);
		}

		return handler.getMessageString();
	}

	@GET
	@Path("/{namespace}/{command}")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(value="Execute a command or list its arguments",
	notes="Method to enumerate all arguments for a given namespace and command or execute a namespace and "
			+ "command if query strings are provided.\n\nReturns a list of arguments as text/plain or the "
			+ "results of executing the command.")
	public String handleCommand(
			@ApiParam(value="Command Namespace") @PathParam("namespace") String namespace,
			@ApiParam(value="Command Name") @PathParam("command") String command, 
			@Context UriInfo uriInfo) {


		final MessageHandler handler = new TextPlainHandler();
		final MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters(true);

		return handleCommand(namespace, command, queryParameters, handler);
	}


	/**
	 * Method to enumerate all arguments for a given namespace and command or
	 * execute a namespace and command if query strings are provided
	 * 
	 * @param namespace
	 * @param command
	 * @param uriInfo
	 *            this provides access to the query strings
	 * @return list of arguments as text/html or the results of executing the
	 *         command
	 */
	@GET
	@Path("/{namespace}/{command}") 
	@Produces(MediaType.TEXT_HTML)
	@ApiOperation(value="Execute a command or list its arguments",
	notes="Method to enumerate all arguments for a given namespace and command or execute a namespace and "
			+ "command if query strings are provided.\n\nReturns a list of arguments as text/html or the "
			+ "results of executing the command.")
	public String handleHTMLCommand(
			@ApiParam(value="Command Namespace") @PathParam("namespace") String namespace,
			@ApiParam(value="Command Name") @PathParam("command") String command, 
			@Context UriInfo uriInfo) {
		final MessageHandler handler = new TextHTMLHandler();
		final MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters(true);

		return handleCommand(namespace, command, queryParameters, handler);
	}


	@POST
	@Path("/{namespace}/{command}") 
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Execute Command with JSON output", hidden=true)
	public Response handleJSONCommand(@PathParam("namespace") String namespace,
			@PathParam("command") String command, Map<String, Object> queryParameters) {
		final MessageHandler handler = new TextPlainHandler();
		//TODO Verify that all query parameters are strings. the command 

		/*
		for (String key : queryParameters.keySet()) {
			System.out.println("Parameter: " + key + " " + queryParameters.get(key));
		}*/

		JSONResultTaskObserver jsonTaskObserver = new JSONResultTaskObserver(handler, ciErrorFactory, logger);
		try {
			executeCommand(namespace, command, queryParameters, handler, jsonTaskObserver);
			return Response.status(jsonTaskObserver.ciErrors.isEmpty() ? 200 : 500).entity(buildCIResult(namespace, command, jsonTaskObserver, handler)).build();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	// We're getting strings from commands; it would cost us to translate to JSON and back to string, so we're building
	// our result manually for efficiency.
	private String buildCIResult(String namespace, String command, JSONResultTaskObserver jsonTaskObserver, MessageHandler messageHandler) {
		List<CIError> ciErrorList = new ArrayList<CIError>(jsonTaskObserver.ciErrors);

		if (jsonTaskObserver.succeeded == false) {
			ciErrorList.add(ciErrorFactory.getCIError(
					HttpStatus.INTERNAL_SERVER_ERROR_500.getStatusCode(), 
					CyRESTConstants.cyRESTCIRoot + ":handle-json-command" + CyRESTConstants.cyRESTCIErrorRoot +":2", 
					"Successful response was not returned."));
		}
		return getJSONResponse(jsonTaskObserver.jsonResultStrings, jsonTaskObserver.ciErrors, logLocation);
	}

	private final String handleCommand(final String namespace, final String command,
			final MultivaluedMap<String, String> queryParameters,
			final MessageHandler handler
			) throws WebApplicationException {

		final List<String> args = available.getArguments(namespace, command);

		if ((queryParameters != null && queryParameters.size() > 0)
				|| (args == null || args.size() == 0)) {
			// Execute!
			return executeCommand(namespace, command, queryParameters, handler);
		}

		handler.appendCommand("Available arguments for '" + namespace + " " + command + "':");
		for (final String arg : args) {
			handler.appendMessage("  " + arg);
		}
		return handler.getMessageString();
	}


	private final String executeCommand(
			final String namespace, final String command,
			final MultivaluedMap<String, String> args,
			final MessageHandler handler
			) throws WebApplicationException {

		final List<String> commands = available.getCommands(namespace);
		if (commands == null || commands.size() == 0) {
			throw new CustomNotFoundException("Error: no such namespace: '" + namespace + "'");
		}

		boolean nocom = true;
		for (String com : commands) {
			if (com.equalsIgnoreCase(command)) {
				nocom = false;
				break;
			}
		}
		if (nocom) {
			throw new CustomNotFoundException("Error: no such command: '" + command + "'");
		}

		List<String> argList = available.getArguments(namespace, command);
		Map<String, Object> modifiedSettings = new HashMap<String, Object>();

		for (String inputArg : args.keySet()) {
			boolean found = false;
			for (String arg : argList) {
				String[] bareArg = arg.split("=");
				if (bareArg[0].equalsIgnoreCase(inputArg)) {
					found = true;
					modifiedSettings.put(bareArg[0],
							stripQuotes(args.getFirst(inputArg)));
					break;
				}
			}
			if (!found) {
				throw new CustomNotFoundException(
						"Error: can't find argument '" + inputArg + "'");
			}
		}
		StringResultTaskObserver taskObserver = new StringResultTaskObserver(handler);
		return executeCommand(namespace, command, modifiedSettings, handler, taskObserver);
	}

	private final String executeCommand(
			final String namespace, final String command,
			final Map<String, Object> args,
			final MessageHandler handler,
			CommandResourceTaskObserver taskObserver) throws WebApplicationException {


		taskManager.execute(ceTaskFactory.createTaskIterator(namespace,
				command, args, taskObserver), taskObserver);

		synchronized (taskObserver) {
			try{
				while (!taskObserver.isFinished()) {
					System.out.println("Waiting for all tasks to finish at "+System.currentTimeMillis());
					taskObserver.wait();
				}
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		String messages = taskObserver.getMessageHandler().getMessageString();

		if (taskObserver.getTaskException() != null)
			throw taskObserver.getTaskException();
		return messages;
	}

	private final String stripQuotes(final String quotedString) {
		String tqString = quotedString.trim();
		if (tqString.startsWith("\"") && tqString.endsWith("\""))
			return tqString.substring(1, tqString.length() - 1);
		return tqString;
	}

	public static String getJSONResponse(List<String> jsonResultStrings, List<CIError> errors, URI logLocation) {

		final String NO_DATA_RESPONSE = "{\n \"data\": {},\n \"errors\":";
		
		StringBuilder jsonResultBuilder = new StringBuilder();
		Gson gson = new Gson();

		boolean jsonValid;
		try {
			for (String jsonResultString : jsonResultStrings) {
				gson.fromJson(jsonResultString, Object.class);
			}
			jsonValid = true;
		} catch (JsonSyntaxException e) {
			jsonValid = false;
			logger.error(e.getMessage(), e);
			
			CIErrorFactory ciErrorFactory = new CIErrorFactoryImpl(logLocation);
			CIError jsonSyntaxError = ciErrorFactory.getCIError(
					HttpStatus.INTERNAL_SERVER_ERROR_500.getStatusCode(), 
					CyRESTConstants.cyRESTCIRoot + ":handle-json-command" + CyRESTConstants.cyRESTCIErrorRoot +":3", 
					"Task returned invalid json.");
			errors.add(jsonSyntaxError);
		}

		if (jsonValid) {
			if (jsonResultStrings.size() == 0) {
				jsonResultBuilder.append(NO_DATA_RESPONSE);
			} else if (jsonResultStrings.size() == 1) {
				jsonResultBuilder.append("{\n \"data\": ");

				String jsonResult = jsonResultStrings.get(0);
				jsonResultBuilder.append(jsonResult);

				jsonResultBuilder.append(",\n \"errors\":");
			} else {
				jsonResultBuilder.append("{\n \"data\": [ ");

				for (int i = 0; i < jsonResultStrings.size(); i++) {
					String jsonResult = jsonResultStrings.get(i);
					jsonResultBuilder.append(jsonResult);
					if (i != jsonResultStrings.size() - 1) {
						jsonResultBuilder.append(",");
					}
				}
				jsonResultBuilder.append("],\n \"errors\":");
			}
		}
		else {
			jsonResultBuilder.append(NO_DATA_RESPONSE);
		}
		if (!errors.isEmpty()){
			jsonResultBuilder.append(gson.toJson(errors));
		} else {
			jsonResultBuilder.append("[]");
		}
		jsonResultBuilder.append("\n}");	
		return jsonResultBuilder.toString();
	}

	public class CustomNotFoundException extends WebApplicationException {
		public CustomNotFoundException() {
			super(404);
		}

		public CustomNotFoundException(String message) {
			super(Response.status(Response.Status.NOT_FOUND).entity(message)
					.type("text/plain").build());
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
}