package org.cytoscape.rest.internal.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.cytoscape.rest.internal.CyRESTConstants;
import org.cytoscape.rest.internal.resource.CyRESTSwagger;
import org.cytoscape.rest.internal.resource.apps.AppConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * This registers/unregisters the CyREST core JAX-RS annotated resources to the OSGi context.
 */
public final class ResourceManager {

	private final static Logger logger = LoggerFactory.getLogger(ResourceManager.class);
	
	public static final String PORT_NUMBER_PROP = "rest.port";
	public static final Integer DEF_PORT_NUMBER = 1234;

	// Note; this used to be used 
	public static final String HOST = "localhost";

	private CyRESTSwagger swagger;

	private SwaggerResourceTracker swaggerResourceTracker;

	private long loadTime;

	private BundleContext bundleContext;

	private final List<ServiceRegistration> serviceRegistrations;

	public final Class<?>[] coreResourceClasses;

	private final Module coreServicesModule;

	private final Map<Class<?>, Module> shimResources;

	public ResourceManager(final BundleContext bundleContext, final Class<?>[] coreResourceClasses, final Module coreServicesModule, final Map<Class<?>, Module> shimResources) throws InvalidSyntaxException {

		this.bundleContext = bundleContext;

		this.coreResourceClasses = coreResourceClasses;

		this.coreServicesModule = coreServicesModule;

		this.shimResources = shimResources;

		this.serviceRegistrations = new ArrayList<ServiceRegistration>();

	}

	//TODO Add this config to integration testing, otherwise our tests and reality could be out of sync.
	public void registerResourceServices() throws Exception 
	{
		loadTime = System.currentTimeMillis();

		Injector injector = Guice.createInjector(coreServicesModule);

		for (Class<?> clazz : coreResourceClasses){
			Object instance = injector.getInstance(clazz);
			serviceRegistrations.add(bundleContext.registerService(clazz.getName(), instance, new Properties()));
		}

		for (Map.Entry<Class<?>, Module> entry : shimResources.entrySet()){
			if (AppConstants.hasValidAppRoot(entry.getKey()))
			{
				Object instance = injector.getInstance(entry.getKey());
				if (entry.getValue() != null)
				{
					Injector appInjector = Guice.createInjector(entry.getValue());
					appInjector.injectMembers(instance);
				}
				serviceRegistrations.add(bundleContext.registerService(entry.getKey().getName(), instance, new Properties()));
			}
			else
			{
				logger.error("App resource " + entry.getKey().getName() + " has an invalid @Path annotation, and could not be loaded.");
			}
		}

		//serviceRegistrations.add(bundleContext.registerService(ServletExceptionMapper.class.getName(), new ServletExceptionMapper(), new Properties()));
		
		

		swagger = injector.getInstance(CyRESTSwagger.class);

		swaggerResourceTracker = new SwaggerResourceTracker(bundleContext,bundleContext.createFilter(CyRESTConstants.ANY_SERVICE_FILTER), swagger);
		swaggerResourceTracker.open();

		serviceRegistrations.add(bundleContext.registerService(CyRESTSwagger.class.getName(), swagger, new Properties()));
		
		loadTime = System.currentTimeMillis() - loadTime;

		logger.info("========== Cytoscape RESTful API registered core resources in (" + loadTime + ") milliseconds.");
	}

	public void unregisterResourceServices() {
		for (ServiceRegistration serviceRegistration : serviceRegistrations)
		{
			serviceRegistration.unregister();
		}
		swaggerResourceTracker.close();
		swaggerResourceTracker = null;
	}
}