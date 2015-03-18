package org.cytoscape.rest.service;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.rest.internal.resource.StyleResource;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.ContinuousMappingPoint;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.w3c.dom.ls.LSException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StyleResourceTest extends BasicResourceTest {

	@Override
	protected Application configure() {
		return new ResourceConfig(StyleResource.class);
	}

	private ObjectMapper mapper = new ObjectMapper();

	
	@Test
	public void testGetStyles() throws Exception {
		String result = target("/v1/styles").request().get(String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		assertTrue(root.isArray());
		assertEquals(2, root.size());
		Set<String> names = new HashSet<>();
		for(JsonNode styleName: root) {
			names.add(styleName.asText());
		}
		assertTrue(names.contains("vs1"));
		assertTrue(names.contains("mock1"));
		assertFalse(names.contains("vs2"));
	}


	@Test
	public void testGetStylesCount() throws Exception {
		String result = target("/v1/styles/count").request().get(String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		int count = root.get("count").asInt();
		assertEquals(2, count);
	}
	
	
	@Test
	public void testGetStyle() throws Exception {
		String result = target("/v1/styles/mock1").request().get(String.class);
		assertNotNull(result);
		final JsonNode root = mapper.readTree(result);
		assertEquals("mock1", root.get("title").asText());
	}
	
	
	@Test
	public void testGetStyleDefaults() throws Exception {
		final Response result = target("/v1/styles/vs1/defaults").request().get();
		assertNotNull(result);
		final String body = result.readEntity(String.class);
		System.out.println(body);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.get("defaults").isArray());
	}


	@Test
	public void testGetMappings() throws Exception {
		final Response result = target("/v1/styles/vs1/mappings").request().get();
		assertNotNull(result);
		System.out.println("res: " + result.toString());
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		String body = result.readEntity(String.class);
		System.out.println(body);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.isArray());
		assertEquals(11, root.size());
		
		for(JsonNode mapping:root) {
			JsonNode type = mapping.get("mappingType");
			assertNotNull(type);
			if(type.asText().equals("discrete")) {
				testDiscrete(mapping);
			} else if(type.asText().equals("continuous")) {
				testContinuous(mapping);
			} else if(type.asText().equals("passthrough")) {
				testPassthrough(mapping);
			} else {
				fail("No such mapping type: " + type.asText());
			}
		}
	}
	
	private void testDiscrete(JsonNode mapping) {
		checkBasicSettings(mapping);
		final JsonNode map = mapping.get("map");
		assertNotNull(map);
		assertTrue(map.isArray());
	}
	
	private void testContinuous(JsonNode mapping) {
		checkBasicSettings(mapping);
		final JsonNode points = mapping.get("points");
		assertNotNull(points);
		assertTrue(points.isArray());
		
		for(JsonNode point:points) {
			JsonNode val = point.get("value");
			assertNotNull(val);
			assertTrue(val.isNumber());
			JsonNode l = point.get("lesser");
			assertNotNull(l);
			JsonNode eq = point.get("equal");
			assertNotNull(eq);
			JsonNode g = point.get("greater");
			assertNotNull(g);
		}
	}
	
	private void testPassthrough(JsonNode mapping) {
		checkBasicSettings(mapping);
	}
	
	private final void checkBasicSettings(JsonNode mapping) {
		final JsonNode column = mapping.get("mappingColumn");
		assertNotNull(column);
		final JsonNode columnType= mapping.get("mappingColumnType");
		assertNotNull(columnType);
		final JsonNode vp = mapping.get("visualProperty");
		assertNotNull(vp);
	}

	@Test
	public void testCreateMapping() throws Exception {
		testCreateDiscrete();
		testCreateContinuous();
		testCreatePassthrough();
	}


	private void testCreateDiscrete() {
		final String edgeWidthMapping = "[{"
				+ "\"mappingType\": \"discrete\","
				+ "\"mappingColumn\": \"interaction\","
				+ "\"mappingColumnType\": \"String\","
				+ "\"visualProperty\": \"EDGE_WIDTH\", "
				+ "\"map\": ["
				+ "{\"key\" : \"pd\", \"value\" : \"20\"},"
				+ "{\"key\" : \"pp\", \"value\" : \"1.5\"}"
				+ "]}"
				+ "]";
		
		// Test String column
		Entity<String> entity = Entity.entity(edgeWidthMapping, MediaType.APPLICATION_JSON_TYPE);
		Response result = target("/v1/styles/vs1/mappings").request().post(entity);
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(201, result.getStatus());
		System.out.println("res: " + result.toString());
		
	}


	private void testCreateContinuous() {
		final String nodeSizeMapping = "[{"
				+ "\"mappingType\": \"continuous\","
				+ "\"mappingColumn\": \"Degree\","
				+ "\"mappingColumnType\": \"Double\","
				+ "\"visualProperty\": \"NODE_SIZE\", "
				+ "\"points\": ["
				+ "{\"value\" : 1, \"lesser\" : \"20\" , \"equal\" : \"20\", \"greater\" : \"20\" },"
				+ "{\"value\" : 20, \"lesser\" : \"120\" , \"equal\" : \"120\", \"greater\" : \"220\" }"
				+ "]}"
				+ "]";
		
		// Test String column
		Entity<String> entity = Entity.entity(nodeSizeMapping, MediaType.APPLICATION_JSON_TYPE);
		Response result = target("/v1/styles/vs1/mappings").request().post(entity);
		System.out.println("result: " + result.toString());
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(201, result.getStatus());
		System.out.println("res: " + result.toString());
		
		ContinuousMapping<?, Double> mapping = (ContinuousMapping<?, Double>) style.getVisualMappingFunction(BasicVisualLexicon.NODE_SIZE);
		assertNotNull(mapping);
		assertEquals("Degree", mapping.getMappingColumnName());
		assertEquals(Double.class, mapping.getMappingColumnType());
		assertEquals(2, mapping.getAllPoints().size());
		ContinuousMappingPoint<?, Double> p1 = mapping.getPoint(0);
		assertTrue(p1.getValue() instanceof Number);
		assertEquals(Double.class, p1.getRange().equalValue.getClass());
	}


	private void testCreatePassthrough() {
		final String edgeLabelMapping = "[{"
				+ "\"mappingType\": \"passthrough\","
				+ "\"mappingColumn\": \"name\","
				+ "\"mappingColumnType\": \"String\","
				+ "\"visualProperty\": \"EDGE_LABEL\" }]";
		
		// Test String column
		Entity<String> entity = Entity.entity(edgeLabelMapping, MediaType.APPLICATION_JSON_TYPE);
		Response result = target("/v1/styles/vs1/mappings").request().post(entity);
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(201, result.getStatus());
		System.out.println("res: " + result.toString());
	}
}
