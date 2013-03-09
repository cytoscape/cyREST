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
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.rest.internal.translator.CyNetwork2JSONTranslator;
import org.cytoscape.rest.internal.translator.JSON2CyNetworkTranslator;

public class GetNetworkServlet extends HttpServlet {

	private static final long serialVersionUID = -4172672399828471997L;

	static final String TYPE_JSON = "application/json";

	private final CyApplicationManager applicationManager;
	private final CyNetworkManager networkManager;

	private final CyNetwork2JSONTranslator network2jsonTranslator;
	private final JSON2CyNetworkTranslator json2CyNetworkTranslator;

	public GetNetworkServlet(final CyApplicationManager applicationManager, final CyNetworkFactory networkFactory,
			final CyNetworkManager networkManager) {
		this.applicationManager = applicationManager;
		this.networkManager = networkManager;

		network2jsonTranslator = new CyNetwork2JSONTranslator();
		json2CyNetworkTranslator = new JSON2CyNetworkTranslator(networkFactory);
	}

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
			IOException {

		final String suid = request.getParameter(CyIdentifiable.SUID.toLowerCase());

		final CyNetwork curNetwork = applicationManager.getCurrentNetwork();
		response.setContentType(TYPE_JSON);

		// Return current network as JSON
		final PrintWriter writer = response.getWriter();
		writer.println(network2jsonTranslator.translate(curNetwork));
		writer.close();
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		System.out.println("Got POST: " + request.getParameter("suid"));

		final ServletInputStream is = request.getInputStream();
		final BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line;
		final StringBuilder builder = new StringBuilder();
		while ((line = br.readLine()) != null)
			builder.append(line);

		br.close();
		is.close();

		final CyNetwork network = json2CyNetworkTranslator.translate(builder.toString());
		network.getRow(network).set(CyNetwork.NAME, "From node.js");
		networkManager.addNetwork(network);
		
		final PrintWriter writer = response.getWriter();
		writer.println("Created = " + network.getSUID());
		writer.close();

	}

}