package org.cytoscape.rest.internal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;

@ApiModel
public class VisualProperty {
	public String visualProperty;
	public String name;
	public String targetDataType;
	
	@SerializedName("default")
	@JsonProperty("default")
	public String _default;
	
	public VisualProperty()	{
		
	}
	
	public VisualProperty(org.cytoscape.view.model.VisualProperty<Object> vp){
		this.visualProperty = vp.getIdString();
		this.name = vp.getDisplayName();
		this.targetDataType = vp.getTargetDataType().getSimpleName();
		this._default = vp.toSerializableString(vp.getDefault());
	}
}
