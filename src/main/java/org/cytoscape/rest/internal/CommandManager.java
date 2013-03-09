package org.cytoscape.rest.internal;

import java.util.Map;

import org.cytoscape.work.TaskFactory;

public class CommandManager {
	
	public void addCommand(TaskFactory command, Map props) {
		System.out.println("got TF: " + command);
	}
	
	public void removeCommand(TaskFactory command, Map props) {
		
	}

}
