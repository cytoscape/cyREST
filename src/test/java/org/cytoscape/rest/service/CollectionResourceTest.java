package org.cytoscape.rest.service;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.rest.internal.resource.CollectionResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jfree.chart.title.Title;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

public class CollectionResourceTest extends BasicResourceTest {

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	protected Application configure() {
		return new ResourceConfig(CollectionResource.class);
	}

	@Test
	public void testGetCollectionCount() throws Exception {
		Response result = target("/v1/collections/count").request().get();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		final String body = result.readEntity(String.class);
		final JsonNode root = mapper.readTree(body);
		Long count = root.get("count").asLong();
		assertTrue(count == 2);
	}

	@Test
	public void testGetCollections() throws Exception {
		Response result = target("/v1/collections").request().get();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		final JsonNode root = mapper.readTree(result.readEntity(String.class));
		final JsonNodeType dataType = root.getNodeType();
		assertEquals(JsonNodeType.ARRAY, dataType);
		
		assertEquals(2, root.size());
		final Set<Long> collections = new HashSet<>();
		for(final JsonNode node: root) {
			collections.add(node.asLong());
		}
		
		assertTrue( collections.contains(((CySubNetwork)this.network).getRootNetwork().getSUID()));
	}

	@Test
	public void testGetSubnetworks() throws Exception {
		final Long rootSUID = ((CySubNetwork)this.network).getRootNetwork().getSUID();
		Response result = target("/v1/collections/" + rootSUID.toString() + "/subnetworks").request().get();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		final JsonNode root = mapper.readTree(result.readEntity(String.class));
		final JsonNodeType dataType = root.getNodeType();
		assertEquals(JsonNodeType.ARRAY, dataType);
		assertEquals(1, root.size());
	}
	
	@Test
	public void testGetTables() throws Exception {
		final Long rootSUID = ((CySubNetwork)this.network).getRootNetwork().getSUID();
		Response result = target("/v1/collections/" + rootSUID.toString() + "/tables").request().get();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		final JsonNode root = mapper.readTree(result.readEntity(String.class));
		final JsonNodeType dataType = root.getNodeType();
		assertEquals(JsonNodeType.ARRAY, dataType);
		assertEquals(2, root.size());
			
		final JsonNode tbl1 = root.get(0);
		final JsonNode tbl2 = root.get(1);
		
		JsonNode title1 = tbl1.get("title");
		JsonNode title2 = tbl2.get("title");
		
		assertNotNull(title1);
		assertNotNull(title2);
		
		JsonNode defTable;
		JsonNode sharedTable;
		
		if(title1.asText().contains("default")) {
			defTable = tbl1;
			sharedTable = tbl2;
		} else {
			defTable = tbl2;
			sharedTable = tbl1;
		}
		
		assertTrue(defTable.get("title").asText().contains("default"));
		assertTrue(sharedTable.get("title").asText().contains("shared"));
		
		final JsonNode defRows = defTable.get("rows");
		final JsonNode sharedRows = sharedTable.get("rows");
		
		System.out.println(sharedTable);
		
		assertEquals(JsonNodeType.ARRAY, defRows.getNodeType());
		assertEquals(JsonNodeType.ARRAY, sharedRows.getNodeType());
	}
	
	@Test
	public void testGetTable() throws Exception {
		final Long rootSUID = ((CySubNetwork)this.network).getRootNetwork().getSUID();
		Response resultDefault = target("/v1/collections/" + rootSUID.toString() + "/tables/default").request().get();
		Response resultShared = target("/v1/collections/" + rootSUID.toString() + "/tables/shared").request().get();
		assertNotNull(resultDefault);
		assertNotNull(resultShared);
		
		assertEquals(200, resultDefault.getStatus());
		assertEquals(200, resultShared.getStatus());
		
		final JsonNode rootDefault = mapper.readTree(resultDefault.readEntity(String.class));
		final JsonNode rootShared = mapper.readTree(resultShared.readEntity(String.class));
		
		final JsonNodeType dataTypeDefault = rootDefault.getNodeType();
		final JsonNodeType dataTypeShared = rootShared.getNodeType();
		assertEquals(JsonNodeType.OBJECT, dataTypeDefault);
		assertEquals(JsonNodeType.OBJECT, dataTypeShared);
		
		JsonNode title1 = rootDefault.get("title");
		JsonNode title2 = rootShared.get("title");
		
		assertNotNull(title1);
		assertNotNull(title2);
		
		assertTrue(title1.asText().contains("default"));
		assertTrue(title2.asText().contains("shared"));
	}
}
