package org.cytoscape.rest.internal.task;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.core.UriBuilder;

import org.cytoscape.command.AvailableCommands;
import org.cytoscape.property.CyProperty;
import org.cytoscape.rest.internal.commands.resources.CommandResource;
import org.cytoscape.rest.internal.resource.AlgorithmicResource;
import org.cytoscape.rest.internal.resource.CORSFilter;
import org.cytoscape.rest.internal.resource.CollectionResource;
import org.cytoscape.rest.internal.resource.CyExceptionMapper;
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
//import org.glassfish.grizzly.http.server.HttpServer;
//import org.glassfish.hk2.utilities.Binder;
//import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainer;
//import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
//import org.glassfish.jersey.jackson.JacksonFeature;
//import org.glassfish.jersey.server.ContainerFactory;
//import org.glassfish.jersey.server.ResourceConfig;
//import org.glassfish.jersey.server.model.Resource;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import io.swagger.jaxrs.config.BeanConfig;

/**
 * Task to start Grizzly server.
 * 
 */
public final class ResourceManager {

	private final static Logger logger = LoggerFactory.getLogger(ResourceManager.class);

	public static final String PORT_NUMBER_PROP = "rest.port";
	public static final Integer DEF_PORT_NUMBER = 1234;

	public static final String HOST = "0.0.0.0";
	
	private String baseURL = "http://" + HOST;
	
	private Integer portNumber = DEF_PORT_NUMBER;

	private final Module binder;

	private final AvailableCommands available;

	private long loadTime;

	private BundleContext bundleContext;
	
	private List<ServiceRegistration> serviceRegistrations;
	
	public static final Class<?>[] resourceClasses = {
			RootResource.class,
			NetworkResource.class,
			NetworkFullResource.class,
			NetworkViewResource.class,
			TableResource.class,
			MiscResource.class,
			AlgorithmicResource.class,
			StyleResource.class,
			GroupResource.class,
			GlobalTableResource.class,
			SessionResource.class,
			NetworkNameResource.class,
			UIResource.class,
			CollectionResource.class,

			// For Commands
			CommandResource.class,
			CyRESTCommandSwagger.class,

			//For CORS
			CORSFilter.class
	};
	
	public ResourceManager(final BundleContext bundleContext, final Module binder, CyProperty<Properties> props, AvailableCommands available) {
		
		this.bundleContext = bundleContext;
		
		this.binder = binder;

		this.available = available;
		
		this.serviceRegistrations = new ArrayList<ServiceRegistration>();
		
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

	public Integer getPortNumber() {
		return portNumber;
	}

	//TODO Add this config to integration testing, otherwise our tests and reality could be out of sync.
	public void startServer() throws Exception 
	{
		loadTime = System.currentTimeMillis();
		
		//FIXME See CyRESTCommandSwaggerConfig.available for details.
		CyRESTCommandSwaggerConfig.available = available;
		
		//if (server == null) 
		{
			//final URI baseURI = UriBuilder.fromUri(baseURL).port(portNumber).build();
			
			System.out.println("Creating Swagger BeanConfig.");
			BeanConfig cyRESTBeanConfig = new BeanConfig(){
				/*
				 * This is overridden from the default implementation because it relied
				 * on reflections to automatically discover annotated classes. OSGi in 
				 * turn doesn't behave as reflections expects, and necessitates us adding
				 * classes here.
				*/
				public Set<Class<?>> classes() 
				{
					Set<Class<?>> classes = new HashSet<Class<?>>();
					classes.addAll(Arrays.asList(resourceClasses));
					classes.add(CyRESTSwaggerConfig.class);
					return classes;
				}
			};
			
			//We set the host here because SwaggerApiConfig is fairly static.
			//cyRESTBeanConfig.set
			//cyRESTBeanConfig.setScannerId("v1.cyREST");
			//cyRESTBeanConfig.setConfigId("v1.cyREST");
			//cyRESTBeanConfig.setBasePath("/");
			//cyRESTBeanConfig.setHost(baseURI.getHost() + ":" + baseURI.getPort()); 
			cyRESTBeanConfig.setScan(true);
			cyRESTBeanConfig.setPrettyPrint(true);
		
		
			Injector injector = Guice.createInjector(binder);
			
			//final ResourceConfig rc = new ResourceConfig();
			for (Class<?> clazz : resourceClasses)
			{
				Object instance = injector.getInstance(clazz);
				serviceRegistrations.add(bundleContext.registerService(clazz.getName(), instance, new Properties()));
			}

			serviceRegistrations.add(bundleContext.registerService(CyExceptionMapper.class.getName(), new CyExceptionMapper(), new Properties()));
			
			serviceRegistrations.add(bundleContext.registerService(io.swagger.jaxrs.listing.ApiListingResource.class.getName(), injector.getInstance(io.swagger.jaxrs.listing.ApiListingResource.class), new Properties()));
			serviceRegistrations.add(bundleContext.registerService(io.swagger.jaxrs.listing.SwaggerSerializers.class.getName(), injector.getInstance(io.swagger.jaxrs.listing.SwaggerSerializers.class),new Properties()));
			
			loadTime = System.currentTimeMillis() - loadTime;

			logger.info("========== Cytoscape RESTful API service started in (" + loadTime + ") milliseconds.  Listening at port: " + portNumber + " ==============");
		}
	}

	public void stopServer() {
		for (ServiceRegistration serviceRegistration : serviceRegistrations)
		{
			serviceRegistration.unregister();
		}
	}
}