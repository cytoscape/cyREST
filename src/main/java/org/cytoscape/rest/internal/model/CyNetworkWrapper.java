package org.cytoscape.rest.internal.model;

import org.cytoscape.rest.internal.serializer.CyNetworkSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = CyNetworkSerializer.class)
public class CyNetworkWrapper {

	private NetworkData data;
	private Elements elements;

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
	public Elements getElements() {
		return elements;
	}
}
