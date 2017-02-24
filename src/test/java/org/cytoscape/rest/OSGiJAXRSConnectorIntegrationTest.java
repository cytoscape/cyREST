package org.cytoscape.rest;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class OSGiJAXRSConnectorIntegrationTest 
{
	@Test
	public void testSanity() 
	{
		File f = new File("./src/main/resources/glassfish-jersey/jersey-client-2.22.2.jar");
		assertTrue(f.exists());
	}
}
