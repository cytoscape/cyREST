package org.cytoscape.rest.service;

import static org.junit.Assert.assertTrue;


import org.cytoscape.work.json.ExampleJSONString;
import org.cytoscape.work.json.JSONResult;
import org.junit.Test;

/**
 * This test outlines some casting behavior relied upon by JSONResultTaskObserver to identify and extract JSONResult 
 * returns from Tasks.
 * @author David Otasek (dotasek.dev@gmail.com)
 *
 */
public class JSONResultCastTest {

	public class TestJSONResult implements JSONResult{

		@Override
		@ExampleJSONString(value="{\"name\":\"Chuck\"}")
		public String getJSON() {

			return "{\"name\":\"Chuck\"}";
		}

		//This is just here to add a method so the methods list couldn't resolve to the same as JSONResult.
		public String getDummy() {
			return "dummy";
		}
	}
	
	@Test
	public void basicCastTests() throws Exception {
		assertTrue(JSONResult.class.isAssignableFrom(TestJSONResult.class));
	}
}