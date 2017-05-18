package org.cytoscape.rest.internal;

import org.cytoscape.rest.internal.commands.resources.CommandResource;
import org.cytoscape.rest.internal.resource.AlgorithmicResource;
import org.cytoscape.rest.internal.resource.CIResponseFilter;
import org.cytoscape.rest.internal.resource.CORSFilter;
import org.cytoscape.rest.internal.resource.CollectionResource;
import org.cytoscape.rest.internal.resource.CyExceptionMapper;
import org.cytoscape.rest.internal.resource.CyRESTCommandSwagger;
import org.cytoscape.rest.internal.resource.GlobalTableResource;
import org.cytoscape.rest.internal.resource.GroupResource;
import org.cytoscape.rest.internal.resource.MiscResource;
import org.cytoscape.rest.internal.resource.NetworkFullResource;
import org.cytoscape.rest.internal.resource.NetworkNameResource;
import org.cytoscape.rest.internal.resource.NetworkResource;
import org.cytoscape.rest.internal.resource.NetworkViewResource;
import org.cytoscape.rest.internal.resource.RootResource;
import org.cytoscape.rest.internal.resource.SessionResource;
import org.cytoscape.rest.internal.resource.StyleResource;
import org.cytoscape.rest.internal.resource.SwaggerUIResource;
import org.cytoscape.rest.internal.resource.TableResource;
import org.cytoscape.rest.internal.resource.UIResource;

public class CyRESTConstants {
	public static final Class<?>[] coreResourceClasses = {
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
			
			SwaggerUIResource.class,

			//For CORS
			CORSFilter.class,
			
			//For CI
			CIResponseFilter.class,
			
			//For Error Handling
			CyExceptionMapper.class
	};
	
	public final static String cyRESTCIRoot = "urn:cytoscape:ci:cyrest-core:v1";
	public final static String cyRESTCIErrorRoot = ":errors";
	
	public final static String cyRESTHelpMenu = "Help.Automation[1.9999999]"; //That's enough 9's right? Yeah. Probably.
}
