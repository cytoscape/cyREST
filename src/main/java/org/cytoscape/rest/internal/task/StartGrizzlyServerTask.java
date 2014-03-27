package org.cytoscape.rest.internal.task;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.rest.TaskFactoryManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private final TaskFactoryManager tfManager;
	private final CyApplicationManager applicationManager;

	public StartGrizzlyServerTask(CyNetworkManager networkManager, final CyNetworkFactory networkFactory,
			final TaskFactoryManager tfManager, final CyApplicationManager applicationManager) {
		this.networkManager = networkManager;
		this.networkFactory = networkFactory;
		this.tfManager = tfManager;
		this.applicationManager = applicationManager;
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
		ResourceConfig rc = new ResourceConfig(DataService.class);

		rc.getSingletons().add(
				new SingletonTypeInjectableProvider<Context, CyNetworkManager>(CyNetworkManager.class, networkManager) {
				});
		rc.getSingletons().add(
				new SingletonTypeInjectableProvider<Context, CyNetworkFactory>(CyNetworkFactory.class, networkFactory) {
				});
		rc.getSingletons().add(
				new SingletonTypeInjectableProvider<Context, TaskFactoryManager>(TaskFactoryManager.class, tfManager) {
				});
		rc.getSingletons().add(
				new SingletonTypeInjectableProvider<Context, CyApplicationManager>(CyApplicationManager.class, applicationManager) {
				});

		return GrizzlyServerFactory.createHttpServer(baseURI, rc);
	}
}
