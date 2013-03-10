package org.cytoscape.rest.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.work.TaskFactory;

public class CommandManager {
	
	private Map<String, TaskFactory> tfMap = new HashMap<String, TaskFactory>();
	
	public void addCommand(TaskFactory command, Map props) {
		Object tfID = props.get("id");
		if(tfID != null) {
			tfMap.put(tfID.toString(), command);
			System.out.println("## got TF: " + tfID);
		}
	}
	
	public void removeCommand(TaskFactory command, Map props) {
		
	}
	
	public Set<TaskFactory> getAllTaskFactories() {
		return new HashSet<TaskFactory>(tfMap.values());
	}
	
	public TaskFactory getTaskFactory(final String name) {
		return tfMap.get(name);
	}

}
