package org.cytoscape.rest.internal.model;

/**
 * Dummy class for generating API document.
 */
public class CyJsNetwork {

	private NetworkData data;
	private CyJsElements elements;

	/**
	 * @return the data
	 */
	public NetworkData getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(NetworkData data) {
		this.data = data;
	}

	/**
	 * @return the elements
	 */
	public CyJsElements getElements() {
		return elements;
	}
}
