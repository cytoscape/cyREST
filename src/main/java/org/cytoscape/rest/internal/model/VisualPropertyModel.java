package org.cytoscape.rest.internal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class VisualPropertyModel {
	
	@ApiModelProperty(value="Unique internal name of the Visual Property", example="NODE_SHAPE")
	public String visualProperty;
	
	@ApiModelProperty(value="Human-readable name of the Visual Property. This can be seen primarily in the Cytoscape GUI", example="Node Shape")
	public String name;
	
	@ApiModelProperty(value="The data type of the Objects to which this Visual Property applies, represented as the simple name of its Java class.", example="CyNode")
	public String targetDataType;
	
	@SerializedName("default")
	@JsonProperty("default")
	@ApiModelProperty(value="Default value of this Visual Property", example="ELLIPSE")
	public String _default;
	
	public VisualPropertyModel()	{
		
	}
	
	public VisualPropertyModel(org.cytoscape.view.model.VisualProperty<Object> vp){
		this.visualProperty = vp.getIdString();
		this.name = vp.getDisplayName();
		this.targetDataType = vp.getTargetDataType().getSimpleName();
		this._default = vp.toSerializableString(vp.getDefault());
	}
}
