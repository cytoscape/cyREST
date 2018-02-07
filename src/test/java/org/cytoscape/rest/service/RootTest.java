package org.cytoscape.rest.service;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.resource.RootResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

public class RootTest extends JerseyTest {

	@Override
	protected Application configure() {
		return new ResourceConfig(RootResource.class);
	}

	@Test
	public void test() {
		final Map status = target("/").request().get(Map.class);
		assertEquals(status.size(), 1);
		assertNotNull(status.get("availableApiVersions"));
		List<String> vers = (List<String>) status.get("availableApiVersions");
		assertEquals(vers.size(), 1);
		assertEquals(vers.get(0), "v1");
	}
	
}
