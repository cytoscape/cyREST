package org.cytoscape.rest.internal.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.cytoscape.property.CyProperty;

public class CyPropertyListener {
	
	public static final String CY_PROPERTY_NAME = "cyPropertyName";
	
	Map<String, CyProperty<?>> cyPropertyMap = new HashMap<String, CyProperty<?>>();
	
	public void addCyProperty(CyProperty<?> property, Map<?,?> serviceProperties){
		String propertyName = property.getName();
		Object obj = property.getProperties();
		String serviceCyPropertyName = (String) serviceProperties.get(CY_PROPERTY_NAME);
		//System.out.println("CyREST CyPropertyListener added property: " + propertyName);
		
		if (propertyName != null && serviceCyPropertyName != null && isJavaProperty(property)) {
			//System.out.println("CyProperty: " + propertyName + serviceCyPropertyName);
			cyPropertyMap.put(serviceCyPropertyName, property);
			
		} else {
			System.out.println("Type: " + obj.getClass());
		}
		
	}
	
	public CyProperty<?> getCyProperty(String propertyName) {
		return cyPropertyMap.get(propertyName);
	}
	
	public List<String> getPropertyNames() {
		List<String> output = new ArrayList<String>();
		output.addAll(cyPropertyMap.keySet());
		return output;
	}
	
	public boolean isJavaProperty(CyProperty<?> property) {
		return property.getProperties() instanceof Properties;
	}
	
	public void removeCyProperty(CyProperty<?> property, Map<?,?> serviceProperties){
		String propertyName = property.getName();
		Object obj = property.getProperties();
		
	}
}
