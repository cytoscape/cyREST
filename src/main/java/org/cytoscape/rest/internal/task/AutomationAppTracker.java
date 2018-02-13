package org.cytoscape.rest.internal.task;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.core.Feature;
import javax.ws.rs.ext.Provider;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.ServiceProperties;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class AutomationAppTracker extends ServiceTracker implements BundleListener
{
	Map<Bundle, Set<Object>> bundles;


	public AutomationAppTracker( BundleContext context, Filter filter) {
		super( context, filter, null );
		this.bundles = new HashMap<Bundle, Set<Object>>();
	}

	public Set<Bundle> getAppBundles() {
		
		return Collections.unmodifiableSet(bundles.keySet());
	}
	
	private void delegateAddService( ServiceReference reference, Object service ) {
		if( isAutomationService(reference, service ) ) {
			addAutomationService(reference.getBundle(), service);
		}
	}
	
	@Override
	public Object addingService( ServiceReference reference ) {
		Object service = super.addingService(reference);
		delegateAddService( reference, service );
		return service;
	}

	public void addAutomationService(Bundle bundle, Object service) {
		Set<Object> services = bundles.get(bundle);
		if (services == null) {
			services = new HashSet<Object>();
		}
		services.add(service);
		bundles.put(bundle, services);
		//System.out.println("Automation bundle found: " + bundle);	
	}

	@Override
	public void removedService( ServiceReference reference, Object service ) {
		Set<Object> bundleServices = bundles.get(reference.getBundle());
		if (bundleServices != null) {
			bundleServices.remove(service);
			if (bundleServices.size() > 0) {
				bundles.put(reference.getBundle(), bundleServices);
			} else {
				bundles.remove(reference.getBundle());
			}
		}
		super.removedService(reference, service);
	}

	@Override
	public void modifiedService( ServiceReference reference, Object service ) {
		
		super.modifiedService(reference, service);
	}

	private boolean isAutomationService( ServiceReference reference, Object service ) {
		return service != null && ( hasRegisterableAnnotation( service ) || service instanceof Feature || isCommand(reference, service) );
	}

	private boolean hasRegisterableAnnotation( Object service ) {
		boolean result = isRegisterableAnnotationPresent( service.getClass() );
		if( !result ) {
			Class<?>[] interfaces = service.getClass().getInterfaces();
			for( Class<?> type : interfaces ) {
				result = result || isRegisterableAnnotationPresent( type );
			}
		}
		/*
		if (result) {
			System.out.println("\t"+ service.getClass().getSimpleName() + " is a JAX-RS service");
		}
		*/
		return result;
	}

	private boolean isCommand(ServiceReference reference, Object service) {
		if (service instanceof TaskFactory) {
			if (reference.getProperty(ServiceProperties.COMMAND) != null && reference.getProperty(ServiceProperties.COMMAND_NAMESPACE) != null) {
				//System.out.println("\t" + service.getClass().getSimpleName() + " is a Command");
				return true;
			}
		}
		return false;
	}
	
	private boolean isRegisterableAnnotationPresent( Class<?> type ) 
	{
		return type.isAnnotationPresent( Path.class ) || type.isAnnotationPresent( Provider.class );
	}

	@Override
	public void bundleChanged(BundleEvent event) {
		if (event.getType() == BundleEvent.STOPPED || event.getType() == BundleEvent.UNINSTALLED) {
			bundles.remove(event.getBundle());
		}
		
	}

}
