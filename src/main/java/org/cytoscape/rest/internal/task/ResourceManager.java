package org.cytoscape.rest.internal.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.cytoscape.rest.internal.commands.resources.CommandResource;
import org.cytoscape.rest.internal.resource.AlgorithmicResource;
import org.cytoscape.rest.internal.resource.CORSFilter;
import org.cytoscape.rest.internal.resource.CollectionResource;
import org.cytoscape.rest.internal.resource.CyExceptionMapper;
import org.cytoscape.rest.internal.resource.CyRESTCommandSwagger;
import org.cytoscape.rest.internal.resource.CyRESTSwagger;
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

	public static final String HOST = "0.0.0.0";

	private final Module binder;

	private CyRESTSwagger swagger;

	private SwaggerResourceTracker resourceTracker;

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
			CORSFilter.class,
	};

	public ResourceManager(final BundleContext bundleContext, final Module binder) throws InvalidSyntaxException {

		this.bundleContext = bundleContext;

		this.binder = binder;

		this.serviceRegistrations = new ArrayList<ServiceRegistration>();
	}

	//TODO Add this config to integration testing, otherwise our tests and reality could be out of sync.
	public void registerResourceServices() throws Exception 
	{
		loadTime = System.currentTimeMillis();
		
		Injector injector = Guice.createInjector(binder);

		for (Class<?> clazz : resourceClasses)
		{
			Object instance = injector.getInstance(clazz);
			serviceRegistrations.add(bundleContext.registerService(clazz.getName(), instance, new Properties()));
		}

		serviceRegistrations.add(bundleContext.registerService(CyExceptionMapper.class.getName(), new CyExceptionMapper(), new Properties()));

		final String ANY_SERVICE_FILTER = "(&(objectClass=*)(!(com.eclipsesource.jaxrs.publish=false)))";

		swagger = injector.getInstance(CyRESTSwagger.class);

		resourceTracker = new SwaggerResourceTracker(bundleContext,bundleContext.createFilter(ANY_SERVICE_FILTER), swagger);
		resourceTracker.open();

		serviceRegistrations.add(bundleContext.registerService(CyRESTSwagger.class.getName(), swagger, new Properties()));

		loadTime = System.currentTimeMillis() - loadTime;

		logger.info("========== Cytoscape RESTful API registered core resources in (" + loadTime + ") milliseconds.");

	}

	public void unregisterResourceServices() {
		for (ServiceRegistration serviceRegistration : serviceRegistrations)
		{
			serviceRegistration.unregister();
		}
		resourceTracker.close();
		resourceTracker = null;
	}
}