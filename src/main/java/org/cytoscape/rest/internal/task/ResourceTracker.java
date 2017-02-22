package org.cytoscape.rest.internal.task;

import java.lang.annotation.Annotation;

import javax.ws.rs.Path;
import javax.ws.rs.core.Feature;
import javax.ws.rs.ext.Provider;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import io.swagger.annotations.Api;

@SuppressWarnings("rawtypes")
public class ResourceTracker extends ServiceTracker
{
	//CyRESTCommandSwagger swaggerResource;
	
	  private final BundleContext context;

	  public ResourceTracker( BundleContext context, Filter filter) {
	    super( context, filter, null );
	    System.out.println("Path class: " + Path.class);
	    this.context = context;
	    //this.swaggerResource = swaggerResource;
	  }

	  @Override
	  public Object addingService( ServiceReference reference ) {
	    Object service = context.getService( reference );
	    return delegateAddService( reference, service );
	  }

	  private Object delegateAddService( ServiceReference reference, Object service ) {
	    Object result;
	    if( isResource( service ) ) {
	    	System.out.println("Is resource:" + reference + " " + service);
	    	  //FIXME implement below
	        //swaggerResource.addResource( service.getClass() );
	    	result = service;
	    } else {
	      context.ungetService( reference );
	      result = null;
	    }
	    return result;
	  }

	  @Override
	  public void removedService( ServiceReference reference, Object service ) {
		  //FIXME implement below
		 // swaggerResource.removeResource( service.getClass() );
	    context.ungetService( reference );
	  }

	  @Override
	  public void modifiedService( ServiceReference reference, Object service ) {
		  //FIXME implement below
		  //swaggerResource.removeResource(  service.getClass() );
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
		  for (Annotation annotation : type.getAnnotations())
		  {
			  if (annotation.annotationType().getName().contains("javax.ws.rs.Path"))
			  {
				  System.out.println("Path annotation found in " + type.getName() + " " + annotation.annotationType().equals(Path.class));
			  }
		  }
		  return (type.isAnnotationPresent( Path.class ) || type.isAnnotationPresent( Provider.class ));
	  }
	
	  private boolean isSwaggerAnnotationPresent(Class<?> type)
	  {
		  return (type.isAnnotationPresent(Api.class));
	  }
}
