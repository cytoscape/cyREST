package org.cytoscape.rest.service;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.resource.StyleResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.w3c.dom.ls.LSException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StyleResourceTest extends BasicResourceTest {

	@Override
	protected Application configure() {
		return new ResourceConfig(StyleResource.class);
	}

	private ObjectMapper mapper = new ObjectMapper();

	
	@Test
	public void testGetStyles() throws Exception {
		String result = target("/v1/styles").request().get(String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		assertTrue(root.isArray());
		assertEquals(2, root.size());
		Set<String> names = new HashSet<>();
		for(JsonNode styleName: root) {
			names.add(styleName.asText());
		}
		assertTrue(names.contains("vs1"));
		assertTrue(names.contains("mock1"));
		assertFalse(names.contains("vs2"));
	}


	@Test
	public void testGetStylesCount() throws Exception {
		String result = target("/v1/styles/count").request().get(String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		int count = root.get("count").asInt();
		assertEquals(2, count);
	}
	
	
	@Test
	public void testGetStyle() throws Exception {
		String result = target("/v1/styles/mock1").request().get(String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		assertEquals("mock1", root.get("title").asText());
	}
	
	
	@Test
	public void testGetStyleDefaults() throws Exception {
		final Response result = target("/v1/styles/vs1/defaults").request().get();
		assertNotNull(result);
		final String body = result.readEntity(String.class);
		System.out.println(body);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.get("defaults").isArray());
	}


	@Test
	public void testGetMappings() throws Exception {
		final Response result = target("/v1/styles/vs1/mappings").request().get();
		assertNotNull(result);
		System.out.println("res: " + result.toString());
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		String body = result.readEntity(String.class);
		System.out.println(body);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.isArray());
		assertEquals(11, root.size());
		
		for(JsonNode mapping:root) {
			JsonNode type = mapping.get("mappingType");
			assertNotNull(type);
			if(type.asText().equals("discrete")) {
				testDiscrete(mapping);
			} else if(type.asText().equals("continuous")) {
				testContinuous(mapping);
			} else if(type.asText().equals("passthrough")) {
				testPassthrough(mapping);
			} else {
				fail("No such mapping type: " + type.asText());
			}
		}
	}
	
	private void testDiscrete(JsonNode mapping) {
		checkBasicSettings(mapping);
		final JsonNode map = mapping.get("map");
		assertNotNull(map);
		assertTrue(map.isArray());
	}
	
	private void testContinuous(JsonNode mapping) {
		checkBasicSettings(mapping);
		final JsonNode points = mapping.get("points");
		assertNotNull(points);
		assertTrue(points.isArray());
		
		for(JsonNode point:points) {
			JsonNode val = point.get("value");
			assertNotNull(val);
			assertTrue(val.isNumber());
			JsonNode l = point.get("lesser");
			assertNotNull(l);
			JsonNode eq = point.get("equal");
			assertNotNull(eq);
			JsonNode g = point.get("greater");
			assertNotNull(g);
		}
	}
	
	private void testPassthrough(JsonNode mapping) {
		checkBasicSettings(mapping);
	}
	
	private final void checkBasicSettings(JsonNode mapping) {
		final JsonNode column = mapping.get("mappingColumn");
		assertNotNull(column);
		final JsonNode columnType= mapping.get("mappingColumnType");
		assertNotNull(columnType);
		final JsonNode vp = mapping.get("visualProperty");
		assertNotNull(vp);
	}
}
