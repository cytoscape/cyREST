package org.cytoscape.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.cytoscape.rest.internal.datamapper.MapperUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MapperUtilTest {

//	private final NetworkTestSupport testSupport = new NetworkTestSupport();


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetColumnClass() {
		assertEquals(Double.class, MapperUtil.getColumnClass("Number", true));
		assertEquals(Number.class, MapperUtil.getColumnClass("Number", false));
		
		assertEquals(Double.class, MapperUtil.getColumnClass("Double", true));
		assertEquals(Integer.class, MapperUtil.getColumnClass("Integer", true));
		assertEquals(Float.class, MapperUtil.getColumnClass("Float", true));
		assertEquals(Long.class, MapperUtil.getColumnClass("Long", true));
		assertEquals(Boolean.class, MapperUtil.getColumnClass("Boolean", true));
		assertEquals(String.class, MapperUtil.getColumnClass("String", true));
		assertNull(MapperUtil.getColumnClass("Hodor", true));
	}

	

}