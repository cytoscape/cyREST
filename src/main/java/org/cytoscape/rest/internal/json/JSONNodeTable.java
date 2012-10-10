package org.cytoscape.rest.internal.json;

import java.util.Vector;

import org.cytoscape.rest.internal.NetworkManager;

public class JSONNodeTable implements JSONCommand {
	
	private String network_name;
	private Vector<String> table_headings;
	private Vector<String> table_types;
	private Vector<Integer> node_cytobridge_ids;
	private Vector<String> table_data;
	
		  
		   
	public void run(NetworkManager netMan) {
		 netMan.pushNodeTable(network_name, table_headings, table_types, node_cytobridge_ids, table_data);
	}

}