package org.cytoscape.rest.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.times;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.rest.internal.resource.CyRESTCommandSwagger;
import org.cytoscape.view.model.CyNetworkView;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.models.parameters.QueryParameter;

public class CyRESTCommandSwaggerTest extends SwaggerResourceTest
{
	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	protected Application configure() {
		return new ResourceConfig(CyRESTCommandSwagger.class);
	}

	/**
	 * Test to ensure that the base networks remain unaltered after fetching the command swagger.
	 */
	@Test
	public void maintainsBaseTestNetworks() {
		Set<CyNetwork> networks = networkManager.getNetworkSet();
		assertEquals(2, networks.size());
		when(cyApplicationManager.getCurrentNetwork()).thenReturn(network);
		when(cyApplicationManager.getCurrentNetworkView()).thenReturn(view);
		verify(viewManager, times(0)).addNetworkView(any(CyNetworkView.class), anyBoolean());
		verify(viewManager, times(0)).destroyNetworkView(any(CyNetworkView.class));
		target("/v1/commands/swagger.json").request().get();
		verify(viewManager, times(0)).addNetworkView(any(CyNetworkView.class), anyBoolean());
		verify(viewManager, times(0)).destroyNetworkView(any(CyNetworkView.class));
		networks = networkManager.getNetworkSet();
		assertEquals(2, networks.size());
	}
	
	/**
	 * Test to ensure that when no networks or views are present, the command swagger can be fetched.
	 */
	@Test
	public void maintainsZeroNetworks() {
		
		Set<CyNetwork> networks = networkManager.getNetworkSet();
		for (CyNetwork network : networks) { networkManager.destroyNetwork(network); }
		networks = networkManager.getNetworkSet();
		assertEquals(0, networks.size());
		verify(viewManager, times(0)).addNetworkView(any(CyNetworkView.class), anyBoolean());
		verify(viewManager, times(0)).destroyNetworkView(any(CyNetworkView.class));
		when(cyApplicationManager.getCurrentNetwork()).thenReturn(null);
		when(cyApplicationManager.getCurrentNetworkView()).thenReturn(null);
		target("/v1/commands/swagger.json").request().get();
	
		verify(viewManager, times(1)).addNetworkView(any(CyNetworkView.class), anyBoolean());
		verify(viewManager, times(1)).destroyNetworkView(any(CyNetworkView.class));
	
		networks = networkManager.getNetworkSet();
		assertEquals(0, networks.size());
	
	}
	
	/**
	 * Test to ensure that when a network or networks with no current or view are present, the command swagger can be fetched.
	 */
	@Test
	public void maintainsOneNetworkZeroViews() {
		
		Set<CyNetwork> networks = networkManager.getNetworkSet();
	
		assertEquals(2, networks.size());
		verify(viewManager, times(0)).addNetworkView(any(CyNetworkView.class), anyBoolean());
		verify(viewManager, times(0)).destroyNetworkView(any(CyNetworkView.class));
		when(cyApplicationManager.getCurrentNetwork()).thenReturn(network);
		when(cyApplicationManager.getCurrentNetworkView()).thenReturn(null);
		target("/v1/commands/swagger.json").request().get();
	
		verify(viewManager, times(1)).addNetworkView(any(CyNetworkView.class), anyBoolean());
		verify(viewManager, times(1)).destroyNetworkView(any(CyNetworkView.class));
	
		networks = networkManager.getNetworkSet();
		assertEquals(2, networks.size());
	
		networks = networkManager.getNetworkSet();
	}
	
	@Test
	public void hasValidSwaggerConfig() throws Exception {
		
		Response response = target("/v1/commands/swagger.json").request().get();
		String result = response.readEntity(String.class);
		assertNotNull(result);
		
		//System.out.println("CyREST Command Swagger exists at /v1/commands/swagger.json");
		System.out.println(result);
		
		final JsonNode root = mapper.readTree(result);
	
		swaggerConfigTest(root);
		
		JsonNode pathsNode = root.get("paths");
		assertTrue(pathsNode.has("/v1/commands/" + DUMMY_NAMESPACE + "/" + DUMMY_COMMAND));
		assertTrue(pathsNode.has("/v1/commands/" + DUMMY_NAMESPACE + "/" + DUMMY_MULTI_TASK_COMMAND));
		assertTrue(pathsNode.has("/v1/commands/" + DUMMY_NAMESPACE + "/" + DUMMY_APPEND_TASK_COMMAND));
	
	
	}
	
	@Test
	public void isDefinitionNullOnInit() {
		CyRESTCommandSwagger cyRESTCommandSwagger = new CyRESTCommandSwagger();
		assertTrue(cyRESTCommandSwagger.isSwaggerDefinitionNull());
	}
	
	@Test
	public void isIntJSONTypeCorrect() {
		QueryParameter parameter = mock(QueryParameter.class);
		CyRESTCommandSwagger.setParameterTypeAndFormatFromClass(parameter, int.class);
		verify(parameter).setType("integer");
		verify(parameter).setFormat("int32");
	}
	
	@Test
	public void isLongJSONTypeCorrect() {
		QueryParameter parameter = mock(QueryParameter.class);
		CyRESTCommandSwagger.setParameterTypeAndFormatFromClass(parameter, long.class);
		verify(parameter).setType("integer");
		verify(parameter).setFormat("int64");
	}
	
	@Test
	public void isFloatJSONTypeCorrect() {
		QueryParameter parameter = mock(QueryParameter.class);
		CyRESTCommandSwagger.setParameterTypeAndFormatFromClass(parameter, float.class);
		verify(parameter).setType("number");
		verify(parameter).setFormat("int32");
	}
	
	@Test
	public void isDoubleJSONTypeCorrect() {
		QueryParameter parameter = mock(QueryParameter.class);
		CyRESTCommandSwagger.setParameterTypeAndFormatFromClass(parameter, double.class);
		verify(parameter).setType("number");
		verify(parameter).setFormat("int64");
	}
	
	@Test
	public void isStringJSONTypeCorrect() {
		QueryParameter parameter = mock(QueryParameter.class);
		CyRESTCommandSwagger.setParameterTypeAndFormatFromClass(parameter, String.class);
		verify(parameter).setType("string");
	}
	
	@Test
	public void isByteJSONTypeCorrect() {
		QueryParameter parameter = mock(QueryParameter.class);
		CyRESTCommandSwagger.setParameterTypeAndFormatFromClass(parameter, byte.class);
		verify(parameter).setType("string");
		verify(parameter).setFormat("byte");
	}
	
	@Test
	public void isBooleanJSONTypeCorrect() {
		QueryParameter parameter = mock(QueryParameter.class);
		CyRESTCommandSwagger.setParameterTypeAndFormatFromClass(parameter, boolean.class);
		verify(parameter).setType("boolean");

	}
}
