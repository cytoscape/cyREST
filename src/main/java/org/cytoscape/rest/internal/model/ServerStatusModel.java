package org.cytoscape.rest.internal.model;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement
public class ServerStatusModel {
	
	private String apiVersion;
	private Integer numberOfCores;
	
	private MemoryStatusModel memoryStatus;

	
	public ServerStatusModel() {
		this.setApiVersion("v1");
		this.setNumberOfCores(Runtime.getRuntime().availableProcessors());
		this.setMemoryStatus(new MemoryStatusModel());
	}
	
	/**
	 * @return the apiVersion
	 */
	@ApiModelProperty(value="CyREST API Version")
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
	@ApiModelProperty(value="Number of Processor Cores Available")
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
	@ApiModelProperty(value="Details on memory use and availability.")
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
