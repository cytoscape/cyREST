package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.StringReader;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.commands.resources.CommandResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommandResourceTest extends BasicResourceTest {

	@Override
	protected Application configure() {
		return new ResourceConfig(CommandResource.class);
	}

	private ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testCommand() throws Exception {
		
		Response response = target("/v1/commands/dummyNamespace/dummyCommand").queryParam("dummyArgument", "fleen").request().get();
		assertNotNull(response);
		
		final String body = response.readEntity(String.class);
		System.out.println(body);
		
		assertEquals(200, response.getStatus());
	}
	
	@Test 
	public void testMultiTaskCommand() throws Exception {
		
		assertFalse(multiTaskAComplete);
		assertFalse(multiTaskBComplete);
		assertFalse(multiTaskCComplete);
		Response response = target("/v1/commands/dummyNamespace/dummyMultiTaskCommand").request().get();
		assertNotNull(response);
		
		final String body = response.readEntity(String.class);
		System.out.println(body);
		
		assertEquals(200, response.getStatus());
		assertTrue(multiTaskAComplete);
		assertTrue(multiTaskBComplete);
		assertTrue(multiTaskCComplete);
		
		BufferedReader reader = new BufferedReader(new StringReader(body));
		assertTrue(reader.readLine().endsWith("Dummy string A</p>"));
		assertTrue(reader.readLine().endsWith("Dummy string B</p>"));
		assertTrue(reader.readLine().endsWith("Dummy string C</p>"));
		assertTrue(reader.readLine().endsWith("Finished</p>"));
	}
	
	@Test
	public void testUnparsableJsonTaskCommandJSON() throws Exception {
		
	
		Response response = target("/v1/commands/dummyNamespace/dummyUnparsableJsonCommand").request().post(null);
		assertNotNull(response);
		
		final String body = response.readEntity(String.class);
		System.out.println(body);
		
		assertEquals(200, response.getStatus());
	
		
		final JsonNode root = mapper.readTree(body);
		JsonNode dataNode = root.get("data");
		assertNotNull(dataNode);
		assertEquals(0, dataNode.size());
		
		JsonNode errorsNode = root.get("errors");
		assertEquals(1, errorsNode.size());
		JsonNode statusNode = errorsNode.get(0).get("status");
		assertEquals(500, statusNode.asInt());
		JsonNode typeNode = errorsNode.get(0).get("type");
		assertEquals("urn:cytoscape:ci:cyrest-core:v1:handle-json-command:errors:3", typeNode.asText());
	}
	
	@Test
	public void testMultiTaskCommandJSON() throws Exception {
		
		assertFalse(multiTaskAComplete);
		assertFalse(multiTaskBComplete);
		assertFalse(multiTaskCComplete);
		Response response = target("/v1/commands/dummyNamespace/dummyMultiTaskCommand").request().post(null);
		assertNotNull(response);
		
		final String body = response.readEntity(String.class);
		System.out.println(body);
		
		assertEquals(200, response.getStatus());
		assertTrue(multiTaskAComplete);
		assertTrue(multiTaskBComplete);
		assertTrue(multiTaskCComplete);
		
		final JsonNode root = mapper.readTree(body);
		JsonNode dataNode = root.get("data");
		assertNotNull(dataNode);
		assertEquals(2, dataNode.size());
		JsonNode dataA = dataNode.get(0);
		JsonNode dataB = dataNode.get(1);
		assertNotNull(dataA);
		assertNotNull(dataB);
		
		JsonNode dataValueA = dataA.get("dummyFieldA");
		assertEquals("dummyValueA", dataValueA.asText());
		JsonNode dataValueB = dataB.get("dummyFieldB");
		assertEquals("dummyValueB", dataValueB.asText());
		
		JsonNode errorsNode = root.get("errors");
		assertEquals(0, errorsNode.size());
	}
	
	@Test 
	public void testAppendTaskCommand() throws Exception {
		
		assertFalse(appendTaskAComplete);
		assertFalse(appendTaskBComplete);
		Response response = target("/v1/commands/dummyNamespace/dummyAppendTaskCommand").request().get();
		assertNotNull(response);
		
		final String body = response.readEntity(String.class);
		System.out.println(body);
		
		assertEquals(200, response.getStatus());
		assertTrue(appendTaskAComplete);
		assertTrue(appendTaskBComplete);
	}
}
