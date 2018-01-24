package org.cytoscape.rest.internal.model;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@XmlRootElement
@ApiModel(value="Memory Status", description="Details on memory use and availability.")
public class MemoryStatusModel {

	private static final Integer MB = 1024 * 1024;
	private static Runtime runtime = Runtime.getRuntime();
	private Long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / MB;
	private Long freeMemory = runtime.freeMemory() / MB;
	private Long totalMemory = runtime.totalMemory() / MB;
	private Long maxMemory = runtime.maxMemory() / MB;

	public MemoryStatusModel()
	{
		
	}
	
	/**
	 * @return the usedMemory
	 */
	@ApiModelProperty(value="Memory used by the JVM, in MB")
	public Long getUsedMemory() {
		return usedMemory;
	}
	
	public void setUsedMemory(Long usedMemory)
	{
		
	}
	

	/**
	 * @return the freeMemory
	 */
	@ApiModelProperty(value="Free Memory available to the JVM, in MB")
	public Long getFreeMemory() {
		return freeMemory;
	}
	
	public void setFreeMemory(Long freeMemory)
	{
		
	}

	/**
	 * @return the totalMemory
	 */
	@ApiModelProperty(value="Total Memory available to the JVM, in MB")
	public Long getTotalMemory() {
		return totalMemory;
	}
	
	public void setTotalMemory(Long totalMemory)
	{
		
	}

	/**
	 * @return the maxMemory
	 */
	@ApiModelProperty(value="Maximum Memory that the JVM will use, in MB")
	public Long getMaxMemory() {
		return maxMemory;
	}
	
	public void setMaxMemory(Long maxMemory)
	{
		
	}
}
