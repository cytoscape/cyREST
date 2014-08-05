package org.cytoscape.rest.internal;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.rest.TaskFactoryManager;
import org.cytoscape.task.NetworkCollectionTaskFactory;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;


@SuppressWarnings("rawtypes")
public class TaskFactoryManagerImpl implements TaskFactoryManager {

	private Map<String, Object> tfMap = new HashMap<String, Object>();

	public void addTaskFactory(TaskFactory tf, Map props) {
		if (tf == null)
			return;

		Object tfID = props.get(ServiceProperties.ID);
		if (tfID != null) {
			tfMap.put(tfID.toString(), tf);
			// System.out.println("## got TF: " + tfID);
		} else {
			// generate ID from class Name
			String tfName = tf.toString();
			// System.out.println("tfName = " + tfName);
			tfMap.put(tfName, tf);
		}
	}

	public void addNetworkTaskFactory(NetworkTaskFactory tf, Map props) {
		if (tf == null)
			return;

		Object tfID = props.get(ServiceProperties.ID);
		if (tfID != null) {
			tfMap.put(tfID.toString(), tf);
			// System.out.println("## got TF: " + tfID);
		} else {
			// generate ID from class Name
			String tfName = tf.toString();
			// System.out.println("tfName = " + tfName);
			tfMap.put(tfName, tf);
		}
	}

	public void addNetworkCollectionTaskFactory(NetworkCollectionTaskFactory tf, Map props) {
		if (tf == null)
			return;

		Object tfID = props.get(ServiceProperties.ID);
		if (tfID != null) {
			tfMap.put(tfID.toString(), tf);
			// System.out.println("## got NC TF: " + tfID);
		} else {
			// generate ID from class Name
			String tfName = tf.getClass().getSimpleName();
			// System.out.println("NC = " + tfName);
			tfMap.put(tfName, tf);
		}
	}

	public void removeTaskFactory(TaskFactory command, Map props) {
	}

	public void removeNetworkTaskFactory(NetworkTaskFactory command, Map props) {
	}

	public void removeNetworkCollectionTaskFactory(NetworkCollectionTaskFactory command, Map props) {
	}

	public TaskFactory getTaskFactory(final String id) {
		Object tf = tfMap.get(id);
		if (tf instanceof TaskFactory)
			return (TaskFactory) tf;
		else
			return null;
	}

	public NetworkTaskFactory getNetworkTaskFactory(final String id) {
		Object tf = tfMap.get(id);
		if (tf instanceof NetworkTaskFactory)
			return (NetworkTaskFactory) tf;
		else
			return null;
	}

	public NetworkCollectionTaskFactory getNetworkCollectionTaskFactory(final String id) {
		Object tf = tfMap.get(id);
		if (tf instanceof NetworkCollectionTaskFactory)
			return (NetworkCollectionTaskFactory) tf;
		else
			return null;
	}

}
