package org.cytoscape.rest.internal;

import org.cytoscape.service.util.CyServiceRegistrar;

public class CyRESTCoreSwaggerAction extends CyRESTSwaggerAction{

	public CyRESTCoreSwaggerAction(CyServiceRegistrar serviceRegistrar, String cyRESTPort) {
		super("CyREST API", serviceRegistrar, cyRESTPort);
	}

	protected String rootURL()	{
		return "http://chianti.ucsd.edu/~bdemchak/CySwagger/core/";
	}
	
	protected String swaggerPath() {
		return "v1/swagger.json";
	}
	
}
