package org.cytoscape.rest.internal.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.rest.internal.translator.CyNetwork2JSONTranslator;

public class GetNetworkServlet extends HttpServlet {

	private final String m_registrationPath;

	private final CyApplicationManager applicationManager;

	CyNetwork2JSONTranslator network2jsonTranslator;

	public GetNetworkServlet(final String registrationPath, final CyApplicationManager applicationManager) {

		m_registrationPath = registrationPath;
		this.applicationManager = applicationManager;
		network2jsonTranslator = new CyNetwork2JSONTranslator();
	}

	
	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
			IOException {

		String id = request.getParameter("id");
		
		request.getAttributeNames();
		
		final CyNetwork curNetwork = applicationManager.getCurrentNetwork();
		response.setContentType("text/html");

		final PrintWriter writer = response.getWriter();
		writer.println("<html><body align='center'>");
		writer.println("<h1>Current Network</h1>");
		writer.println("<h1>" + getServletConfig().getInitParameter("from") + "</h1>");
		writer.println("<p>");
		writer.println("Served by servlet registered at: " + m_registrationPath);
		writer.println("<br/>");
		writer.println("Servlet Path: " + request.getServletPath());
		writer.println("<br/> Network ID = " + id);
		writer.println("Current Network = " + curNetwork.getRow(curNetwork).get(CyNetwork.NAME, String.class));
		writer.println("<br/>");
		writer.println(network2jsonTranslator.translate(curNetwork));
		writer.println("Path Info: " + request.getPathInfo());
		writer.println("</p>");
		writer.println("</body></html>");
		writer.close();
	}

}