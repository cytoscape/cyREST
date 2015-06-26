package org.cytoscape.rest.service;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Paint;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.Collectors;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.rest.internal.resource.StyleResource;
import org.cytoscape.view.model.DiscreteRange;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.values.VisualPropertyValue;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.ContinuousMappingPoint;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
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
	public void testGetDefaultValues() throws Exception {
		final Response result = target("/v1/styles/vs1/defaults").request().get();
		assertNotNull(result);
		final String body = result.readEntity(String.class);
		System.out.println(body);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.get("defaults").isArray());
		final JsonNode defaults = root.get("defaults");
		assertEquals(103, defaults.size());
	}


	@Test
	public void testGetRangeValues() throws Exception {
		testRange(BasicVisualLexicon.NODE_SHAPE);
		testRange(BasicVisualLexicon.NODE_BORDER_LINE_TYPE);
		testRange(BasicVisualLexicon.EDGE_LINE_TYPE);
		testRange(BasicVisualLexicon.EDGE_SOURCE_ARROW_SHAPE);
		testRange(BasicVisualLexicon.EDGE_TARGET_ARROW_SHAPE);
	}
	
	
	private final void testRange(VisualProperty<?> vp) throws JsonProcessingException, IOException {
		Set<?> values = ((DiscreteRange<?>)vp.getRange()).values();
		Set<String> valueStrings = new HashSet<>();
		for(Object val:values) {
			VisualPropertyValue vpv = (VisualPropertyValue) val;
			valueStrings.add(vpv.getSerializableString());
		}
		
		Response result = target("/v1/styles/visualproperties/" + vp.getIdString() + "/values").request().get();
		assertNotNull(result);
		final String body = result.readEntity(String.class);
		System.out.println(body);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.isObject());
		final JsonNode generatedValues = root.get("values");
		assertNotNull(generatedValues);
		assertTrue(generatedValues.isArray());
		assertEquals(values.size(), generatedValues.size());
		
		for(JsonNode node: generatedValues) {
			assertTrue(valueStrings.contains(node.asText()));
		}
	}


	@Test
	public void testGetVisualProperties() throws Exception {
		Response result = target("/v1/styles/visualproperties").request().get();
		assertNotNull(result);
		final String body = result.readEntity(String.class);
		System.out.println(body);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.isArray());
		assertEquals(103, root.size());
	}


	@Test
	public void testGetVisualProperty() throws Exception {
		final Set<VisualProperty<?>> vps = lexicon.getAllVisualProperties();
		for (VisualProperty vp : vps) {
			Response result = target("/v1/styles/visualproperties/" + vp.getIdString()).request().get();
			assertNotNull(result);
			final String body = result.readEntity(String.class);
			System.out.println(body);
			final JsonNode root = mapper.readTree(body);
			assertTrue(root.isObject());
			assertEquals(vp.getIdString(), root.get("visualProperty").asText());
			assertEquals(vp.getDisplayName(), root.get("name").asText());
			assertEquals(vp.getTargetDataType().getSimpleName(), root.get("targetDataType").asText());
			String def = vp.toSerializableString(vp.getDefault());
			if(def != null)
				assertEquals(def, 
					root.get("default").asText());
		}
	
	}
	
	
	@Test
	public void testGetDefaultValue() throws Exception {
		
		final String vp = BasicVisualLexicon.NODE_FILL_COLOR.getIdString();
		Response result = target("/v1/styles/vs1/defaults/NODE_FILL_COLOR").request().get();
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		final String body = result.readEntity(String.class);
		System.out.println(body);
		final JsonNode root = mapper.readTree(body);
		assertTrue(root.isObject());
		assertEquals(vp, root.get("visualProperty").textValue());
		Integer colorValue = ((Color)style.getDefaultValue(BasicVisualLexicon.NODE_FILL_COLOR)).getRGB();
		String inHex = Integer.toHexString(colorValue);
		final Integer inHexWithoutAlpha = Integer.parseInt(inHex.substring(2), 16);
		final String fillColorValue = root.get("value").asText();
		final String colorInHex = fillColorValue.substring(1);
		System.out.println("Fill: " + colorInHex);
		final Integer newColorValue = Integer.parseInt(colorInHex, 16);
		assertEquals(inHexWithoutAlpha, newColorValue);
	}


	@Test
	public void testGetMappings() throws Exception {
		final Response emptyResult = target("/v1/styles/mock1/mappings").request().get();
		assertNotNull(emptyResult);
		assertFalse(emptyResult.getStatus() == 500);
		assertEquals(200, emptyResult.getStatus());
		String body0 = emptyResult.readEntity(String.class);
		System.out.println(body0);
		final JsonNode root0 = mapper.readTree(body0);
		assertTrue(root0.isArray());
		assertEquals(0, root0.size());
		
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
	public void testCreateStyle() throws Exception {
		final String style1 = "{ \"title\": \"style1\""
				+ "}";
		System.out.println("Data: " + style1);
		final Entity<String> entity = Entity.entity(style1, MediaType.APPLICATION_JSON_TYPE);
		final Response result = target("/v1/styles").request().post(entity);
		assertNotNull(result);
		System.out.println("res: " + result.toString());
		String body = result.readEntity(String.class);
		assertFalse(result.getStatus() == 500);
		assertEquals(201, result.getStatus());
		assertTrue(body.contains("title"));
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
	
	@Test
	public void testUpdateMapping() throws Exception {
		final String nodeLabelMapping = "[{"
				+ "\"mappingType\": \"passthrough\","
				+ "\"mappingColumn\": \"name\","
				+ "\"mappingColumnType\": \"String\","
				+ "\"visualProperty\": \"NODE_LABEL\" }]";
		
		// Test String column
		Entity<String> entity = Entity.entity(nodeLabelMapping, MediaType.APPLICATION_JSON_TYPE);
		Response result = target("/v1/styles/vs1/mappings/NODE_LABEL").request().put(entity);
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());
		System.out.println("res: " + result.toString());
		
		final String nodeTooltipMapping = "[{"
				+ "\"mappingType\": \"passthrough\","
				+ "\"mappingColumn\": \"name\","
				+ "\"mappingColumnType\": \"String\","
				+ "\"visualProperty\": \"NODE_TOOLTIP\" }]";
		
		// Test String column
		entity = Entity.entity(nodeTooltipMapping, MediaType.APPLICATION_JSON_TYPE);
		result = target("/v1/styles/vs1/mappings/NODE_TOOLTIP").request().put(entity);
		assertNotNull(result);
		assertFalse(result.getStatus() == 500);
		assertEquals(404, result.getStatus());
		System.out.println("res: " + result.toString());
	}
	
	@Test
	public void testDeleteStyle() throws Exception {
		
		final Response result = target("/v1/styles/vs1").request().delete();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
	}
	
	
	@Test
	public void testDeleteAllStyles() throws Exception {
		
		final Response result = target("/v1/styles").request().delete();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
	}
	
	@Test
	public void testDeleteMapping() throws Exception {
		
		final Response result = target("/v1/styles/vs1/mappings/NODE_LABEL").request().delete();
		assertNotNull(result);
		assertEquals(200, result.getStatus());
	}
	
	private final String createDefaultsJson() throws Exception {
		final JsonFactory factory = new JsonFactory();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = null;
		generator = factory.createGenerator(stream);
		generator.writeStartArray();
			
		generator.writeStartObject();
		generator.writeStringField("visualProperty", BasicVisualLexicon.NODE_FILL_COLOR.getIdString());
		generator.writeStringField("value", "blue");
		generator.writeEndObject();

		generator.writeStartObject();
		generator.writeStringField("visualProperty", BasicVisualLexicon.NODE_SIZE.getIdString());
		generator.writeNumberField("value", 120);
		generator.writeEndObject();
		
		generator.writeStartObject();
		generator.writeStringField("visualProperty", BasicVisualLexicon.EDGE_LABEL_FONT_SIZE.getIdString());
		generator.writeNumberField("value", 20);
		generator.writeEndObject();
		
		generator.writeEndArray();
		generator.close();
		final String result = stream.toString("UTF-8");
		stream.close();
		return result;
	}

	@Test
	public void testUpdateDefault() throws Exception {
		String newVal = createDefaultsJson();
		Entity<String> entity = Entity.entity(newVal, MediaType.APPLICATION_JSON_TYPE);
		Response result = target("/v1/styles/vs1/defaults").request().put(entity);
		assertNotNull(result);
		System.out.println("res: " + result.toString());
		assertFalse(result.getStatus() == 500);
		assertEquals(200, result.getStatus());

		final String body = result.readEntity(String.class);
		System.out.println("BODY: " + body);
		assertEquals("", body);
		
		// TODO: Add more updated value check
		Paint fillColor = style.getDefaultValue(BasicVisualLexicon.NODE_FILL_COLOR);
		System.out.println("Fill Color: " + ((Color)fillColor));
		assertEquals(Color.blue, fillColor);
	}
	
}
