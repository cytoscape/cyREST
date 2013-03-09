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

public class ExecuteTaskServlet extends HttpServlet {

	private static final long serialVersionUID = 5067580373453129029L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		final String taskName = request.getParameter("task");
		System.out.println("Got Executing Task: " + taskName);

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