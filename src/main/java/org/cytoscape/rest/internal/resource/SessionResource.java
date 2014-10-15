package org.cytoscape.rest.internal.resource;

import java.io.File;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.task.HeadlessTaskMonitor;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.task.create.NewSessionTaskFactory;
import org.cytoscape.task.read.OpenSessionTaskFactory;
import org.cytoscape.task.write.SaveSessionAsTaskFactory;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;



@Singleton
@Path("/v1/session")
public class SessionResource extends AbstractResource {

	@Context
	@NotNull
	private CySessionManager sessionManager;
	
	@Context
	@NotNull
	private SaveSessionAsTaskFactory saveSessionAsTaskFactory;
	
	@Context
	@NotNull
	private OpenSessionTaskFactory openSessionTaskFactory;
	
	@Context
	@NotNull
	private NewSessionTaskFactory newSessionTaskFactory;


	public SessionResource() {
		super();
	}


	/**
	 * 
	 * @summary Get current session name
	 * 
	 * @return Current session name
	 */
	@GET
	@Path("/name")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSessionName() {
		String sessionName = sessionManager.getCurrentSessionFileName();
		if(sessionName == null || sessionName.isEmpty()) {
			sessionName = "";
		}
		
		return "{\"name\": \"" + sessionName +"\"}";
	}
	
	
	/**
	 * 
	 * @summary Delete current session and start new one
	 * 
	 * @return Success message
	 * 
	 */
	@DELETE
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteSession() {
		
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
		return "{\"message\": \"New session created.\"}";
	}
	
	
	/**
	 * This get method save the current session into a local file system.
	 * Make sure you have a write permission to the directory.
	 * 
	 * @summary Save session to a local file
	 * 
	 * @param file File name as (should be absolute path)
	 * 
	 * @return Session file name as string
	 * 
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSessionAsFile(@QueryParam("file") String file) {
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
	
		return "{\"name\": \"" + sessionFile.getAbsolutePath() +"\"}";
	}
	
	
	/**
	 * 
	 * @summary Create a session from a CYS file
	 * 
	 * @param file Session file location (should be absolute path)
	 * 
	 * @return Session file name loaded to the current instance of Cytoscape
	 */
	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String createSessionFromFile(@QueryParam("file") String file) {
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
			throw getError("Could not load session.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	
		return "{\"name\": \"" + sessionFile.getAbsolutePath() +"\"}";
	}
}
