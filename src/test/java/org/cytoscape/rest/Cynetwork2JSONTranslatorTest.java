package org.cytoscape.rest;

import static org.junit.Assert.*;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.NetworkTestSupport;
import org.cytoscape.rest.internal.translator.CyNetwork2JSONTranslator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

public class Cynetwork2JSONTranslatorTest {

	private Translator<String, CyNetwork> translator;
	private final NetworkTestSupport testSupport = new NetworkTestSupport();
	
	
	@Before
	public void setUp() throws Exception {
		translator = new CyNetwork2JSONTranslator();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTranslation() {
		final CyNetwork network = testSupport.getNetwork();
		final CyNode node1 = network.addNode();
		final CyNode node2 = network.addNode();
		final CyEdge edge = network.addEdge(node1, node2, true);
		
//		final String result = translator.translate(network);
//		assertNotNull(result);
	}

}
