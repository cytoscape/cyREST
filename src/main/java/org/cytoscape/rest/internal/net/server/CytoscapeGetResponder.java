package org.cytoscape.rest.internal.net.server;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.rest.internal.net.server.LocalHttpServer.Response;
import org.cytoscape.rest.internal.translator.CyNetwork2JSONTranslator;
import org.cytoscape.rest.internal.translator.CyNode2JSONTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for handling POST requests received by the local
 * HTTP server.
 */
public class CytoscapeGetResponder implements LocalHttpServer.GetResponder {

	private static final String PACKAGE = "org.cytoscape.rest.internal.json.";

	private final static Logger logger = LoggerFactory.getLogger(CytoBridgeGetResponder.class);

	private CyNetworkManager netMan;

	public CytoscapeGetResponder(CyNetworkManager netMan) {
		this.netMan = netMan;
	}

	public boolean canRespondTo(String url) throws Exception {
		return (url.indexOf("cytoscape") >= 0);
	}

	public Response respond(String url) throws Exception {
		// System.out.println("GET url     : " + url);
		String[] full = url.split("/");
		int i = 0;
		while (i < full.length) {
			if (full[i].equals("cytoscape"))
				break;
			i++;
		}

		String namespace = full[++i];
		String command = full[++i];
		String args = "";
		while (i < full.length - 1)
			args = args + full[++i] + "/";
		args = args.replaceAll("&", " ");

		String fullCommand = namespace + " " + command + " " + args;

		System.out.println("GET command : " + fullCommand);

		Map<String, String> responseData = new HashMap<String, String>();

		responseData.put("namespace", namespace);
		responseData.put("command", command);
		responseData.put("args", args);
		responseData.put("status", "failure");
		if ("network".equals(namespace)) {
			CyNetwork2JSONTranslator translator = new CyNetwork2JSONTranslator();
			responseData.put("data", translator.translate(netMan.getNetwork(Long.parseLong(command))));
			responseData.put("status", "success");
		} else if ("node".equals(namespace)) {
			CyNode2JSONTranslator translator = new CyNode2JSONTranslator();
			for (CyNetwork net : netMan.getNetworkSet()) {
				CyNode node;
				if ((node = net.getNode(Long.parseLong(command))) != null) {
					responseData.put("data", translator.translate(node));
					responseData.put("status", "success");
				}
			}

		}

		String responseBody = responseData.toString();
		responseBody += "\n";
		LocalHttpServer.Response response = new LocalHttpServer.Response(responseBody, "application/json");
		return response;
	}
}
