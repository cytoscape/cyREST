package org.cytoscape.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Hashtable;

import javax.ws.rs.Path;

import org.cytoscape.rest.internal.task.AutomationAppTracker;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.task.NetworkViewCollectionTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.TableTaskFactory;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;

public class AutomationAppTrackerTest {


	BundleContext bc;
	Filter filter;
	AutomationAppTracker automationAppTracker;
	
	@Before
	public void setUp() throws Exception {
		bc = mock(BundleContext.class);
		filter = mock(Filter.class);
		
		automationAppTracker = new AutomationAppTracker(bc, filter);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEmpty() {
		assertEquals(0, automationAppTracker.getAppBundles().size());
	}

	@Test
	public void testAddInvalidService() {
		assertEquals(0, automationAppTracker.getAppBundles().size());
		Bundle bundle = mock(Bundle.class);
		ServiceReference serviceReference = mock(ServiceReference.class);
		String service = new String();
		when(bc.getService(serviceReference)).thenReturn(service);
		
		when(serviceReference.getBundle()).thenReturn(bundle);
		automationAppTracker.addingService(serviceReference);
		
		assertEquals(0, automationAppTracker.getAppBundles().size());
	}
	
	@Test
	public void testAddCommandTaskFactoryService() {
		assertEquals(0, automationAppTracker.getAppBundles().size());
		Bundle bundle = mock(Bundle.class);
		
		Hashtable<String, String> dummyAutomationAppBundleHeaders = new Hashtable<String, String>();
		dummyAutomationAppBundleHeaders.put("Bundle-Name", "dummy command bundle");
		when(bundle.getHeaders()).thenReturn(dummyAutomationAppBundleHeaders);
		ServiceReference serviceReference = mock(ServiceReference.class);
		TaskFactory service = mock(TaskFactory.class);
		when(serviceReference.getProperty(ServiceProperties.COMMAND)).thenReturn("dummyCommand");
		when(serviceReference.getProperty(ServiceProperties.COMMAND_NAMESPACE)).thenReturn("dummyNamespace");
		when(bc.getService(serviceReference)).thenReturn(service);
		
		when(serviceReference.getBundle()).thenReturn(bundle);
		automationAppTracker.addingService(serviceReference);
		
		assertEquals(1, automationAppTracker.getAppBundles().size());
		assertSame(bundle, automationAppTracker.getAppBundles().iterator().next());
	
		assertEquals("dummy command bundle", automationAppTracker.getCommandSourceApp("dummyNamespace", "dummyCommand"));
	}
	
	@Test
	public void testModifiedService() {
		automationAppTracker.modifiedService(null, null);
	}
	
	@Test
	public void testObjectIsNotCommand() {
		Object service = new Object();
		assertEquals(false, automationAppTracker.isCommandTaskFactory(service));
	}
	
	@Test
	public void testNetworkTaskFactoryIsCommand() {
		NetworkTaskFactory service = mock(NetworkTaskFactory.class);
		assertEquals(true, automationAppTracker.isCommandTaskFactory(service));
	}
	
	@Test
	public void testTaskFactoryIsCommand() {
		TaskFactory service = mock(TaskFactory.class);
		assertEquals(true, automationAppTracker.isCommandTaskFactory(service));
	}
	
	@Test
	public void testNetworkViewTaskFactoryIsCommand() {
		NetworkViewTaskFactory service = mock(NetworkViewTaskFactory.class);
		assertEquals(true, automationAppTracker.isCommandTaskFactory(service));
	}
	
	@Test
	public void testNetworkViewCollectionTaskFactoryIsCommand() {
		NetworkViewCollectionTaskFactory service = mock(NetworkViewCollectionTaskFactory.class);
		assertEquals(true, automationAppTracker.isCommandTaskFactory(service));
	}
	
	@Test
	public void testTableTaskFactoryIsCommand() {
		TableTaskFactory service = mock(TableTaskFactory.class);
		assertEquals(true, automationAppTracker.isCommandTaskFactory(service));
	}
	
	@Path("/v1/apps/")
	public class DummyJaxRsService {
		
	}
	
	@Test
	public void testAddJaxRsService() {
		assertEquals(0, automationAppTracker.getAppBundles().size());
		Bundle bundle = mock(Bundle.class);
		ServiceReference serviceReference = mock(ServiceReference.class);
		DummyJaxRsService service = new DummyJaxRsService();
		when(bc.getService(serviceReference)).thenReturn(service);
		
		when(serviceReference.getBundle()).thenReturn(bundle);
		automationAppTracker.addingService(serviceReference);
		
		assertEquals(1, automationAppTracker.getAppBundles().size());
		assertSame(bundle, automationAppTracker.getAppBundles().iterator().next());
	}
	
	@Test
	public void testRemoveJaxRsService() {
		assertEquals(0, automationAppTracker.getAppBundles().size());
		Bundle bundle = mock(Bundle.class);
		ServiceReference serviceReference = mock(ServiceReference.class);
		DummyJaxRsService service = new DummyJaxRsService();
		when(bc.getService(serviceReference)).thenReturn(service);
		
		when(serviceReference.getBundle()).thenReturn(bundle);
		automationAppTracker.addingService(serviceReference);
		
		assertEquals(1, automationAppTracker.getAppBundles().size());
		assertSame(bundle, automationAppTracker.getAppBundles().iterator().next());
		
		automationAppTracker.removedService(serviceReference, service);
		
		assertEquals(0, automationAppTracker.getAppBundles().size());
	}
	
	@Test
	public void testRemoveMultipleJaxRsService() {
		assertEquals(0, automationAppTracker.getAppBundles().size());
		Bundle bundle = mock(Bundle.class);
		ServiceReference serviceReferenceA = mock(ServiceReference.class);
		DummyJaxRsService serviceA = new DummyJaxRsService();
		when(bc.getService(serviceReferenceA)).thenReturn(serviceA);
		
		when(serviceReferenceA.getBundle()).thenReturn(bundle);
		automationAppTracker.addingService(serviceReferenceA);
		
		ServiceReference serviceReferenceB = mock(ServiceReference.class);
		DummyJaxRsService serviceB = new DummyJaxRsService();
		when(bc.getService(serviceReferenceB)).thenReturn(serviceB);
		
		when(serviceReferenceB.getBundle()).thenReturn(bundle);
		automationAppTracker.addingService(serviceReferenceB);
		
		assertEquals(1, automationAppTracker.getAppBundles().size());
		assertSame(bundle, automationAppTracker.getAppBundles().iterator().next());
		
		automationAppTracker.removedService(serviceReferenceA, serviceA);
		
		assertEquals(1, automationAppTracker.getAppBundles().size());
		
		automationAppTracker.removedService(serviceReferenceB, serviceB);
		
		assertEquals(0, automationAppTracker.getAppBundles().size());
	}
	
	@Test
	public void testRemoveStoppingBundle() {
		assertEquals(0, automationAppTracker.getAppBundles().size());
		Bundle bundle = mock(Bundle.class);
		ServiceReference serviceReference = mock(ServiceReference.class);
		DummyJaxRsService service = new DummyJaxRsService();
		when(bc.getService(serviceReference)).thenReturn(service);
		
		when(serviceReference.getBundle()).thenReturn(bundle);
		automationAppTracker.addingService(serviceReference);
		
		assertEquals(1, automationAppTracker.getAppBundles().size());
		assertSame(bundle, automationAppTracker.getAppBundles().iterator().next());
		
		BundleEvent event = mock(BundleEvent.class);
		when(event.getBundle()).thenReturn(bundle);
		when(event.getType()).thenReturn(BundleEvent.STOPPING);
		automationAppTracker.bundleChanged(event);
		
		
	}
	
}