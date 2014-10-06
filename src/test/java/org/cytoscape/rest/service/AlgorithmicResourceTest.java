package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.resource.AlgorithmicResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

public class AlgorithmicResourceTest extends BasicResourceTest {

	@Override
	protected Application configure() {
		return new ResourceConfig(AlgorithmicResource.class);
	}

	@Test
	public void testLayout() {
		final Collection layouts = target("/v1/apply/layouts").request().get(
				Collection.class);
		assertNotNull(layouts);
		assertEquals(layouts.size(), 1);
	}
	
	@Test
	public void testStyles() {
		final Collection styles = target("/v1/apply/styles").request().get(
				Collection.class);
		assertNotNull(styles);
		assertEquals(styles.size(), 0);
	}
	
	@Test
	public void testApplyLayout() {
		Long suid = this.network.getSUID();
		Response result = target("/v1/apply/layouts/grid/" + suid).request().get(
				Response.class);
		assertNotNull(result);
		System.out.println("##################" + result.getStatus());
	}
}