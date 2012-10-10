package org.cytoscape.rest.internal.json;

import java.util.Vector;

import org.cytoscape.rest.internal.NetworkManager;

public class JSONEdgeTable implements JSONCommand {
	
	private String network_name;
	private Vector<String> table_headings;
	private Vector<String> table_types;
	private Vector<Integer> edge_cytobridge_ids;
	private Vector<String> table_data;
	
		  
		   
	public void run(NetworkManager netMan) {
		 netMan.pushEdgeTable(network_name, table_headings, table_types, edge_cytobridge_ids, table_data);
	}

}