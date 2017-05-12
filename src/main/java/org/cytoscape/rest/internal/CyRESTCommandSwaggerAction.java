package org.cytoscape.rest.internal;

import org.cytoscape.service.util.CyServiceRegistrar;

public class CyRESTCommandSwaggerAction extends CyRESTSwaggerAction{

	public CyRESTCommandSwaggerAction(CyServiceRegistrar serviceRegistrar, String cyRESTPort) {
		super("CyREST Command API", serviceRegistrar, cyRESTPort);
	}

	protected String rootURL()	{
		return "http://localhost:"+getCyRESTPort()+"/v1/swaggerUI/swagger-ui/index.html";
	}
	
	protected String swaggerPath() {
		return "v1/commands/swagger.json";
	}
	
}
