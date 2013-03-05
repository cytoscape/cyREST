package org.cytoscape.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.NetworkTestSupport;
import org.cytoscape.rest.internal.translator.CyEdge2JSONTranslator;
import org.junit.Before;
import org.junit.Test;

public class CyEdge2JSONTranslatorTest extends JSONTranslatorTest {

	private Translator<String, CyEdge> translator;
	private final NetworkTestSupport testSupport = new NetworkTestSupport();

	@Before
	public void setUp() throws Exception {
		translator = new CyEdge2JSONTranslator();
	}


	@Test
	public void testTranslation() {
		final CyNetwork network = testSupport.getNetwork();
		final CyNode node1 = network.addNode();
		final CyNode node2 = network.addNode();
		final CyEdge edge = network.addEdge(node1, node2, true);

		assertNotNull(edge);
		assertEquals(node1, edge.getSource());
		assertEquals(node2, edge.getTarget());

		// Translation
		final String result = translator.translate(edge);
		assertNotNull(result);

		assertTrue(result.contains("\"source\":" + node1.getSUID()));
		assertTrue(result.contains("\"target\":" + node2.getSUID()));
		assertTrue(result.contains("\"isDirected\":" + edge.isDirected()));
	}
}