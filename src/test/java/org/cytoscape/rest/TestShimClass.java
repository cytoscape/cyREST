package org.cytoscape.rest;

import javax.ws.rs.Path;

import org.cytoscape.rest.internal.task.CyRESTPort;

import com.google.inject.Inject;

@Path("/v1/apps/")
public class TestShimClass {
		@Inject
		@CyRESTPort
		public String cyRestPort;
		
		@Inject
		public Object injected;
		
		public boolean equals(Object object)
		{
			return object instanceof TestShimClass;
		}
	}