package org.cytoscape.rest.service;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.resource.MiscResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

public class MiscResourceTest extends JerseyTest {

	@Override
	protected Application configure() {
		return new ResourceConfig(MiscResource.class);
	}

	
	@Test
	public void gcTest() {
		final Response result = target("/v1/gc").request().get();
		assertEquals(204, result.getStatus());
	}
	
	@Test 
	public void statusTest() {
		final Response result = target("/v1/").request().get();
		assertEquals(200, result.getStatus());
	}
}
