package org.cytoscape.rest;

import org.cytoscape.rest.internal.task.CyRESTPort;

import com.google.inject.Inject;

public class TestClass
{
		@Inject
		@CyRESTPort
		String cyRestPort;
		
		public boolean equals(Object object)
		{
			return object instanceof TestClass;
		}
}