package org.cytoscape.rest.internal.json;

import java.util.Vector;

import org.cytoscape.rest.internal.NetworkManager;

public class JSONNetworkTable implements JSONCommand {
	
	private String network_name;
	private Vector<String> table_headings;
	private Vector<String> table_types;
	private Vector<String> table_data;
	
		  
		   
	public void run(NetworkManager netMan) {
		 netMan.pushNetTable(network_name, table_headings, table_types, table_data);
	}

}
