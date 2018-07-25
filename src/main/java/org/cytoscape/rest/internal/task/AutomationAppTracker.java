package org.cytoscape.rest.internal.task;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.core.Feature;
import javax.ws.rs.ext.Provider;

import org.cytoscape.rest.internal.CyRESTConstants;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.task.NetworkViewCollectionTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.TableTaskFactory;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutomationAppTracker extends ServiceTracker implements BundleListener
{
	private final static Logger logger = LoggerFactory.getLogger(AutomationAppTracker.class);
	
	Map<Bundle, Set<Object>> bundles;
	Map<String, Map<String, Bundle>> commandBundles;

	public AutomationAppTracker( BundleContext context, Filter filter) {
		super( context, filter, null );
		this.bundles = new HashMap<Bundle, Set<Object>>();
		this.commandBundles = new HashMap<String, Map<String, Bundle>>();
	}

	public Set<Bundle> getAppBundles() {
		
		return Collections.unmodifiableSet(bundles.keySet());
	}
	
	private void delegateAddService( ServiceReference reference, Object service ) {
		if( isAutomationService(reference, service )) {
			addAutomationService(reference.getBundle(), service);
		}
		if (isCommand(reference, service)) {
			addAutomationService(reference.getBundle(), service);
			addCommand(reference);
		}
	}
	
	public String getMarkdownReport() {
		int runningAutomationBundles = 0;
		int totalAutomationBundles = 0;
		try {
			Set<Bundle> automationBundles = getAppBundles();
			totalAutomationBundles = automationBundles.size();
			for (Bundle bundle : automationBundles) {
				if (bundle.getState() == Bundle.ACTIVE) {
					runningAutomationBundles++;
				}
			}
		}
		catch (Throwable e) {
			logger.error("Error reporting running automation bundles.", e);
			return "_Error reporting running automation bundles. This document may not be complete._";
		}
		String summary;
		if (runningAutomationBundles == totalAutomationBundles) {
			summary = "";
		} else {
			summary = " Some API may not be listed. Try reloading this page. If this condition persists, check the Cytoscape error log for possible causes.";
		}
		return "_" + runningAutomationBundles + "/" + totalAutomationBundles  + " Automation Apps started." + summary + "_";
	}
	
	@Override
	public Object addingService( ServiceReference reference ) {
		Object service = super.addingService(reference);
		delegateAddService( reference, service );
		return service;
	}

	private void addAutomationService(Bundle bundle, Object service) {
		Set<Object> services = bundles.get(bundle);
		if (services == null) {
			services = new HashSet<Object>();
		}
		services.add(service);
		bundles.put(bundle, services);
	}

	private void addCommand(ServiceReference serviceReference) {
		
		String command = serviceReference.getProperty(ServiceProperties.COMMAND).toString();
		String commandNamespace = serviceReference.getProperty(ServiceProperties.COMMAND_NAMESPACE).toString();
		Map<String, Bundle> commandMap = commandBundles.get(commandNamespace);
		if (commandMap == null) {
			commandMap = new HashMap<String, Bundle>();
		}
		commandMap.put(command, serviceReference.getBundle());
		commandBundles.put(commandNamespace, commandMap);
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
		return service != null && (hasRegisterableAnnotation( service ) || service instanceof Feature);
	}

	private boolean hasRegisterableAnnotation( Object service ) {
		boolean result = isRegisterableAnnotationPresent( service.getClass() );
		if( !result ) {
			Class<?>[] interfaces = service.getClass().getInterfaces();
			for( Class<?> type : interfaces ) {
				result = result || isRegisterableAnnotationPresent( type );
			}
		}
		return result;
	}

	public boolean isCommandTaskFactory(Object service) {
		if (service instanceof TaskFactory) {
			return true;
		}
		if (service instanceof NetworkTaskFactory) {
			return true;
		}
		if (service instanceof NetworkViewTaskFactory) {
			return true;
		}
		if (service instanceof NetworkViewCollectionTaskFactory) {
			return true;
		}
		if (service instanceof TableTaskFactory) {
			return true;
		}
		return false;
	}
	
	private boolean isCommand(ServiceReference reference, Object service) {
		if (service != null && isCommandTaskFactory(service)) {
			if (reference.getProperty(ServiceProperties.COMMAND) != null && reference.getProperty(ServiceProperties.COMMAND_NAMESPACE) != null) {
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
		if (event.getType() == BundleEvent.STOPPING  || event.getType() == BundleEvent.STOPPED || event.getType() == BundleEvent.UNINSTALLED) {
			bundles.remove(event.getBundle());
		}
	}

	public String getCommandSourceApp(String commandNamespace, String command) {
		Map<String, Bundle> commandMap = commandBundles.get(commandNamespace);
		Bundle bundle = commandMap.get(command);
		Object bundleNameObject = bundle.getHeaders().get(CyRESTConstants.BUNDLE_NAME);
		String bundleName = null;
		if (bundleNameObject != null) {
			bundleName = bundleNameObject.toString();
		}
		return bundleName; 		
	}

}
