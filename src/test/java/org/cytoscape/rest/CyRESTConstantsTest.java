package org.cytoscape.rest;

import static org.junit.Assert.assertEquals;
import org.cytoscape.rest.internal.CyRESTConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CyRESTConstantsTest {
	
	@Before
	public void setUp() throws Exception {
	
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCytoscapeAPIVersion() {
		/*This test is here to ensure that any documentation we're linking to from within the Swagger docs
		  is the same as the API that the plugin relies upon. 
		  
		  This isn't as good as being able to set it directly via a maven property, but if this test fails,
		  at least you can be directed to where you're diverging the code from the maven properties.
		*/
		var prop = System.getProperty("cytoscape.api.version");
		
		// this property can be null when running the tests from inside eclipse
		if(prop != null) {
			assertEquals(prop, CyRESTConstants.CYTOSCAPE_API_VERSION);
		}
	}
	
}