package org.cytoscape.rest.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.rest.internal.resource.TableResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Ignore;
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
	public void testGetAllTables() throws Exception {
		final Long suid = network.getSUID();
		String result = target("/v1/networks/" + suid.toString() + "/tables").request().get(
				String.class);
		System.out.println(result);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
	
		
		assertTrue(root.isArray());
		assertEquals(0, root.size());
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
		assertTrue(columnNames.contains("local1"));
	}

	@Test
	public void testGetColumnNames() throws Exception {
		final Long suid = network.getSUID();
		String result = target("/v1/networks/" + suid.toString() + 
				"/tables/defaultnode/columns")
				.request().get(String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		final Set<String> columnNames = new HashSet<>();
		
		for (JsonNode column : root) {
			columnNames.add(column.get("name").asText());
		}
		assertEquals(5, columnNames.size());
		assertTrue(columnNames.contains("SUID"));
		assertTrue(columnNames.contains("name"));
		assertTrue(columnNames.contains("shared name"));
		assertTrue(columnNames.contains("selected"));
		assertTrue(columnNames.contains("local1"));
		
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
		assertEquals(Integer.valueOf(4), Integer.valueOf(root.get("values").size()));
		
		String result_suid = target("/v1/networks/" + suid.toString() + "/tables/defaultnetwork/columns/SUID").request().get(
				String.class);
		assertNotNull(result_suid);
		final JsonNode root2 = mapper.readTree(result_suid);
		assertTrue(root2.isObject());
		assertTrue(root2.get("values").isArray());
		assertEquals("SUID", root2.get("name").asText());
		assertEquals(Integer.valueOf(1), Integer.valueOf(root2.get("values").size()));
		
		String result_edge = target("/v1/networks/" + suid.toString() + "/tables/defaultedge/columns/interaction").request().get(
				String.class);
		assertNotNull(result_edge);
		final JsonNode root3 = mapper.readTree(result_edge);
		assertTrue(root3.isObject());
		assertTrue(root3.get("values").isArray());
		assertEquals("interaction", root3.get("name").asText());
		assertEquals(Integer.valueOf(3), Integer.valueOf(root3.get("values").size()));
		
		String result_local1 = target("/v1/networks/" + suid.toString() + "/tables/defaultnode/columns/local1").request().get(
				String.class);
		assertNotNull(result_local1);
		final JsonNode root_local1 = mapper.readTree(result_local1);
		assertTrue(root_local1.isObject());
		assertTrue(root_local1.get("values").isArray());
		assertEquals("local1", root_local1.get("name").asText());
		JsonNode valueNode = root_local1.get("values");
		assertEquals(Integer.valueOf(4), Integer.valueOf(valueNode.size()));
	    ArrayList<Double> arrayList = new ArrayList<Double>(4); 
	    for (int i = 0; i < valueNode.size();i++) { 
	      if (valueNode.get(i).isNull()) { 
	       
	        arrayList.add(i, null); 
	      } else { 
	        arrayList.add(i, valueNode.get(i).asDouble()); 
	      } 
	    } 
	     
	    assertTrue(arrayList.contains(1d)); 
	    assertTrue(arrayList.contains(null)); 
	    assertTrue(arrayList.contains(3d));  
	    assertTrue(arrayList.contains(4d)); 
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
		String result = target("/v1/networks/" + suid.toString() + 
				"/tables/defaultnode/rows/" + node.getSUID())
				.request().get(String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		assertTrue(root.isObject());
		JsonNode localVal = root.get("local1");
		assertNotNull(localVal);
		assertNotNull(localVal.asDouble());
		assertEquals(network.getRow(node).get("local1", Double.class), 
				(Double)localVal.asDouble());
		System.out.println(localVal.asDouble());
		
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
	
	
	@Test
	public void testUpdateColumnValues() throws Exception {
		
		// Pick SUID of some nodes
		CyNode node1 = network.getNodeList().get(0);
		CyNode node2= network.getNodeList().get(1);
		CyNode node3 = network.getNodeList().get(2);
		
		final String newValues = "["
				+ "{\"SUID\":" + node1.getSUID() + ", \"value\": \"true\" },"
				+ "{\"SUID\":" + node2.getSUID() + ", \"value\": \"false\" },"
				+ "{\"SUID\":" + node3.getSUID() + ", \"value\": \"true\" }"
				+ "]";
		
		Entity<String> entity = Entity.entity(newValues, MediaType.APPLICATION_JSON_TYPE);
		final Long suid = network.getSUID();
		
		Response result = target("/v1/networks/" + suid.toString() + "/tables/defaultnode/columns/selected")
				.request().put(entity);
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		System.out.println("res: " + result.toString());
		
		assertTrue(network.getRow(node1).get(CyNetwork.SELECTED, Boolean.class));
		assertFalse(network.getRow(node2).get(CyNetwork.SELECTED, Boolean.class));
		assertTrue(network.getRow(node3).get(CyNetwork.SELECTED, Boolean.class));
		
		CyEdge edge1 = network.getEdgeList().get(0);
		CyEdge edge2= network.getEdgeList().get(1);
		CyEdge edge3 = network.getEdgeList().get(2);
		
		final String edgeNewValues = "["
				+ "{\"SUID\":" + edge1.getSUID() + ", \"value\": \"false\" },"
				+ "{\"SUID\":" + edge2.getSUID() + ", \"value\": \"false\" },"
				+ "{\"SUID\":" + edge3.getSUID() + ", \"value\": \"true\" }"
				+ "]";
		
		entity = Entity.entity(edgeNewValues, MediaType.APPLICATION_JSON_TYPE);
		
		Response edgeResult = target("/v1/networks/" + suid.toString() + "/tables/defaultedge/columns/selected")
				.request().put(entity);
		assertNotNull(edgeResult);
		assertFalse(edgeResult.getStatus() == 500);
		assertEquals(200, edgeResult.getStatus());
		System.out.println("res: " + edgeResult.toString());
		
		assertFalse(network.getRow(edge1).get(CyNetwork.SELECTED, Boolean.class));
		assertFalse(network.getRow(edge2).get(CyNetwork.SELECTED, Boolean.class));
		assertTrue(network.getRow(edge3).get(CyNetwork.SELECTED, Boolean.class));
	}

	@Test
	public void testUpdateColumnIntegerListValues() throws Exception {
		
		network.getDefaultNodeTable().createListColumn("integerListColumn", Integer.class, false);
		
		// Pick SUID of some nodes
		CyNode node1 = network.getNodeList().get(0);
		CyNode node2= network.getNodeList().get(1);
		CyNode node3 = network.getNodeList().get(2);
		
		final String newValues = "["
				+ "{\"SUID\":" + node1.getSUID() + ", \"value\": [1,2] },"
				+ "{\"SUID\":" + node2.getSUID() + ", \"value\": [3,4] },"
				+ "{\"SUID\":" + node3.getSUID() + ", \"value\": [5.6] }"
				+ "]";
		
		Entity<String> entity = Entity.entity(newValues, MediaType.APPLICATION_JSON_TYPE);
		final Long suid = network.getSUID();
		
		Response result = target("/v1/networks/" + suid.toString() + "/tables/defaultnode/columns/integerListColumn")
				.request().put(entity);
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		System.out.println("res: " + result.toString());
		List<Integer> listA = network.getDefaultNodeTable().getRow(node1.getSUID()).getList("integerListColumn", Integer.class);
				assertEquals(2, listA.size());
		assertTrue(listA.contains(1));
		assertTrue(listA.contains(2));
	}

	@Test
	public void testUpdateColumnDoubleListValues() throws Exception {
		assertNull(network.getDefaultNodeTable().getColumn("integerListColumn"));
		assertNull(network.getDefaultNodeTable().getColumn("doubleListColumn"));
		assertNull(network.getDefaultNodeTable().getColumn("stringListColumn"));
		assertNull(network.getDefaultNodeTable().getColumn("longListColumn"));
		assertNull(network.getDefaultNodeTable().getColumn("booleanListColumn"));
		network.getDefaultNodeTable().createListColumn("doubleListColumn", Double.class, false);
		
		// Pick SUID of some nodes
		CyNode node1 = network.getNodeList().get(0);
		CyNode node2= network.getNodeList().get(1);
		CyNode node3 = network.getNodeList().get(2);
		
		final String newValues = "["
				+ "{\"SUID\":" + node1.getSUID() + ", \"value\": [0.1,0.2] },"
				+ "{\"SUID\":" + node2.getSUID() + ", \"value\": [0.3,0.4] },"
				+ "{\"SUID\":" + node3.getSUID() + ", \"value\": [0.5,0.6] }"
				+ "]";
		
		Entity<String> entity = Entity.entity(newValues, MediaType.APPLICATION_JSON_TYPE);
		final Long suid = network.getSUID();
		
		Response result = target("/v1/networks/" + suid.toString() + "/tables/defaultnode/columns/doubleListColumn")
				.request().put(entity);
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		System.out.println("res: " + result.toString());
		List<Double> listA = network.getDefaultNodeTable().getRow(node1.getSUID()).getList("doubleListColumn", Double.class);
		assertEquals(2, listA.size());
		assertTrue(listA.contains(.1));
		assertTrue(listA.contains(.2));
	}
	
	@Test
	public void testUpdateColumnStringListValues() throws Exception {
		assertNull(network.getDefaultNodeTable().getColumn("integerListColumn"));
		assertNull(network.getDefaultNodeTable().getColumn("doubleListColumn"));
		assertNull(network.getDefaultNodeTable().getColumn("stringListColumn"));
		assertNull(network.getDefaultNodeTable().getColumn("longListColumn"));
		assertNull(network.getDefaultNodeTable().getColumn("booleanListColumn"));
		network.getDefaultNodeTable().createListColumn("stringListColumn", String.class, false);
		
		// Pick SUID of some nodes
		CyNode node1 = network.getNodeList().get(0);
		CyNode node2= network.getNodeList().get(1);
		CyNode node3 = network.getNodeList().get(2);
		
		final String newValues = "["
				+ "{\"SUID\":" + node1.getSUID() + ", \"value\": [\"A\",\"B\"] },"
				+ "{\"SUID\":" + node2.getSUID() + ", \"value\": [\"C\",\"D\"] },"
				+ "{\"SUID\":" + node3.getSUID() + ", \"value\": [\"E\",\"F\"] }"
				+ "]";
		
		Entity<String> entity = Entity.entity(newValues, MediaType.APPLICATION_JSON_TYPE);
		final Long suid = network.getSUID();
		
		Response result = target("/v1/networks/" + suid.toString() + "/tables/defaultnode/columns/stringListColumn")
				.request().put(entity);
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		System.out.println("res: " + result.toString());
		List<String> listA = network.getDefaultNodeTable().getRow(node1.getSUID()).getList("stringListColumn", String.class);
		assertEquals(2, listA.size());
		assertTrue(listA.contains("A"));
		assertTrue(listA.contains("B"));
	}
	
	@Test
	public void testUpdateColumnLongListValues() throws Exception {
		assertNull(network.getDefaultNodeTable().getColumn("integerListColumn"));
		assertNull(network.getDefaultNodeTable().getColumn("doubleListColumn"));
		assertNull(network.getDefaultNodeTable().getColumn("stringListColumn"));
		assertNull(network.getDefaultNodeTable().getColumn("longListColumn"));
		assertNull(network.getDefaultNodeTable().getColumn("booleanListColumn"));
		network.getDefaultNodeTable().createListColumn("longListColumn", Long.class, false);
		
		// Pick SUID of some nodes
		CyNode node1 = network.getNodeList().get(0);
		CyNode node2= network.getNodeList().get(1);
		CyNode node3 = network.getNodeList().get(2);
		
		final String newValues = "["
				+ "{\"SUID\":" + node1.getSUID() + ", \"value\": [1,2] },"
				+ "{\"SUID\":" + node2.getSUID() + ", \"value\": [3,4] },"
				+ "{\"SUID\":" + node3.getSUID() + ", \"value\": [5,6] }"
				+ "]";
		
		Entity<String> entity = Entity.entity(newValues, MediaType.APPLICATION_JSON_TYPE);
		final Long suid = network.getSUID();
		
		Response result = target("/v1/networks/" + suid.toString() + "/tables/defaultnode/columns/longListColumn")
				.request().put(entity);
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		System.out.println("res: " + result.toString());
		List<Long> listA = network.getDefaultNodeTable().getRow(node1.getSUID()).getList("longListColumn", Long.class);
		assertEquals(2, listA.size());
		assertTrue(listA.contains(1l));
		assertTrue(listA.contains(2l));
	}
	
	@Test
	public void testUpdateColumnBooleanListValues() throws Exception {
		assertNull(network.getDefaultNodeTable().getColumn("integerListColumn"));
		assertNull(network.getDefaultNodeTable().getColumn("doubleListColumn"));
		assertNull(network.getDefaultNodeTable().getColumn("stringListColumn"));
		assertNull(network.getDefaultNodeTable().getColumn("longListColumn"));
		assertNull(network.getDefaultNodeTable().getColumn("booleanListColumn"));
		network.getDefaultNodeTable().createListColumn("booleanListColumn", Boolean.class, false);
		
		// Pick SUID of some nodes
		CyNode node1 = network.getNodeList().get(0);
		CyNode node2= network.getNodeList().get(1);
		CyNode node3 = network.getNodeList().get(2);
		
		final String newValues = "["
				+ "{\"SUID\":" + node1.getSUID() + ", \"value\": [false, false] },"
				+ "{\"SUID\":" + node2.getSUID() + ", \"value\": [false, true] },"
				+ "{\"SUID\":" + node3.getSUID() + ", \"value\": [true, true] }"
				+ "]";
		
		Entity<String> entity = Entity.entity(newValues, MediaType.APPLICATION_JSON_TYPE);
		final Long suid = network.getSUID();
		
		Response result = target("/v1/networks/" + suid.toString() + "/tables/defaultnode/columns/booleanListColumn")
				.request().put(entity);
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		System.out.println("res: " + result.toString());
		List<Boolean> listA = network.getDefaultNodeTable().getRow(node1.getSUID()).getList("booleanListColumn", Boolean.class);
		assertEquals(2, listA.size());
		assertTrue(listA.contains(false));
		assertTrue(listA.contains(false));
	}
	
	@Test
	public void testUpdateTable() throws Exception {
		
		// Pick SUID of some nodes
		CyNode node1 = network.getNodeList().get(0);
		
		String newData = "{" +
			 "\"key\":\"SUID\"," +
			 "\"dataKey\": \"id\"," +
			 "\"data\": [{" +
			 "\"id\": " + node1.getSUID() + "," +
			 "\"gene_name\": \"brca1\"," +
			 "\"local1\": 4.5" +
			 "}]}";
		
		Entity<String> entity = Entity.entity(newData, MediaType.APPLICATION_JSON_TYPE);
		final Long suid = network.getSUID();
		
		Response result = target(
				"/v1/networks/" + suid.toString() + "/tables/defaultnode")
				.queryParam("class", "local")
				.request().put(entity);
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		
		assertEquals("brca1", 
				network.getRow(node1).get("gene_name", String.class));
		
		assertEquals((Double)4.5, 
				network.getRow(node1).get("local1", Double.class));
		
		final CyTable localTable = network
				.getTable(CyNode.class, CyNetwork.LOCAL_ATTRS);
		Collection<CyColumn> cols = localTable.getColumns();
		System.out.println("Columns: " + cols);
		assertEquals(7, cols.size());
		
	}

	@Test
	public void testUpdateTableList() throws Exception {
		
		// Pick SUID of some nodes
		CyNode node1 = network.getNodeList().get(0);
		
		String newData = "{" +
			 "\"key\":\"SUID\"," +
			 "\"dataKey\": \"id\"," +
			 "\"data\": [{" +
			 "\"id\": " + node1.getSUID() + "," +
			 "\"gene_name\": \"brca1\"," +
			 "\"new\": [ 4.5 ]" +
			 "}]}";
		
		Entity<String> entity = Entity.entity(newData, MediaType.APPLICATION_JSON_TYPE);
		final Long suid = network.getSUID();
		
		Response result = target(
				"/v1/networks/" + suid.toString() + "/tables/defaultnode")
				.queryParam("class", "local")
				.request().put(entity);
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		
		assertEquals("brca1", 
				network.getRow(node1).get("gene_name", String.class));
		
		final List<Double> newValue = network.getRow(node1).getList("new", Double.class);
		
		assertEquals(1, 
				newValue.size());
		assertEquals((Double)4.5, newValue.get(0));
		
		final CyTable localTable = network
				.getTable(CyNode.class, CyNetwork.LOCAL_ATTRS);
		Collection<CyColumn> cols = localTable.getColumns();
		System.out.println("Columns: " + cols);
		assertEquals(8, cols.size());
		
	}
	
	@Test
	public void testFailUpdateBadTableList() throws Exception {
		
		// Pick SUID of some nodes
		CyNode node1 = network.getNodeList().get(0);
		
		String newData = "{" +
			 "\"key\":\"SUID\"," +
			 "\"dataKey\": \"id\"," +
			 "\"data\": [{" +
			 "\"id\": " + node1.getSUID() + "," +
			 "\"gene_name\": \"brca1\"," +
			 "\"new\": [ 4.5, \"potrzebie\" ]" +
			 "}]}";
		
		Entity<String> entity = Entity.entity(newData, MediaType.APPLICATION_JSON_TYPE);
		final Long suid = network.getSUID();
		
		Response result = target(
				"/v1/networks/" + suid.toString() + "/tables/defaultnode")
				.queryParam("class", "local")
				.request().put(entity);
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		
		assertEquals("brca1", 
				network.getRow(node1).get("gene_name", String.class));
		
		final List<Double> newValue = network.getRow(node1).getList("new", Double.class);
		
		assertEquals(null, newValue);
		
		final CyTable localTable = network
				.getTable(CyNode.class, CyNetwork.LOCAL_ATTRS);
		Collection<CyColumn> cols = localTable.getColumns();
		System.out.println("Columns: " + cols);
		assertEquals(7, cols.size());
		
	}
	

	@Test
	public void testUpdateColumnDefaultValues() throws Exception {
		final Entity<String> entity = Entity.entity("", MediaType.APPLICATION_JSON_TYPE);
		final Long suid = network.getSUID();
		
		Response result = target("/v1/networks/" + suid.toString() + "/tables/defaultnode/columns/selected")
				.queryParam("default", "true").request().put(entity);
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		System.out.println("res: " + result.toString());
		
		for(CyNode node: network.getNodeList()) {
			assertTrue(network.getRow(node).get(CyNetwork.SELECTED, Boolean.class));
		}
		
	}


	@Test
	public void testCreateColumn() throws Exception {
		testCreateColumForGivenType("defaultnode", network.getDefaultNodeTable());
		testCreateColumForGivenType("defaultedge", network.getDefaultEdgeTable());
		testCreateColumForGivenType("defaultnetwork", network.getDefaultNetworkTable());
	}
	
	private final void testCreateColumForGivenType(final String type, CyTable table) {
		final String strColumn = "{"
				+ "\"name\": \"strColumn\","
				+ "\"type\": \"String\" }";
		
		final String intColumn = "{"
				+ "\"name\": \"intColumn\","
				+ "\"immutable\": \"true\","
				+ "\"type\": \"Integer\" }";
		
		final String doubleColumn = "{"
				+ "\"name\": \"doubleColumn\","
				+ "\"isLocal\": true,"
				+ "\"type\": \"Double\"}";

		final String boolColumn = "{"
				+ "\"name\": \"boolColumn\","
				+ "\"type\": \"Boolean\" }";
		
		final String srtListColumn = "{"
				+ "\"name\": \"strListColumn\","
				+ "\"immutable\": \"true\","
				+ "\"list\": \"true\","
				+ "\"type\": \"String\" }";
		
		// Test String column
		Entity<String> entity = Entity.entity(strColumn, MediaType.APPLICATION_JSON_TYPE);
		final Long suid = network.getSUID();
		
		Response result = target("/v1/networks/" + suid.toString() + "/tables/" + type +"/columns")
				.request().post(entity);
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(201, result.getStatus());
		System.out.println("res: " + result.toString());
		
		final CyColumn strCol = table.getColumn("strColumn");
		assertNotNull(strCol);
		assertEquals(String.class, strCol.getType());
		
		// Test integer column
		entity = Entity.entity(intColumn, MediaType.APPLICATION_JSON_TYPE);
		result = target("/v1/networks/" + suid.toString() + "/tables/" + type + "/columns")
				.request().post(entity);
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(201, result.getStatus());
		System.out.println("res: " + result.toString());
		
		final CyColumn intCol = table.getColumn("intColumn");
		assertNotNull(intCol);
		assertEquals(Integer.class, intCol.getType());
		
		// Double
		entity = Entity.entity(doubleColumn, MediaType.APPLICATION_JSON_TYPE);
		result = target("/v1/networks/" + suid.toString() + "/tables/" + type + "/columns").request().post(entity);
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(201, result.getStatus());
		System.out.println("res: " + result.toString());
		
		final CyColumn doubleCol = table.getColumn("doubleColumn");
		assertNotNull(doubleCol);
		assertEquals(Double.class, doubleCol.getType());
		
		// Boolean
		entity = Entity.entity(boolColumn, MediaType.APPLICATION_JSON_TYPE);
		result = target("/v1/networks/" + suid.toString() + "/tables/" + type + "/columns").request().post(entity);
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(201, result.getStatus());
		System.out.println("res: " + result.toString());
		
		final CyColumn boolCol = table.getColumn("boolColumn");
		assertNotNull(boolCol);
		assertEquals(Boolean.class, boolCol.getType());
		
		// String List
		entity = Entity.entity(srtListColumn, MediaType.APPLICATION_JSON_TYPE);
		result = target("/v1/networks/" + suid.toString() + "/tables/" + type + "/columns").request().post(entity);
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(201, result.getStatus());
		System.out.println("res: " + result.toString());
		
		final CyColumn strListCol = table.getColumn("strListColumn");
		assertNotNull(strListCol);
		assertEquals(List.class, strListCol.getType());
		assertEquals(String.class, strListCol.getListElementType());
	}


	@Test
	public void testDeleteColumn() throws Exception {
		deleteColumn("defaultnode", network.getDefaultNodeTable());
		deleteColumn("defaultedge", network.getDefaultEdgeTable());
		deleteColumn("defaultnetwork", network.getDefaultNetworkTable());
	}


	private void deleteColumn(final String type, final CyTable table) {
		// Create dummy column
		table.createColumn("dummy", String.class, false);
		
		final Long suid = network.getSUID();
		Response result = target("/v1/networks/" + suid.toString() + "/tables/" + type + "/columns/dummy").request().delete();
		assertNotNull(result);
		System.out.println("res: " + result.toString());
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());

		final CyColumn dummyCol = table.getColumn("dummy");
		assertNull(dummyCol);
	}
	
}