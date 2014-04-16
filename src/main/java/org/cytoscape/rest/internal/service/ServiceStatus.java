package org.cytoscape.rest.internal.service;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name = "status")
public class ServiceStatus {

	@JsonProperty("value")
	private String version = "3.1.0";
	
}
