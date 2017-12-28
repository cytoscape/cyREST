package org.cytoscape.rest.internal.model;

import io.swagger.annotations.ApiModel;

@ApiModel(value="Row Data", description="A map of column name to value.\n\n"
		+ "```\n"
		+ "{\n" 
		+ "  \"id\": 12345,\n"  
		+ "  \"gene_name\": \"brca1\",\n" 
		+ "  \"exp1\": 0.11,\n" 
		+ "  \"exp2\": 0.2\n" 
		+ "}\n"
		+ "```")
public class CyRowModel{

}
