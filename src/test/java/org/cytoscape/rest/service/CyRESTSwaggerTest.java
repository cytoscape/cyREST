package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.resource.CyRESTSwagger;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

public class CyRESTSwaggerTest extends SwaggerResourceTest 
{
	private ObjectMapper mapper = new ObjectMapper();
	
	@Api
	@Path("/dummy")
	public class DummySwaggerResource{
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		@ApiOperation(value="", notes="")
		public String greeting(){
			return null;
		}
	}
	
	@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.COMMANDS_TAG})
	@Path("/dummyCommand")
	public class DummyCommandSwaggerResource{
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		@ApiOperation(value="", notes="")
		public String greeting(){
			return null;
		}
	}
	
	@Override
	protected Application configure() 
	{
		return new ResourceConfig(CyRESTSwagger.class);
	}

	@Test
	public void hasValidSwaggerConfig() throws JsonProcessingException, IOException {
		Response response = target("/v1/swagger.json").request().get();
		String result = response.readEntity(String.class);
		assertNotNull(result);
		
		final JsonNode root = mapper.readTree(result);
		swaggerConfigTest(root);
		
		assertFalse(root.has("paths"));
	}
	
	
	@Test
	public void addsCommandLink() throws JsonProcessingException, IOException {
		this.cyRESTSwagger.addResource(DummyCommandSwaggerResource.class);
		
		Response response = target("/v1/swagger.json").request().get();
		String result = response.readEntity(String.class);
		
		System.out.println(result);
		
		assertNotNull(result);
		
		final JsonNode root = mapper.readTree(result);
		swaggerConfigTest(root);
		
		assertTrue(root.has("paths"));
		
		JsonNode pathsNode = root.get("paths");
		
		assertTrue(pathsNode.has("/dummyCommand"));
		
		JsonNode description = pathsNode.get("/dummyCommand").get("get").get("description");
		assertEquals("\n\nFor a detailed list of all commands see the [CyREST Command API](http://localhost:1234/v1/swaggerUI/swagger-ui/index.html?url=http%3A%2F%2Flocalhost%3A1234%2Fv1%2Fcommands%2Fswagger.json)", description.asText());
	}
	@Test
	public void updatesAfterResourceAdd() throws JsonProcessingException, IOException {
		this.cyRESTSwagger.addResource(DummySwaggerResource.class);
		
		Response response = target("/v1/swagger.json").request().get();
		String result = response.readEntity(String.class);
		assertNotNull(result);
		
		final JsonNode root = mapper.readTree(result);
		swaggerConfigTest(root);
		
		assertTrue(root.has("paths"));
		
		JsonNode pathsNode = root.get("paths");
		
		//System.out.println(result);
		
		int resourceCount = 0;
		for (Iterator<JsonNode> rootIterator = pathsNode.elements(); rootIterator.hasNext(); )
		{
			resourceCount++;
			rootIterator.next();
		}
		assertEquals(1, resourceCount);
		
		assertTrue(pathsNode.has("/dummy"));
	}
	
	@Test 
	public void updatesAfterResourceRemove() throws JsonProcessingException, IOException
	{
		updatesAfterResourceAdd();
		this.cyRESTSwagger.removeResource(DummySwaggerResource.class);
		Response response = target("/v1/swagger.json").request().get();
		String result = response.readEntity(String.class);
		final JsonNode root = mapper.readTree(result);
		assertFalse(root.has("paths"));
	}
	
	@Test
	public void isDefinitionNullOnInit()
	{
		CyRESTSwagger cyRESTSwagger = new CyRESTSwagger();
		assertTrue(cyRESTSwagger.isSwaggerDefinitionNull());
	}
	
}
