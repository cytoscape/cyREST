package org.cytoscape.rest.internal;

import org.cytoscape.rest.internal.commands.resources.CommandResource;
import org.cytoscape.rest.internal.resource.AlgorithmicResource;
import org.cytoscape.rest.internal.resource.AppsResource;
import org.cytoscape.rest.internal.resource.CIResponseFilter;
import org.cytoscape.rest.internal.resource.CORSFilter;
import org.cytoscape.rest.internal.resource.CollectionResource;
import org.cytoscape.rest.internal.resource.CyExceptionMapper;
import org.cytoscape.rest.internal.resource.CyRESTCommandSwagger;
import org.cytoscape.rest.internal.resource.EventFlushRequestFilter;
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

public class CyRESTConstants {
	public static final Class<?>[] coreResourceClasses = {
			AppsResource.class,
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
			PropertiesResource.class,
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
			
			//These filters ensure CyREST is in sync with Cytoscape events.
			EventFlushRequestFilter.class,
			EventFlushResponseFilter.class,
			
			//For Error Handling
			CyExceptionMapper.class
	};
	
	public final static String CYTOSCAPE_API_VERSION = "3.9.1";
	public final static String ANY_SERVICE_FILTER = "(&(objectClass=*)(!(com.eclipsesource.jaxrs.publish=false)))";
	
	public final static String CY_REST_CI_ROOT = "urn:cytoscape:ci:cyrest-core:v1";
	public final static String CY_REST_CI_ERROR_ROOT = "errors";
	
	public final static String CY_REST_HELP_MENU_ANCHOR = "Help.Automation[1.9999999]"; //That's enough 9's right? Yeah. Probably.

	public final static String getErrorURI(String resourceURI, int code) {
		return CY_REST_CI_ROOT + ":" + resourceURI + ":"+ CY_REST_CI_ERROR_ROOT + ":"+ code;
	}
	
	public final static String CX_FILE_FORMAT_LINK = "https://manual.cytoscape.org/en/stable/Supported_Network_File_Formats.html#cytoscape-cx";
	
	public final static String NNF_FILE_FORMAT_LINK = "https://manual.cytoscape.org/en/stable/Supported_Network_File_Formats.html#nnf";
	public final static String SIF_FILE_FORMAT_LINK = "https://manual.cytoscape.org/en/stable/Supported_Network_File_Formats.html#sif-format";
	public final static String XGMML_FILE_FORMAT_LINK = "https://manual.cytoscape.org/en/stable/Supported_Network_File_Formats.html?highlight=xgmml#xgmml-format";
	public final static String CYTOSCAPE_JS_FILE_FORMAT_LINK = "https://manual.cytoscape.org/en/stable/Supported_Network_File_Formats.html#cytoscape-js-json";

	public final static String CYTOSCAPE_JS_LINK = "https://cytoscape.github.io/cytoscape.js/";
	
	public static String BUNDLE_NAME = "Bundle-Name";
}
