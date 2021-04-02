package org.cytoscape.rest.internal.systemtest;

import java.lang.reflect.Field;

import org.cytoscape.rest.internal.resource.*;

import org.cytoscape.service.util.CyServiceRegistrar;

public class CyRESTResources {
	
	protected final AlgorithmicResource algorithmicResource;
	protected final AppsResource appsResource;
	protected final CollectionResource collectionResource;
	protected final CyRESTCommandSwagger cyRESTCommandSwagger;
	protected final CyRESTSwagger cyRESTSwagger;
	protected final GlobalTableResource globalTableResource;
	protected final GroupResource groupResource;
	protected final MiscResource miscResource;
	protected final NetworkFullResource networkFullResource;
	protected final NetworkNameResource networkNameResource;
	protected final NetworkResource networkResource;
	protected final NetworkViewResource networkViewResource;
	protected final PropertiesResource propertiesResource;
	protected final RootResource rootResource;
	protected final SessionResource sessionResource;
	protected final StyleResource styleResource;
	protected final SwaggerUIResource swaggerUIResource;
	protected final TableResource tableResource;
	protected final UIResource uiResource;
	
	public CyRESTResources (CyServiceRegistrar serviceRegistrar) {
		algorithmicResource = serviceRegistrar.getService(AlgorithmicResource.class);
		appsResource= serviceRegistrar.getService(AppsResource.class);
		collectionResource= serviceRegistrar.getService(CollectionResource.class);
		cyRESTCommandSwagger= serviceRegistrar.getService(CyRESTCommandSwagger.class);
		cyRESTSwagger= serviceRegistrar.getService(CyRESTSwagger.class);
		globalTableResource= serviceRegistrar.getService(GlobalTableResource.class);
		groupResource= serviceRegistrar.getService(GroupResource.class);
		miscResource= serviceRegistrar.getService(MiscResource.class);
		networkFullResource= serviceRegistrar.getService(NetworkFullResource.class);
		networkNameResource= serviceRegistrar.getService(NetworkNameResource.class);
		networkResource = serviceRegistrar.getService(NetworkResource.class);
		networkViewResource = serviceRegistrar.getService(NetworkViewResource.class);
		propertiesResource= serviceRegistrar.getService(PropertiesResource.class);
		rootResource= serviceRegistrar.getService(RootResource.class);
		sessionResource= serviceRegistrar.getService(SessionResource.class);
		styleResource= serviceRegistrar.getService(StyleResource.class);
		swaggerUIResource= serviceRegistrar.getService(SwaggerUIResource.class);
		tableResource= serviceRegistrar.getService(TableResource.class);
		uiResource= serviceRegistrar.getService(UIResource.class);		
	}
}
