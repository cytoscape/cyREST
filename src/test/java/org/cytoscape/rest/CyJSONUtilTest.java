package org.cytoscape.rest;

import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.rest.internal.CyJSONUtilImpl;
import org.cytoscape.util.json.CyJSONUtil;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;

public class CyJSONUtilTest {
	
	CyJSONUtil jsonUtil = new CyJSONUtilImpl();
	
	JsonParser parser = new JsonParser();
	
	@Test
	public void getSingleObjectTest() {
		CyNode node = mock(CyNode.class);
		when(node.getSUID()).thenReturn(13l);
		
		String jsonString = jsonUtil.toJson((CyIdentifiable)node);
		System.out.println(jsonString);
		JsonElement rootElement = parser.parse(jsonString);
		assertEquals(13l, rootElement.getAsLong());
	}
	
	@Test
	public void getMultipleObjectTest() {
		CyNode nodeA = mock(CyNode.class);
		when(nodeA.getSUID()).thenReturn(13l);
		
		CyNode nodeB = mock(CyNode.class);
		when(nodeB.getSUID()).thenReturn(14l);
		
		List<CyIdentifiable> list = new ArrayList<CyIdentifiable>();
		list.add(nodeA);
		list.add(nodeB);
		
		String jsonString = jsonUtil.cyIdentifiablesToJson(list);
		JsonElement rootElement = parser.parse(jsonString);
		assertEquals(13l, rootElement.getAsJsonArray().get(0).getAsLong());
		assertEquals(14l, rootElement.getAsJsonArray().get(1).getAsLong());
		assertEquals(2, rootElement.getAsJsonArray().size());
		System.out.println(jsonString);
		
	}
	
	@Test 
	public void getCyRowTest() {
		CyRow row = mock(CyRow.class);
		Map<String, Object> dummyMap = new HashMap<String, Object>();
		
		dummyMap.put("SUID", 13l);
		dummyMap.put("stringCol", "dummyStringValue");
		dummyMap.put("doubleCol", 14.0);
		dummyMap.put("intCol", 15);
		
		List<String> dummyStringList = Arrays.asList("Hey", "Ho");
		
		dummyMap.put("booleanCol", true);
		dummyMap.put("listCol", dummyStringList);
		
		when(row.getAllValues()).thenReturn(dummyMap);
		String jsonString = jsonUtil.toJson(row);
		JsonElement rootElement = parser.parse(jsonString);
		System.out.println(jsonString);
	}
}
