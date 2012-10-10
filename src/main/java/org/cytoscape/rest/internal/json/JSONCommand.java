package org.cytoscape.rest.internal.json;

import org.cytoscape.rest.internal.NetworkManager;

public interface JSONCommand {
	
	public void run(NetworkManager netMan);
	
}
