package org.cytoscape.rest;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.HTMLEditorKit.Parser;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.NetworkTestSupport;
import org.cytoscape.rest.internal.translator.CyNetwork2JSONTranslator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

public class CyNetwork2JSONTranslatorTest {

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
	public void testSanity() {
		
	}
	@Test
	public void testTranslation() throws Exception {
		CyNetwork network = testSupport.getNetwork();
		final CyNode node1 = network.addNode();
		final CyNode node2 = network.addNode();
		final CyNode node3 = network.addNode();
		final CyEdge edge1 = network.addEdge(node1, node2, true);
		final CyEdge edge2 = network.addEdge(node1, node3, true);
		final CyEdge edge3 = network.addEdge(node2, node3, true);
		
		final List<String> sampleList = new ArrayList<String>();
		sampleList.add("term1");
		sampleList.add("term2");
		sampleList.add("term3");
		
		final List<Double> numberList = new ArrayList<Double>();
		numberList.add(4.2322);
		numberList.add(2.33);
		
		network.getDefaultNodeTable().createListColumn("number list", Double.class, false);
		network.getRow(node1).set("number list", numberList);
		
		network.getRow(network).set(CyNetwork.NAME, "Sample Network");
		network.getDefaultNetworkTable().createListColumn("String List Col", String.class, false);
		network.getRow(network).set("String List Col", sampleList);
		
		final String result = translator.translate(network);
		assertNotNull(result);
		
		System.out.println(result);
		
		assertTrue(result.contains("term1"));
	}

}