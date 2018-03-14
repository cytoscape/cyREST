package org.cytoscape.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import javax.ws.rs.Path;

import org.cytoscape.property.CyProperty;
import org.cytoscape.rest.internal.task.AutomationAppTracker;
import org.cytoscape.rest.internal.task.CyPropertyListener;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.task.NetworkViewCollectionTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.TableTaskFactory;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;

public class CyPropertyListenerTest {

	CyPropertyListener cyPropertyListener;
	
	@Before
	public void setUp() throws Exception {
		cyPropertyListener = new CyPropertyListener();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEmpty() {
		assertEquals(0, cyPropertyListener.getPropertyNames().size());
		assertNull(cyPropertyListener.getCyProperty("some.property"));
	}

	@Test
	public void testAddProperties() {
		CyProperty<?> cyProperty = mock(CyProperty.class);
		when(cyProperty.getName()).thenReturn("new Cy Property");
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return new Properties();
			}
		}).when(cyProperty).getProperties();
		
		HashMap<String, String> serviceProperties = new HashMap<String, String>();
		serviceProperties.put("cyPropertyName", "newCyProperty");
		cyPropertyListener.addCyProperty(cyProperty, serviceProperties);
		
		assertEquals(1, cyPropertyListener.getPropertyNames().size());
		assertSame(cyProperty, cyPropertyListener.getCyProperty("newCyProperty"));
	}

	@Test
	public void testRemoveProperty() {
		CyProperty<?> cyProperty = mock(CyProperty.class);
		when(cyProperty.getName()).thenReturn("new Cy Property");
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return new Properties();
			}
		}).when(cyProperty).getProperties();
		
		HashMap<String, String> serviceProperties = new HashMap<String, String>();
		serviceProperties.put("cyPropertyName", "newCyProperty");
		cyPropertyListener.addCyProperty(cyProperty, serviceProperties);
		
		assertEquals(1, cyPropertyListener.getPropertyNames().size());
		assertSame(cyProperty, cyPropertyListener.getCyProperty("newCyProperty"));
		
		cyPropertyListener.removeCyProperty(cyProperty, serviceProperties);
		
		assertEquals(0, cyPropertyListener.getPropertyNames().size());
		assertNull(cyPropertyListener.getCyProperty("newCyProperty"));
	}
}