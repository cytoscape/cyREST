package org.cytoscape.rest.internal.net.server;

import java.util.HashMap;
import java.util.Map;
import org.cytoscape.rest.internal.NetworkManager;
import org.cytoscape.rest.internal.json.JSONCommand;
import org.cytoscape.rest.internal.net.server.LocalHttpServer.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * This class is responsible for handling POST requests received by the local
 * HTTP server.
 */
public class CytoBridgeGetResponder implements LocalHttpServer.GetResponder {

	private static final String PACKAGE = "org.cytoscape.rest.internal.json.";

	private final static Logger logger = LoggerFactory.getLogger(CytoBridgeGetResponder.class);

	private NetworkManager myManager;

	public CytoBridgeGetResponder(NetworkManager myManager) {
		this.myManager = myManager;
	}

	public boolean canRespondTo(String url) throws Exception {
		return (url.indexOf("cytobridge") >= 0);
	}

	public Response respond(String url) throws Exception {
		// System.out.println("GET url     : " + url);
		String[] full = url.split("/");
		int i = 0;
		while (i < full.length) {
			if (full[i].equals("cytobridge"))
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

		System.out.println("GET command : " + namespace);

		Gson gson = new Gson();

		JSONCommand jcom = (JSONCommand) gson.fromJson(command, Class.forName(PACKAGE + namespace));
		jcom.run(myManager);

		Map<String, String> responseData = new HashMap<String, String>();

		responseData.put("namespace", namespace);
		// responseData.put("command",command);
		// responseData.put("args",args);
		// responseData.put("status","success");

		String responseBody = responseData.toString();
		responseBody += "\n";
		LocalHttpServer.Response response = new LocalHttpServer.Response(responseBody, "application/json");
		return response;
	}
}
