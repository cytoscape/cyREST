package org.cytoscape.rest.internal.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.rest.internal.CommandManager;
import org.cytoscape.rest.internal.task.RestTaskManager;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

public class ExecuteTaskServlet extends HttpServlet {

	private static final long serialVersionUID = 5067580373453129029L;

	private final CommandManager manager;
	
	private final CyApplicationManager applicationManager;
	
	public ExecuteTaskServlet(CommandManager manager, final CyApplicationManager applicationManager) {
		this.manager = manager;
		this.applicationManager = applicationManager;
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		final String taskName = request.getParameter("task");
		System.out.println("Got Executing Task: " + taskName);

		if(taskName != null) {
			TaskFactory tf = manager.getTaskFactory(taskName);
			if(tf!=null) {
				TaskIterator ti;
				if(tf instanceof NetworkTaskFactory) {
					NetworkTaskFactory ntf = (NetworkTaskFactory) tf;
					ti = ntf.createTaskIterator(applicationManager.getCurrentNetwork());
				} else {
					ti = tf.createTaskIterator();
				}
				
				TaskManager tm = new RestTaskManager();
				tm.execute(ti);
				System.out.println("Finished Task: " + taskName);
			}
		}
		
	
		final PrintWriter writer = response.getWriter();
		writer.println("Created = " + taskName);
		writer.close();
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		response.setContentType(GetNetworkServlet.TYPE_JSON);

		System.out.println("Got POST: ");

		final ServletInputStream is = request.getInputStream();
		final BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line;
		final StringBuilder builder = new StringBuilder();
		while ((line = br.readLine()) != null)
			builder.append(line);

		br.close();
		is.close();

		final PrintWriter writer = response.getWriter();
		// writer.println("Created = " + network.getSUID());
		writer.close();

	}

}