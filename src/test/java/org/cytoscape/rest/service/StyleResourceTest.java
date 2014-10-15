package org.cytoscape.rest.service;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.cytoscape.rest.internal.resource.StyleResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

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
		String result = target("/v1/styles/vs1/defaults").request().get(String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		System.out.println(result);
		assertTrue(root.get("defaults").isArray());
	}
}
