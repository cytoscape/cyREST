package org.cytoscape.rest.internal.json;

import java.util.Vector;
import org.cytoscape.rest.internal.NetworkManager;

public class JSONTable implements JSONCommand {

	private String table_name;
	private Vector<String> table_headings;
	private Vector<String> table_types;
	private Vector<String> row_ids;
	private Vector<String> table_data;
	
	public void run(NetworkManager netMan) {
		 netMan.pushTable(table_name, table_headings, table_types, row_ids, table_data);
	}
	
}
