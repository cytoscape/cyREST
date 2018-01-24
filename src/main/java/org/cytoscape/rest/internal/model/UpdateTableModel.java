package org.cytoscape.rest.internal.model;

import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Table Update Data")
public class UpdateTableModel {
	@ApiModelProperty(value = "The column in the target table to use as a key. If not specified, SUID will be used.", example="SUID")
	public String key;
	@ApiModelProperty(value = "The field in the row data to use as a key. If not specified, SUID will be used.", example="id")
	public String dataKey;
	@ApiModelProperty(value = "The row data with which to update the table.\n\nEach row entry should consist of pairs of keys and values, including one that supplies a value for the `dataKey` key. \n"
			+ "```\n"
			+ "[\n"
			+ "  {\n"
			+ "    \"id\": 12345,\n"
			+ "    \"gene_name\": \"brca1\",\n"  
			+ "    \"exp1\": 0.11,\n"  
			+ "    \"exp2\": 0.2\n"
			+ "  },...\n"
			+ "]\n"
			+ "```\n", required=true)
	public List<CyRowModel> data;
}
