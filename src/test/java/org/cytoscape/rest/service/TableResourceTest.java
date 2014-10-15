package org.cytoscape.rest.service;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.rest.internal.resource.TableResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TableResourceTest extends BasicResourceTest {

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	protected Application configure() {
		return new ResourceConfig(TableResource.class);
	}
	
	@Test
	public void testGetTable() throws Exception {
		final Long suid = network.getSUID();
		String result = target("/v1/networks/" + suid.toString() + "/tables/defaultnode").request().get(
				String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		JsonNode rows = root.get("rows");
		assertNotNull(rows);
		assertTrue(rows.isArray());
		assertEquals(CyNetwork.SUID, root.get("primaryKey").asText());
		assertEquals(true, root.get("public").asBoolean());
	}

	@Test
	public void testGetTableAsCSV() throws Exception {
		final Long suid = network.getSUID();
		String result = target("/v1/networks/" + suid.toString() + "/tables/defaultnode.csv").request().get(
				String.class);
		assertNotNull(result);
		final String[] rows = result.split("\n");
		assertFalse(rows.length == 0);
		final String header = rows[0];
		final String[] columnNamesArray = header.split(",");
		final Set<String> columnNames = new HashSet<>();
		
		for (String column : columnNamesArray) {
			columnNames.add(column);
		}
		assertTrue(columnNames.contains("SUID"));
		assertTrue(columnNames.contains("name"));
		assertTrue(columnNames.contains("shared name"));
		assertTrue(columnNames.contains("selected"));
	}

	@Test
	public void testGetColumnNames() throws Exception {
		final Long suid = network.getSUID();
		String result = target("/v1/networks/" + suid.toString() + "/tables/defaultnode/columns").request().get(
				String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		final Set<String> columnNames = new HashSet<>();
		
		for (JsonNode column : root) {
			columnNames.add(column.get("name").asText());
		}
		assertTrue(columnNames.contains("SUID"));
		assertTrue(columnNames.contains("name"));
		assertTrue(columnNames.contains("shared name"));
		assertTrue(columnNames.contains("selected"));
	}
	
	
	@Test
	public void testGetColumn() throws Exception {
		final Long suid = network.getSUID();
		String result = target("/v1/networks/" + suid.toString() + "/tables/defaultnode/columns/name").request().get(
				String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		assertTrue(root.isObject());
		assertTrue(root.get("values").isArray());
		assertEquals("name", root.get("name").asText());
	}


	@Test
	public void testGetRows() throws Exception {
		final Long suid = network.getSUID();
		String result = target("/v1/networks/" + suid.toString() + "/tables/defaultnode/rows").request().get(
				String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		assertTrue(root.isArray());
		assertFalse(root.size() == 0);
		
		JsonNode firstRow = root.get(0);
		assertTrue(firstRow.isObject());
	}
	
	
	@Test
	public void testGetRow() throws Exception {
		final Long suid = network.getSUID();
		final CyNode node = network.getNodeList().get(0);
		String result = target("/v1/networks/" + suid.toString() + "/tables/defaultnode/rows/" + node.getSUID()).request().get(
				String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		
		assertTrue(root.isObject());
	}


	@Test
	public void testGetCell() throws Exception {
		final Long suid = network.getSUID();
		final List<CyNode> nodes = network.getNodeList();
		CyNode node = null;
		
		// Find target node for testing
		for(CyNode n: nodes) {
			if(network.getRow(n).get("name", String.class).equals("n1")) {
				node = n;
				break;
			}
		}
		assertNotNull(node);
		
		String result = target("/v1/networks/" + suid.toString() + "/tables/defaultnode/rows/" + node.getSUID() + "/name").request().get(
				String.class);
		assertNotNull(result);
		assertEquals("n1", result);
	}
}