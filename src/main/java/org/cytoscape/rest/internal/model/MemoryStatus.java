package org.cytoscape.rest.internal.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MemoryStatus {

	private static final Integer MB = 1024 * 1024;
	private static Runtime runtime = Runtime.getRuntime();
	private Long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / MB;
	private Long freeMemory = runtime.freeMemory() / MB;
	private Long totalMemory = runtime.totalMemory() / MB;
	private Long maxMemory = runtime.maxMemory() / MB;

	public MemoryStatus()
	{
		
	}
	
	/**
	 * @return the usedMemory
	 */
	public Long getUsedMemory() {
		return usedMemory;
	}
	
	public void setUsedMemory(Long usedMemory)
	{
		
	}
	

	/**
	 * @return the freeMemory
	 */
	public Long getFreeMemory() {
		return freeMemory;
	}
	
	public void setFreeMemory(Long freeMemory)
	{
		
	}

	/**
	 * @return the totalMemory
	 */
	public Long getTotalMemory() {
		return totalMemory;
	}
	
	public void setTotalMemory(Long totalMemory)
	{
		
	}

	/**
	 * @return the maxMemory
	 */
	public Long getMaxMemory() {
		return maxMemory;
	}
	
	public void setMaxMemory(Long maxMemory)
	{
		
	}
}
