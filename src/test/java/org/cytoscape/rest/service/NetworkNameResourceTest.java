package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.Application;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.rest.internal.resource.NetworkResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NetworkNameResourceTest extends BasicResourceTest {

	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	protected Application configure() {
		return new ResourceConfig(NetworkResource.class);
	}

	@Test
	public void testGetNetworks() throws Exception {
		String result = target("/v1/networks.names").request().get(String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		assertTrue(root.isArray());
		final JsonNode firstEntry = root.get(0);
		assertTrue(firstEntry.isObject());
		assertEquals(network.getRow(network).get(CyNetwork.NAME, String.class), firstEntry.get("name").asText());
		assertEquals(network.getSUID(), (Long)firstEntry.get("SUID").asLong());
	}

}
