package org.cytoscape.rest.internal.task;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.cytoscape.rest.internal.jaxrs.DataService;
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
public final class StartGrizzlyServerTask extends AbstractTask {

	private final static Logger logger = LoggerFactory
			.getLogger(StartGrizzlyServerTask.class);

	private String baseURL = "http://localhost/";
	private int port = 1234;

	private final Binder binder;

	public StartGrizzlyServerTask(final Binder binder) {
		this.binder = binder;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setStatusMessage("Starting REST server (Port " + port
				+ ")...");
		final URI baseURI = UriBuilder.fromUri(baseURL).port(port).build();
		taskMonitor.setStatusMessage("URL = " + baseURI.toURL());
		final HttpServer httpServer = startServer(baseURI);
		System.out
				.println("#######################################Server start");
		taskMonitor.setStatusMessage("DONE!");
	}

	private final HttpServer startServer(final URI baseURI) throws IOException {
		final ResourceConfig rc = new ResourceConfig(DataService.class);
		rc.registerInstances(binder)
				.packages("org.glassfish.jersey.examples.jackson")
				.register(JacksonFeature.class);
		return GrizzlyHttpServerFactory.createHttpServer(baseURI, rc);
	}
}
