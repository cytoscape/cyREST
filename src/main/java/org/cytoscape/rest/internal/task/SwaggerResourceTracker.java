package org.cytoscape.rest.internal.task;

import javax.ws.rs.Path;
import javax.ws.rs.core.Feature;
import javax.ws.rs.ext.Provider;

import org.cytoscape.rest.internal.resource.CyRESTSwagger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import io.swagger.annotations.Api;

public class SwaggerResourceTracker extends ServiceTracker
{
	CyRESTSwagger swaggerResource;
	
	  private final BundleContext context;

	  public SwaggerResourceTracker( BundleContext context, Filter filter, CyRESTSwagger swaggerResource ) {
	    super( context, filter, null );
	    this.context = context;
	    this.swaggerResource = swaggerResource;
	  }

	  @Override
	  public Object addingService( ServiceReference reference ) {
	    Object service = context.getService( reference );
	    return delegateAddService( reference, service );
	  }

	  private Object delegateAddService( ServiceReference reference, Object service ) {
	    Object result;
	    if( isResource( service ) ) {
	        swaggerResource.addResource( service.getClass() );
	    	result = service;
	    } else {
	      context.ungetService( reference );
	      result = null;
	    }
	    return result;
	  }

	  @Override
	  public void removedService( ServiceReference reference, Object service ) {
		 
		  swaggerResource.removeResource( service.getClass() );
	    context.ungetService( reference );
	  }

	  @Override
	  public void modifiedService( ServiceReference reference, Object service ) {
		 
		  swaggerResource.removeResource(  service.getClass() );
	    delegateAddService( reference, service );
	  }

	  private boolean isResource( Object service ) {
	    return service != null && ( hasRegisterableAnnotation( service ) || service instanceof Feature );
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

	  private boolean isRegisterableAnnotationPresent( Class<?> type ) 
	  {
		  return (type.isAnnotationPresent( Path.class ) || type.isAnnotationPresent( Provider.class )) && type.isAnnotationPresent(Api.class);
	  }
	
}
