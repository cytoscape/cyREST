package org.cytoscape.rest.internal.task;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;

import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.glassfish.grizzly.http.server.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

/**
 * Task to start Grizzly server.
 * 
 */
public final class StartGrizzlyServerTask extends AbstractTask {

	private final static Logger logger = LoggerFactory.getLogger(StartGrizzlyServerTask.class);

	// Server settings TODO make these Cytoscape property
	private String baseURL = "http://localhost/";
	private int port = 9988;

	private String resourceLocation = "org.cytoscape.rest.internal.jaxrs";

	private final CyNetworkManager networkManager;
	private final CyNetworkFactory networkFactory;

	public StartGrizzlyServerTask(CyNetworkManager networkManager, final CyNetworkFactory networkFactory) {
		this.networkManager = networkManager;
		this.networkFactory = networkFactory;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setStatusMessage("Starting REST server (Port " + port + ")...");
		final URI baseURI = UriBuilder.fromUri(baseURL).port(port).build();
		final HttpServer httpServer = startServer(baseURI);
		logger.info(String.format("REST service started with WADL available at " + "%sapplication.wadl\nTry out %s%s",
				baseURI, baseURI, resourceLocation));
	}

	private final HttpServer startServer(final URI baseURI) throws IOException {
		ResourceConfig rc = new PackagesResourceConfig("org.cytoscape.rest.internal.jaxrs");

		rc.getSingletons().add(
				new SingletonTypeInjectableProvider<Context, CyNetworkManager>(CyNetworkManager.class, networkManager) {
				});
		rc.getSingletons().add(
				new SingletonTypeInjectableProvider<Context, CyNetworkFactory>(CyNetworkFactory.class, networkFactory) {
				});

		return GrizzlyServerFactory.createHttpServer(baseURI, rc);
	}
}
