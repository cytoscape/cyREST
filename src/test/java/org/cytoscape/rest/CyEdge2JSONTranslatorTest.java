package org.cytoscape.rest;

import static org.junit.Assert.*;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.NetworkTestSupport;
import org.cytoscape.rest.internal.translator.CyEdge2JSONTranslator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class CyEdge2JSONTranslatorTest {

	private Translator<String, CyEdge> translator;
	private final NetworkTestSupport testSupport = new NetworkTestSupport();


	@Before
	public void setUp() throws Exception {
		translator = new CyEdge2JSONTranslator();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSanity() {
		
	}
	@Test
	public void testTranslation() {
		final CyNetwork network = testSupport.getNetwork();
		final CyNode node1 = network.addNode();
		final CyNode node2 = network.addNode();
		final CyEdge edge = network.addEdge(node1, node2, true);
		node1.setNetworkPointer(network);
		
		//Test valid translation
		assertEquals("{\"source\":"+node1.getSUID()+",\"target\":"+node2.getSUID()+",\"isDirected\":true,\"interaction\":null,\"selected\":false,\"shared interaction\":null,\"shared name\":null,\"SUID\":"+edge.getSUID()+",\"name\":null}",translator.translate(edge));
	}

}