package org.cytoscape.work.json;

/**
 * This interface is intended to facilitate retrieving valid JSON strings from 
 * {@link org.cytoscape.work.ObservableTask#getResults(Class)}. The primary intended user of this interface is CyREST,
 * which will execute commands, and can benefit from returning JSON instead of Strings of unknown format, however, JSON
 * data may be useful for apps that wish to retrieve data from another app's commands.
 * 
 * @author davidotasek
 *
 */
public interface JSONResult {
	
	/**
	 * 
	 * @return A valid JSON String.
	 */
	public String getJSON();
}
