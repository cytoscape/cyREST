package org.cytoscape.rest.internal.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ServerStatus {
	
	private String apiVersion;
	private Integer numberOfCores;
	
	private MemoryStatusModel memoryStatus;

	
	public ServerStatus() {
		this.setApiVersion("v1");
		this.setNumberOfCores(Runtime.getRuntime().availableProcessors());
		this.setMemoryStatus(new MemoryStatusModel());
	}
	
	/**
	 * @return the apiVersion
	 */
	public String getApiVersion() {
		return apiVersion;
	}

	/**
	 * @param apiVersion
	 *            the apiVersion to set
	 */
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	/**
	 * @return the numberOfCores
	 */
	public Integer getNumberOfCores() {
		return numberOfCores;
	}

	/**
	 * @param numberOfCores
	 *            the numberOfCores to set
	 */
	public void setNumberOfCores(Integer numberOfCores) {
		this.numberOfCores = numberOfCores;
	}

	/**
	 * @return the memoryStatus
	 */
	public MemoryStatusModel getMemoryStatus() {
		return memoryStatus;
	}

	/**
	 * @param memoryStatus the memoryStatus to set
	 */
	public void setMemoryStatus(MemoryStatusModel memoryStatus) {
		this.memoryStatus = memoryStatus;
	}

}
