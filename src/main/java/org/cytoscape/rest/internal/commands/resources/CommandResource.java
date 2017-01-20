package org.cytoscape.rest.internal.commands.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.cytoscape.command.AvailableCommands;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.rest.internal.commands.handlers.MessageHandler;
import org.cytoscape.rest.internal.commands.handlers.TextHTMLHandler;
import org.cytoscape.rest.internal.commands.handlers.TextPlainHandler;
import org.cytoscape.rest.internal.task.CyRESTCommandSwaggerConfig;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskObserver;
import org.ops4j.pax.logging.spi.PaxAppender;
import org.ops4j.pax.logging.spi.PaxLevel;
import org.ops4j.pax.logging.spi.PaxLoggingEvent;

import io.swagger.annotations.Api;

/**
 * 
 * JAX-RS Resource for all Command-related API
 * 
 *
 */
@Api(tags = {"Commands"})
@Singleton
@Path("/v1/commands")
public class CommandResource implements PaxAppender, TaskObserver 
{

	@Context
	@NotNull
	private AvailableCommands available;

	@Context
	@NotNull
	private CommandExecutorTaskFactory ceTaskFactory;
	
	@Context
	@NotNull
	private SynchronousTaskManager<?> taskManager;


	private CustomFailureException taskException;
	private MessageHandler messageHandler;
	private boolean processingCommand = false;



	/**
	 * Method handling HTTP GET requests to enumerate all namespaces. The
	 * returned list will be sent to the client as "text/plain" media type.
	 * 
	 * @return String that will be returned as a text/plain response.
	 */
	@GET
	@Path("/")
	@Produces(MediaType.TEXT_PLAIN)
	public String enumerateNamespaces() {
		final MessageHandler handler = new TextPlainHandler();
		final List<String> namespaces = available.getNamespaces();

		handler.appendCommand("Available namespaces:");
		for (final String namespace : namespaces) {
			handler.appendMessage("  " + namespace);
		}

		return handler.getMessages();
	}

	/**
	 * Method handling HTTP GET requests to enumerate all namespaces. The
	 * returned list will be sent to the client as "text/html" media type.
	 * 
	 * @return String that will be returned as a text/html response.
	 */
	@GET
	@Path("/")
	@Produces(MediaType.TEXT_HTML)
	public String enumerateNamespacesHtml() {
		final MessageHandler handler = new TextHTMLHandler();
		final List<String> namespaces = available.getNamespaces();

		handler.appendCommand("Available namespaces:");
		for (final String namespace : namespaces) {
			handler.appendMessage(namespace);
		}

		return handler.getMessages();
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
	public String enumerateCommands(@PathParam("namespace") String namespace) {
		final MessageHandler handler = new TextPlainHandler();
		final List<String> commands = available.getCommands(namespace);

		if (commands == null || commands.size() == 0)
			throw new WebApplicationException(404);

		handler.appendCommand("Available commands for '" + namespace + "':");
		for (final String command : commands) {
			handler.appendMessage("  " + command);
		}
		return handler.getMessages();
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
	public String enumerateHTMLCommands(@PathParam("namespace") String namespace) {
		final MessageHandler handler = new TextHTMLHandler();
		final List<String> commands = available.getCommands(namespace);

		if (commands == null || commands.size() == 0)
			throw new WebApplicationException(404);

		handler.appendCommand("Available commands for '" + namespace + "':");
		for (final String command : commands) {
			handler.appendMessage("  " + command);
		}
		
		return handler.getMessages();
	}


	/**
	 * Method to enumerate all arguments for a given namespace and command or
	 * execute a namespace and command if query strings are provided
	 * 
	 * @param namespace
	 * @param command
	 * @param uriInfo
	 *            this provides access to the query strings
	 * @return list of arguments as text/plain or the results of executing the
	 *         command
	 */
	@GET
	@Path("/{namespace}/{command}")
	@Produces(MediaType.TEXT_PLAIN)
	public String handleCommand(@PathParam("namespace") String namespace,
			@PathParam("command") String command, @Context UriInfo uriInfo) {
		
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
	@Path("/{namespace}/{command}") // Be wary of changing this, as SwaggerApiConfig hides it in favor of listing all 
	//available commands.
	@Produces(MediaType.TEXT_HTML)
	public String handleHTMLCommand(@PathParam("namespace") String namespace,
			@PathParam("command") String command, @Context UriInfo uriInfo) {
		final MessageHandler handler = new TextHTMLHandler();
		final MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters(true);
		
		return handleCommand(namespace, command, queryParameters, handler);
	}


	private final String handleCommand(final String namespace, final String command,
			final MultivaluedMap<String, String> queryParameters,
			final MessageHandler handler) throws WebApplicationException {
		
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
		return handler.getMessages();
	}


	private final String executeCommand(
			final String namespace, final String command,
			final MultivaluedMap<String, String> args,
			final MessageHandler handler) throws WebApplicationException {

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

		processingCommand = true;
		messageHandler = handler;
		taskException = null;

		taskManager.execute(ceTaskFactory.createTaskIterator(namespace,
				command, modifiedSettings, this), this);

		String messages = messageHandler.getMessages();
		processingCommand = false;
		if (taskException != null)
			throw taskException;
		return messages;
	}

	public void doAppend(PaxLoggingEvent event) {
		// System.out.println(event.getLevel().toInt() + ": " + event.getMessage());
		// Get prefix
		// Handle levels
		if (!processingCommand) {
			return;
		}

		PaxLevel level = event.getLevel();
		if (level.toInt() == 40000)
			messageHandler.appendError(event.getMessage());
		else if (level.toInt() == 30000)
			messageHandler.appendWarning(event.getMessage());
		else
			messageHandler.appendMessage(event.getMessage());
	}

	////////////////// For Observable Task //////////////////////////
	
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
	}

	private final String stripQuotes(final String quotedString) {
		String tqString = quotedString.trim();
		if (tqString.startsWith("\"") && tqString.endsWith("\""))
			return tqString.substring(1, tqString.length() - 1);
		return tqString;
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
