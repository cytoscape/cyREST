package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Row data associated with the Edge" + ModelConstants.ROW_DESCRIPTION)
public class EdgeDataModel {

	private String source;
	private String target;

	/**
	 * @return the source
	 */
	@ApiModelProperty(value="SUID of the Edge's Source Node", required=true)
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the target
	 */
	@ApiModelProperty(value="SUID of the Edge's Target Node", required=true)
	public String getTarget() {
		return target;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}
}
