package org.cytoscape.rest.internal.net.server;

	import java.util.HashMap;
	import java.util.Map;
	import java.net.URLDecoder;

	import org.cytoscape.rest.internal.NetworkManager;
import org.cytoscape.rest.internal.json.JSONCommand;
	import org.cytoscape.rest.internal.net.server.LocalHttpServer.Response;

	import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

	/**
	 * This class is responsible for handling POST requests received by the local HTTP server.
	 */
	public class CytoBridgePostResponder implements LocalHttpServer.PostResponder{

		private static final String PACKAGE = "org.cytoscape.rest.internal.json.";
		
		private NetworkManager myManager;
		
		private final static Logger logger = LoggerFactory.getLogger(CytoBridgePostResponder.class);
		
		public CytoBridgePostResponder(NetworkManager myManager) {
			this.myManager = myManager;
		}

		public boolean canRespondTo(String url) throws Exception {
			return (url.indexOf("cytobridge") >= 0);
		}

		public Response respond(String url, String body) throws Exception {
			logger.debug("POST url     : " + url);
			logger.debug("POST body    : " + body);
			String[] chunk = url.substring( url.indexOf("cytobridge") + 10 ).split("/"); 

			String namespace = chunk[1];
			String args = URLDecoder.decode(body.substring(5)); // trims off "data=" from body string

			System.out.println("args= "+args);
			System.out.println("namespace= "+namespace);
			logger.debug("POST command : " + namespace);

			Gson gson = new Gson();
			
			JSONCommand jcom = (JSONCommand)gson.fromJson(args,Class.forName(PACKAGE+namespace));
			jcom.run(myManager);
			
			Map<String, String> responseData = new HashMap<String, String>();

			String responseBody = "Yay!";
			LocalHttpServer.Response response = new LocalHttpServer.Response(responseBody, "application/json");
			return response;
		}
}
