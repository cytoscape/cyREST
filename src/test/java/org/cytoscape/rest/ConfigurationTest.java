package org.cytoscape.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.cytoscape.rest.internal.CyRESTConstants;
import org.cytoscape.rest.internal.commands.resources.CommandResource;
import org.cytoscape.rest.internal.resource.AlgorithmicResource;
import org.cytoscape.rest.internal.resource.AppsResource;
import org.cytoscape.rest.internal.resource.CIResponseFilter;
import org.cytoscape.rest.internal.resource.CORSFilter;
import org.cytoscape.rest.internal.resource.CollectionResource;
import org.cytoscape.rest.internal.resource.CyExceptionMapper;
import org.cytoscape.rest.internal.resource.CyRESTCommandSwagger;
import org.cytoscape.rest.internal.resource.EventFlushResponseFilter;
import org.cytoscape.rest.internal.resource.GlobalTableResource;
import org.cytoscape.rest.internal.resource.GroupResource;
import org.cytoscape.rest.internal.resource.MiscResource;
import org.cytoscape.rest.internal.resource.NetworkFullResource;
import org.cytoscape.rest.internal.resource.NetworkNameResource;
import org.cytoscape.rest.internal.resource.NetworkResource;
import org.cytoscape.rest.internal.resource.NetworkViewResource;
import org.cytoscape.rest.internal.resource.PropertiesResource;
import org.cytoscape.rest.internal.resource.RootResource;
import org.cytoscape.rest.internal.resource.SessionResource;
import org.cytoscape.rest.internal.resource.StyleResource;
import org.cytoscape.rest.internal.resource.SwaggerUIResource;
import org.cytoscape.rest.internal.resource.TableResource;
import org.cytoscape.rest.internal.resource.UIResource;
import org.junit.Test;

public class ConfigurationTest 
{
	/**
	 * This is a little nit-picky, but is here to ensure that adding or removing another resource to the server requires 
	 * acknowledgement in testing.
	 */
	@Test
	public void testConstants() 
	{
		assertTrue(contains(CyRESTConstants.coreResourceClasses, AppsResource.class));
		assertTrue(contains(CyRESTConstants.coreResourceClasses, RootResource.class));
		assertTrue(contains(CyRESTConstants.coreResourceClasses, NetworkResource.class));
		assertTrue(contains(CyRESTConstants.coreResourceClasses, NetworkFullResource.class));
		assertTrue(contains(CyRESTConstants.coreResourceClasses, NetworkViewResource.class));
		assertTrue(contains(CyRESTConstants.coreResourceClasses, PropertiesResource.class));
		assertTrue(contains(CyRESTConstants.coreResourceClasses,TableResource.class));
		assertTrue(contains(CyRESTConstants.coreResourceClasses,MiscResource.class));
		assertTrue(contains(CyRESTConstants.coreResourceClasses,AlgorithmicResource.class));
		assertTrue(contains(CyRESTConstants.coreResourceClasses,StyleResource.class));
		assertTrue(contains(CyRESTConstants.coreResourceClasses,GroupResource.class));
		assertTrue(contains(CyRESTConstants.coreResourceClasses,GlobalTableResource.class));
		assertTrue(contains(CyRESTConstants.coreResourceClasses,SessionResource.class));
		assertTrue(contains(CyRESTConstants.coreResourceClasses,NetworkNameResource.class));
		assertTrue(contains(CyRESTConstants.coreResourceClasses,UIResource.class));
		assertTrue(contains(CyRESTConstants.coreResourceClasses,CollectionResource.class));

		// For Commands
		assertTrue(contains(CyRESTConstants.coreResourceClasses,CommandResource.class));
		assertTrue(contains(CyRESTConstants.coreResourceClasses,CyRESTCommandSwagger.class));
		assertTrue(contains(CyRESTConstants.coreResourceClasses,SwaggerUIResource.class));

		//For CORS
		assertTrue(contains(CyRESTConstants.coreResourceClasses,CORSFilter.class));
		assertTrue(contains(CyRESTConstants.coreResourceClasses,EventFlushResponseFilter.class));
		
		assertTrue(contains(CyRESTConstants.coreResourceClasses, CIResponseFilter.class));
		assertTrue(contains(CyRESTConstants.coreResourceClasses, CyExceptionMapper.class));
		
		assertEquals(23, CyRESTConstants.coreResourceClasses.length);

	}
	
	private boolean contains(Class<?>[] array, Class<?> key)
	{
		for (Class<?> entry : array){
			if (key.equals(entry)){
				return true;
			}
		}
		return false;
	}
}
