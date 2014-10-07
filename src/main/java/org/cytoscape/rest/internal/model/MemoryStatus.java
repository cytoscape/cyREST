package org.cytoscape.rest.internal.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MemoryStatus {

	private static final Integer MB = 1024 * 1024;
	private final Runtime runtime = Runtime.getRuntime();
	private final Long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / MB;
	private final Long freeMemory = runtime.freeMemory() / MB;
	private final Long totalMemory = runtime.totalMemory() / MB;
	private final Long maxMemory = runtime.maxMemory() / MB;

	/**
	 * @return the usedMemory
	 */
	public Long getUsedMemory() {
		return usedMemory;
	}

	/**
	 * @return the freeMemory
	 */
	public Long getFreeMemory() {
		return freeMemory;
	}

	/**
	 * @return the totalMemory
	 */
	public Long getTotalMemory() {
		return totalMemory;
	}

	/**
	 * @return the maxMemory
	 */
	public Long getMaxMemory() {
		return maxMemory;
	}
}
