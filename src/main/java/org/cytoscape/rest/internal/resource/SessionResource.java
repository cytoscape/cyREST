package org.cytoscape.rest.internal.resource;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.model.Message;
import org.cytoscape.rest.internal.model.FileModel;
import org.cytoscape.rest.internal.model.SessionName;
import org.cytoscape.rest.internal.task.HeadlessTaskMonitor;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.task.create.NewSessionTaskFactory;
import org.cytoscape.task.read.OpenSessionTaskFactory;
import org.cytoscape.task.write.SaveSessionAsTaskFactory;
import org.cytoscape.task.write.SaveSessionTaskFactory;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;

import com.google.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.SESSION_TAG})
@Singleton
@Path("/v1/session")
public class SessionResource extends AbstractResource {

	@Inject
	@NotNull
	private CySessionManager sessionManager;

	
	private SaveSessionTaskFactory saveSessionTaskFactory;

	@Inject
	@NotNull
	private SaveSessionAsTaskFactory saveSessionAsTaskFactory;

	@Inject
	@NotNull
	private OpenSessionTaskFactory openSessionTaskFactory;

	@Inject
	@NotNull
	private NewSessionTaskFactory newSessionTaskFactory;


	public SessionResource() {
		super();
	}


	@GET
	@Path("/name")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get current session file name",
    notes = "Returns the file name for the current Cytoscape session.",
    response = SessionName.class)
	public SessionName getSessionName() 
	{
		String sessionName = sessionManager.getCurrentSessionFileName();
		if(sessionName == null || sessionName.isEmpty()) {
			sessionName = "";
		}
		return new SessionName(sessionName);
	}


	
	@DELETE
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Delete current Session",
    notes = "This deletes the current session and initializes a new one. A message is returned to indicate the success of the deletion.",
    response = Message.class)
	public Message deleteSession() {

		try {
			TaskIterator itr = newSessionTaskFactory.createTaskIterator(true);
			while(itr.hasNext()) {
				final Task task = itr.next();
				task.run(new HeadlessTaskMonitor());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw getError("Could not delete current session.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
		return new Message("New session created.");
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Load a Session from a local file",
    notes = "Loads a session from a local file and returns the session file name",
    response = FileModel.class)
	public FileModel getSessionFromFile(@ApiParam(value = "Session file location as an absolute path", required = true) @QueryParam("file") String file) throws IOException
	{
		File sessionFile = null;
		try {
			sessionFile = new File(file);
			TaskIterator itr = openSessionTaskFactory.createTaskIterator(sessionFile);
			while(itr.hasNext()) {
				final Task task = itr.next();
				task.run(new HeadlessTaskMonitor());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw getError("Could not open session.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
		return new FileModel(sessionFile.getAbsolutePath());
	}

	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Save current Session to a file",
    notes = "Saves the current session to a file. If successful, the session file location will be returned.",
    response = FileModel.class)
	public FileModel createSessionFile(@ApiParam(value = "Session file location as an absolute path", required = true) @QueryParam("file") String file) {
		File sessionFile = null;
		try {
			sessionFile = new File(file);
			TaskIterator itr = saveSessionAsTaskFactory.createTaskIterator(sessionFile);
			while(itr.hasNext()) {
				final Task task = itr.next();
				task.run(new HeadlessTaskMonitor());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw getError("Could not save session.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	
		return new FileModel(sessionFile.getAbsolutePath());
	}
}
