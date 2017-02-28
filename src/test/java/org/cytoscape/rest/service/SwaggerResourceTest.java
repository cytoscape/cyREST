package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;

public class SwaggerResourceTest extends BasicResourceTest
{
	public void swaggerConfigTest(JsonNode root)
	{
		assertTrue(root.has("swagger"));
		assertTrue(root.has("info"));
		assertTrue(root.has("host"));
		assertTrue(root.has("basePath"));
		assertTrue(root.has("tags"));
		assertTrue(root.has("schemes"));
		assertTrue(root.has("consumes"));
		assertTrue(root.has("produces"));
		
		assertTrue(root.has("externalDocs"));
		
		final JsonNode hostNode = root.get("host");
		assertEquals("0.0.0.0:" + cyRESTPort , hostNode.asText());
		
		final JsonNode pathNode = root.get("basePath");
		assertEquals("/", pathNode.asText());
		
	}
}
