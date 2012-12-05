package org.cytoscape.rest;

import static org.junit.Assert.assertEquals;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.NetworkTestSupport;
import org.cytoscape.rest.internal.translator.CyNetwork2JSONTranslator;

public class BugTester {

	private static Translator<String, CyNetwork> translator;
	private final static NetworkTestSupport testSupport = new NetworkTestSupport();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		translator = new CyNetwork2JSONTranslator();
		
		final CyNetwork network = testSupport.getNetwork();
		final CyNode node1 = network.addNode();
		final CyNode node2 = network.addNode();
		final CyNode node3 = network.addNode();
		final CyEdge edge = network.addEdge(node1, node2, true);
		node1.setNetworkPointer(network);
		node2.setNetworkPointer(network);
		node3.setNetworkPointer(network);
		
		final String result = translator.translate(network);
		System.out.println(result);
		//assertEquals("{\"SUID\":"+network.getSUID()+"}", result);

	}

}
