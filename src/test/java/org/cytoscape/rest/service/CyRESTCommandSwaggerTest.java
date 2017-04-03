package org.cytoscape.rest.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.resource.CyRESTCommandSwagger;
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

	@Test
	public void hasValidSwaggerConfig() throws Exception {
		Response response = target("/v1/commands/swagger.json").request().get();
		String result = response.readEntity(String.class);
		assertNotNull(result);
		
		//System.out.println("CyREST Command Swagger exists at /v1/commands/swagger.json");
		//System.out.println(result);
		
		final JsonNode root = mapper.readTree(result);
	
		swaggerConfigTest(root);
		
		JsonNode pathsNode = root.get("paths");
		assertTrue(pathsNode.has("/v1/commands/" + DUMMY_NAMESPACE + "/" + DUMMY_COMMAND));
	}
	
	@Test
	public void isDefinitionNullOnInit() {
		CyRESTCommandSwagger cyRESTCommandSwagger = new CyRESTCommandSwagger();
		assertTrue(cyRESTCommandSwagger.isSwaggerDefinitionNull());
	}
	
	@Test
	public void isIntJSONTypeCorrect() {
		QueryParameter parameter = mock(QueryParameter.class);
		CyRESTCommandSwagger.setTypeAndFormatFromClass(parameter, int.class);
		verify(parameter).setType("integer");
		verify(parameter).setFormat("int32");
	}
	
	@Test
	public void isLongJSONTypeCorrect() {
		QueryParameter parameter = mock(QueryParameter.class);
		CyRESTCommandSwagger.setTypeAndFormatFromClass(parameter, long.class);
		verify(parameter).setType("integer");
		verify(parameter).setFormat("int64");
	}
	
	@Test
	public void isFloatJSONTypeCorrect() {
		QueryParameter parameter = mock(QueryParameter.class);
		CyRESTCommandSwagger.setTypeAndFormatFromClass(parameter, float.class);
		verify(parameter).setType("number");
		verify(parameter).setFormat("int32");
	}
	
	@Test
	public void isDoubleJSONTypeCorrect() {
		QueryParameter parameter = mock(QueryParameter.class);
		CyRESTCommandSwagger.setTypeAndFormatFromClass(parameter, double.class);
		verify(parameter).setType("number");
		verify(parameter).setFormat("int64");
	}
	
	@Test
	public void isStringJSONTypeCorrect() {
		QueryParameter parameter = mock(QueryParameter.class);
		CyRESTCommandSwagger.setTypeAndFormatFromClass(parameter, String.class);
		verify(parameter).setType("string");
	}
	
	@Test
	public void isByteJSONTypeCorrect() {
		QueryParameter parameter = mock(QueryParameter.class);
		CyRESTCommandSwagger.setTypeAndFormatFromClass(parameter, byte.class);
		verify(parameter).setType("string");
		verify(parameter).setFormat("byte");
	}
	
	@Test
	public void isBooleanJSONTypeCorrect() {
		QueryParameter parameter = mock(QueryParameter.class);
		CyRESTCommandSwagger.setTypeAndFormatFromClass(parameter, boolean.class);
		verify(parameter).setType("boolean");

	}
}
