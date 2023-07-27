package org.cytoscape.rest.internal.model;

import org.cytoscape.rest.internal.CyRESTConstants;

public final class ModelConstants {
	
	//public static final String COLUMN_TYPES="Double,String,Boolean,Long,Integer,List";
	
	public static enum ColumnTypePrimitive {
		Double,
		String,
		Boolean,
		Long,
		Integer
	}
	
	public static enum ColumnTypeAll {
		Double,
		String,
		Boolean,
		Long,
		Integer,
		List
	}
	
	public static final String ROW_DESCRIPTION = "Data is represented by column names and their values.\n\n"
			+ "```json\n"
			+ "{\n" 
			+ "  \"name\": \"Hodor 1\",\n" 
			+ "  \"value\": 0.11,\n"
			+ "  \"matched\": false\n"
			+ "  ...\n" 
			+ "}\n"
			+ "```";
	
	
	public static final String NODE_VISUAL_PROPERTY_EXAMPLE = "```\n"
			+ "{\n" 
			+ "  \"visualProperty\": \"NODE_BORDER_WIDTH\",\n" 
			+ "  \"value\": 2\n" 
			+ "}\n"
			+ "```\n";
	public static final String NODE_VISUAL_PROPERTY_EXAMPLES = NODE_VISUAL_PROPERTY_EXAMPLE
			+ "```\n"
			+ "{\n" 
			+ "  \"visualProperty\": \"NODE_BORDER_PAINT\",\n" 
			+ "  \"value\": \"#CCCCCC\"\n" 
			+ "}\n"
			+ "```\n";
	
	public static final String EDGE_VISUAL_PROPERTY_EXAMPLE = "```\n"
			+ "{\n" 
			+ "  \"visualProperty\": \"EDGE_TRANSPARENCY\",\n" 
			+ "  \"value\": 170\n" 
			+ "}"
			+ "```\n";
	public static final String EDGE_VISUAL_PROPERTY_EXAMPLES = EDGE_VISUAL_PROPERTY_EXAMPLE  
			+ "```\n"
			+ "{\n" 
			+ "  \"visualProperty\": \"EDGE_PAINT\",\n" 
			+ "  \"value\": \"#808080\"\n" 
			+ " }"
			+ "```\n";
	
	public static final String NETWORK_VISUAL_PROPERTY_EXAMPLE = "```\n"
			+ "{\n" 
			+ "  \"visualProperty\": \"NETWORK_BACKGROUND_PAINT\",\n" 
			+ "  \"value\": \"#000000\"\n" 
			+ "}"
			+ "```\n" ;
	
	public static final String NETWORK_VISUAL_PROPERTY_EXAMPLES = NETWORK_VISUAL_PROPERTY_EXAMPLE
			+"```\n"
			+ "{\n" 
			+ "  \"visualProperty\": \"NETWORK_CENTER_X_LOCATION\",\n" 
			+ "  \"value\": 250\n"
			+ "}"
			+ "```\n";
	
	public static final String JAVADOC_BASE_LINK = "https://cytoscape.org/javadoc/" + CyRESTConstants.CYTOSCAPE_API_VERSION + "/";
	
	public static final String VISUAL_PROPERTY_JAVADOC_LINK = JAVADOC_BASE_LINK + "org/cytoscape/view/presentation/property/BasicVisualLexicon.html";
	public static final String PASSTHROUGH_MAPPING_JAVADOC_LINK = JAVADOC_BASE_LINK + "org/cytoscape/view/vizmap/mappings/PassthroughMapping.html"; 
	public static final String DISCREET_MAPPING_JAVADOC_LINK = JAVADOC_BASE_LINK +"org/cytoscape/view/vizmap/mappings/DiscreteMapping.html"; 
	public static final String CONTINUOUS_MAPPING_JAVADOC_LINK = JAVADOC_BASE_LINK +"org/cytoscape/view/vizmap/mappings/ContinuousMapping.html"; 

	public static final String MAPPING_EXAMPLES = "#### Discrete Mapping\n[JavaDoc API](" + ModelConstants.DISCREET_MAPPING_JAVADOC_LINK+")\n"
			+ "```\n"
			+ "{ \"mappingType\": \"discrete\",\n" 
			+ "  \"mappingColumn\": \"interaction\",\n" 
			+ "	 \"mappingColumnType\": \"String\",\n" 
			+ "	 \"visualProperty\": \"EDGE_WIDTH\", \n" 
			+ "	 \"map\": [\n" 
			+ "      { \"key\" : \"pd\",\n "
			+ "        \"value\" : \"20\"\n"
			+ "      },\n" 
			+ "      { \"key\" : \"pp\",\n"
			+ "        \"value\" : \"1.5\"\n"
			+ "      }\n" 
			+ "    ]\n" 
			+ "}"
			+ "```\n"
			+ "#### Continuous Mapping\n[JavaDoc API](" + ModelConstants.CONTINUOUS_MAPPING_JAVADOC_LINK+")\n"
			+ "```\n"
			+ "{ \"mappingType\": \"continuous\",\n" 
			+ "  \"mappingColumn\": \"Degree\",\n" 
			+ "  \"mappingColumnType\": \"Integer\",\n" 
			+ "  \"visualProperty\": \"NODE_SIZE\",\n" 
			+ "  \"points\": [\n" 
			+ "      { \"value\" : 1,\n"
			+ "        \"lesser\" : \"20\",\n"
			+ "        \"equal\" : \"20\",\n"
			+ "        \"greater\" : \"20\"\n"
			+ "      },\n" 
			+ "	     { \"value\" : 20,\n"
			+ "        \"lesser\" : \"120\",\n"
			+ "        \"equal\" : \"120\",\n"
			+ "        \"greater\" : \"220\""
			+ "      }\n" 
			+ "    ]\n" 
			+ "}"
			+ "```\n"
			+ "#### Passthrough Mapping\n[JavaDoc API](" + ModelConstants.PASSTHROUGH_MAPPING_JAVADOC_LINK+")\n"
			+ "```\n"
			+ "{ \"mappingType\": \"passthrough\",\n" 
			+ "  \"mappingColumn\": \"name\",\n" 
			+ "  \"mappingColumnType\": \"String\",\n" 
			+ "  \"visualProperty\": \"EDGE_LABEL\" \n" 
			+ "}"
			+ "```\n";


	public static final String VISUAL_PROPERTIES_REFERENCES = "Additional details on common Visual Properties can be found in the [Basic Visual Lexicon JavaDoc API](" +VISUAL_PROPERTY_JAVADOC_LINK+ ")";
}
