package org.cytoscape.rest.internal.task;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.cytoscape.rest.internal.service.MiscDataService;
import org.cytoscape.rest.internal.service.NetworkDataService;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task to start Grizzly server.
 * 
 */
public final class GrizzlyServerManager {

	private final static Logger logger = LoggerFactory.getLogger(GrizzlyServerManager.class);

	private String baseURL = "http://localhost/";
	private int port = 1234;

	private final Binder binder;

	private HttpServer server = null;

	public GrizzlyServerManager(final Binder binder) {
		this.binder = binder;
	}

	public void startServer() throws Exception {
		if (server == null) {
			final URI baseURI = UriBuilder.fromUri(baseURL).port(port).build();
			final ResourceConfig rc = new ResourceConfig(NetworkDataService.class, MiscDataService.class);
			rc.registerInstances(binder).packages("org.glassfish.jersey.examples.jackson")
					.register(JacksonFeature.class);

			this.server = GrizzlyHttpServerFactory.createHttpServer(baseURI, rc);
			System.out.println("**************REST Server started: " + server.toString());
		}
	}

	public void stopServer() {
		if(this.server != null) {
			this.server.shutdown();
		}
	}

}
