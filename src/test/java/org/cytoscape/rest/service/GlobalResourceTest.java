package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Application;

import org.cytoscape.rest.internal.resource.GlobalTableResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GlobalResourceTest extends BasicResourceTest {

	
	private ObjectMapper mapper = new ObjectMapper();

	
	@Override
	protected Application configure() {
		return new ResourceConfig(GlobalTableResource.class);
	}
	
	@Test
	public void testGetTableCount() throws Exception {
		
		String result = target("/v1/tables/count").request().get(
				String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		JsonNode count = root.get("count");
		assertNotNull(count);
		assertEquals(0, count.asInt());
	}
}