package org.cytoscape.rest.internal.task;

import java.net.URI;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.core.UriBuilder;

import org.cytoscape.command.AvailableCommands;
import org.cytoscape.property.CyProperty;
import org.cytoscape.rest.internal.commands.resources.CommandResource;
import org.cytoscape.rest.internal.resource.AlgorithmicResource;
import org.cytoscape.rest.internal.resource.CORSFilter;
import org.cytoscape.rest.internal.resource.CollectionResource;
import org.cytoscape.rest.internal.resource.GlobalTableResource;
import org.cytoscape.rest.internal.resource.GroupResource;
import org.cytoscape.rest.internal.resource.MiscResource;
import org.cytoscape.rest.internal.resource.NetworkFullResource;
import org.cytoscape.rest.internal.resource.NetworkNameResource;
import org.cytoscape.rest.internal.resource.NetworkResource;
import org.cytoscape.rest.internal.resource.NetworkViewResource;
import org.cytoscape.rest.internal.resource.StyleResource;
import org.cytoscape.rest.internal.resource.TableResource;
import org.cytoscape.rest.internal.resource.UIResource;
import org.cytoscape.rest.internal.resource.RootResource;
import org.cytoscape.rest.internal.resource.SessionResource;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.jaxrs.config.BeanConfig;

/**
 * Task to start Grizzly server.
 * 
 */
public final class GrizzlyServerManager {

	private final static Logger logger = LoggerFactory.getLogger(GrizzlyServerManager.class);

	public static final String PORT_NUMBER_PROP = "rest.port";
	public static final Integer DEF_PORT_NUMBER = 1234;

	public static final String HOST = "0.0.0.0";
	
	private String baseURL = "http://" + HOST;
	
	private Integer portNumber = DEF_PORT_NUMBER;

	private final Binder binder;

	private final AvailableCommands available;
	
	private HttpServer server = null;
	
	//private HttpServer swaggerServer = null;

	private long loadTime;

	public GrizzlyServerManager(final Binder binder, CyProperty<Properties> props, AvailableCommands available) {
		
		this.binder = binder;

		this.available = available;
		
		// Get property from property
		Object portNumberProp = props.getProperties().get(PORT_NUMBER_PROP);
		if(portNumberProp != null) {
			try {
				portNumber = Integer.parseInt(portNumberProp.toString());
			} catch(Exception ex) {
				portNumber = DEF_PORT_NUMBER;
			}
		}
	}

	//TODO Add this config to testing, otherwise our tests and reality could be out of sync.
	public void startServer() throws Exception 
	{
		loadTime = System.currentTimeMillis();
		
		//FIXME See CyRESTCommandSwaggerConfig.available for details.
		CyRESTCommandSwaggerConfig.available = available;
		
		if (server == null) 
		{
			final URI baseURI = UriBuilder.fromUri(baseURL).port(portNumber).build();
			
			final Set<Class<?>> serviceClasses = new HashSet<Class<?>>();
			serviceClasses.add(RootResource.class);
			serviceClasses.add(NetworkResource.class);
			serviceClasses.add(NetworkFullResource.class);
			serviceClasses.add(NetworkViewResource.class);
			serviceClasses.add(TableResource.class); 
			serviceClasses.add(MiscResource.class);
			serviceClasses.add(AlgorithmicResource.class);
			serviceClasses.add(StyleResource.class);
			serviceClasses.add(GroupResource.class);
			serviceClasses.add(GlobalTableResource.class);
			serviceClasses.add(SessionResource.class);
			serviceClasses.add(NetworkNameResource.class);
			serviceClasses.add(UIResource.class);
			serviceClasses.add(CollectionResource.class);

			// For Commands
			serviceClasses.add(CommandResource.class);
			serviceClasses.add(CyRESTCommandSwagger.class);

			//For CORS
			serviceClasses.add(CORSFilter.class);
			
			BeanConfig cyRESTBeanConfig = new BeanConfig()
			{
				/*
				 * This is overridden from the default implementation because it relied
				 * on reflections to automatically discover annotated classes. OSGi in 
				 * turn doesn't behave as reflections expects, and necessitates us adding
				 * classes here.
				*/
				public Set<Class<?>> classes() 
				{
					Set<Class<?>> classes = new HashSet<Class<?>>();
					classes.addAll(serviceClasses);
					classes.add(CyRESTSwaggerConfig.class);
					return classes;
				}
			};
			
			//We set the host here because SwaggerApiConfig is fairly static.
			//cyRESTBeanConfig.set
			//cyRESTBeanConfig.setScannerId("v1.cyREST");
			//cyRESTBeanConfig.setConfigId("v1.cyREST");
			//cyRESTBeanConfig.setBasePath("/");
			cyRESTBeanConfig.setHost(baseURI.getHost() + ":" + baseURI.getPort()); 
			cyRESTBeanConfig.setScan(true);
			cyRESTBeanConfig.setPrettyPrint(true);
		
			final ResourceConfig rc = new ResourceConfig();
			for (Class<?> clazz : serviceClasses)
			{
				rc.register(clazz);
			}

			rc.register(io.swagger.jaxrs.listing.ApiListingResource.class);
			rc.register(io.swagger.jaxrs.listing.SwaggerSerializers.class);
			
			/*
			rc.registerInstances(binder).packages("org.glassfish.jersey.examples.jackson")
			.register(JacksonFeature.class);
			 */
			rc.registerInstances(binder).register(JacksonFeature.class);

			this.server = GrizzlyHttpServerFactory.createHttpServer(baseURI, rc, false);
			this.server.start();

			//this.swaggerServer = GrizzlyHttpServerFactory.createHttpServer(swaggerURI, swaggerRC, false);
			//this.swaggerServer.start();
			
			loadTime = System.currentTimeMillis() - loadTime;

			logger.info("========== Cytoscape RESTful API service started in (" + loadTime + ") milliseconds.  Listening at port: " + portNumber + " ==============");
		}
	}

	public void stopServer() {
		if(this.server != null) 
		{
			this.server.shutdown();
		}
	}
}