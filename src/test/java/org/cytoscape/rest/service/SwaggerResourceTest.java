package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;

public class SwaggerResourceTest extends BasicResourceTest
{
	public void swaggerConfigTest(JsonNode root)
	{
		assertNotNull(root.has("swagger"));
		assertNotNull(root.has("info"));
		assertNotNull(root.has("host"));
		assertNotNull(root.has("basePath"));
		assertNotNull(root.has("tags"));
		assertNotNull(root.has("schemes"));
		assertNotNull(root.has("consumes"));
		assertNotNull(root.has("produces"));
		assertNotNull(root.has("paths"));
		assertNotNull(root.has("externalDocs"));
		
		final JsonNode hostNode = root.get("host");
		assertEquals("0.0.0.0:" + cyRESTPort , hostNode.asText());
		
		final JsonNode pathNode = root.get("basePath");
		assertEquals("/", pathNode.asText());
	}
}
