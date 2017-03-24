package org.cytoscape.rest.internal.resource.apps.clustermaker2;

import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class MCODEParameters 
{
	@ApiModelProperty(value = "Cluster only selected nodes", example="false")
	public Boolean selectedOnly = false;

	@ApiModelProperty(value = "Include Loops", example="false")
	public Boolean includeLoops = false;

	@ApiModelProperty(value = "Degree Cutoff", example="2")
	public Integer degreeCutoff = 2;

	@ApiModelProperty(value = "Hair Cut", example="true")
	public Boolean haircut = true;

	@ApiModelProperty(value = "Fluff", example="false")
	public Boolean fluff = false;

	@ApiModelProperty(value = "Node Score Cutoff", example="0.2")
	public Double scoreCutoff = 0.2;

	@ApiModelProperty(value = "K-Core", example="2")
	public Integer kCore = 2;

	@ApiModelProperty(value = "Max Depth", example="100")
	public Integer maxDepth = 100;
	
	public MCODEParameters()
	{
		this.selectedOnly = false;
		this.includeLoops = false;
		this.degreeCutoff = 2;
		this.haircut = true;
		this.fluff = false;
		this.scoreCutoff = 0.2;
		this.kCore = 2;
		this.maxDepth = 100;
	
	}
	
	protected  Map<String, Object> getTunableMap()
	{
		Map<String, Object> tunableMap;
		tunableMap = new HashMap<String, Object>();
		tunableMap.put("selectedOnly", this.selectedOnly);
		tunableMap.put("includeLoops", this.includeLoops);
		tunableMap.put("degreeCutoff", this.degreeCutoff);
		tunableMap.put("haircut", this.haircut);
		tunableMap.put("fluff", this.fluff);
		tunableMap.put("scoreCutoff", this.scoreCutoff);
		tunableMap.put("kCore", this.kCore);
		tunableMap.put("maxDepth", this.maxDepth);
		return tunableMap;
	}
}
