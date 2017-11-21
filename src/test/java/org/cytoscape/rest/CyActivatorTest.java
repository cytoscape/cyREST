package org.cytoscape.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.eq;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import org.cytoscape.property.CyProperty;
import org.cytoscape.rest.internal.CyActivator;
import org.cytoscape.rest.internal.CyActivator.ServerState;
import org.cytoscape.rest.internal.task.ResourceManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class CyActivatorTest {
	ConfigurationAdmin configAdmin;
	Configuration paxWebConfig;

	Configuration jaxRsConnectorConfig;

	BundleContext bc;
	ServiceReference configAdminServiceReference;

	Bundle bundle;

	URLConnection mockUrlCon;

	URLStreamHandler stubUrlHandler = new URLStreamHandler() {
		@Override
		protected URLConnection openConnection(URL u) throws IOException {
			return mockUrlCon;
		}            
	};

	Properties properties;
	Properties cyCommandProperties;

	@Before
	public void before() throws IOException, InvalidSyntaxException {
		configAdmin = mock(ConfigurationAdmin.class);
		paxWebConfig = mock(Configuration.class);
		when(configAdmin.getConfiguration("org.ops4j.pax.web", null)).thenReturn(paxWebConfig);

		jaxRsConnectorConfig = mock(Configuration.class);
		when(configAdmin.getConfiguration("com.eclipsesource.jaxrs.connector", null)).thenReturn(jaxRsConnectorConfig);

		bc = mock(BundleContext.class);
		configAdminServiceReference = mock(ServiceReference.class);
		when(bc.getServiceReference(ConfigurationAdmin.class.getName())).thenReturn(configAdminServiceReference);
		when(bc.getService(configAdminServiceReference)).thenReturn(configAdmin);

		VisualMappingFunctionFactory visualMappingFunctionFactory = mock(VisualMappingFunctionFactory.class);
		
		bundle = mock(Bundle.class);
		when(bundle.getSymbolicName()).thenReturn("dummyCyRESTBundleName");
		when(bundle.getVersion()).thenReturn(new Version(1,2,3));
		when(bc.getBundle()).thenReturn(bundle);

		mockUrlCon = mock(URLConnection.class);

		ByteArrayInputStream is = new ByteArrayInputStream(
				"<myList></myList>".getBytes("UTF-8"));
		doReturn(is).when(mockUrlCon).getInputStream();

		//make getLastModified() return first 10, then 11
		when(mockUrlCon.getLastModified()).thenReturn((Long)10L, (Long)11L);

		doAnswer( new Answer<URL>() {
			public URL answer(InvocationOnMock invocation) throws MalformedURLException {
				URL url = new URL("foo", "bar", 99, (String)invocation.getArguments()[0], stubUrlHandler);
				return url;
			}
		}).when(bundle).getResource(anyString());

		CyProperty<Properties> cyProperty = mock(CyProperty.class);
		properties = new Properties();
		properties.put("cyrest.version", "1.2.3");
		when(cyProperty.getProperties()).thenReturn(properties);
		
		CyProperty<Properties> cyCommandProperty = mock(CyProperty.class);
		cyCommandProperties = new Properties();
		cyCommandProperties.put(ResourceManager.PORT_NUMBER_PROP, "4321");
		when(cyCommandProperty.getProperties()).thenReturn(cyCommandProperties);

		ConfigurationAdmin configAdmin = mock(ConfigurationAdmin.class);
		Configuration configuration = mock(Configuration.class);
		when(configAdmin.getConfiguration("org.ops4j.pax.logging")).thenReturn(configuration);
		when(configAdmin.getConfiguration("org.ops4j.pax.web", null)).thenReturn(configuration);
		when(configAdmin.getConfiguration("com.eclipsesource.jaxrs.connector", null)).thenReturn(configuration);
		Dictionary<String,Object> dictionary = new Hashtable<String, Object>();
		dictionary.put("log4j.appender.file.File", "dummyLogLocation");
		when(configuration.getProperties()).thenReturn(dictionary);
		
		when(bc.createFilter(anyString())).thenReturn(mock(Filter.class));

		ServiceReference cyPropertyServiceReference = mock(ServiceReference.class);
		when(cyPropertyServiceReference.getPropertyKeys()).thenReturn(new String[]{});
		ServiceReference cyCommandPropertyServiceReference = mock(ServiceReference.class);
		when(cyCommandPropertyServiceReference.getPropertyKeys()).thenReturn(new String[]{});
		
		when(configAdminServiceReference.getPropertyKeys()).thenReturn(new String[]{});
		
		doAnswer( new Answer<ServiceReference[]>() {
			public ServiceReference[] answer(InvocationOnMock invocation) {
				if (CyProperty.class.getName().equals((String)invocation.getArguments()[0])) {
					if ("(cyPropertyName=cytoscape3.props)".equals((String)invocation.getArguments()[1])) {
						return new ServiceReference[] {cyPropertyServiceReference};
					} else if ("(cyPropertyName=commandline.props)".equals((String)invocation.getArguments()[1])){
						return new ServiceReference[] {cyCommandPropertyServiceReference};
					}
				}
				ServiceReference serviceReference = mock(ServiceReference.class);
				if (invocation.getArguments()[0] == null) {
					when(serviceReference.getPropertyKeys()).thenReturn(new String[]{});
					return new ServiceReference[] {};//{serviceReference};
				}
			
				when(serviceReference.getPropertyKeys()).thenReturn(new String[]{"castClass"});
				when(serviceReference.getProperty(eq("castClass"))).thenReturn(invocation.getArguments()[0]);
				return new ServiceReference[] {serviceReference};
			}
		}).when(bc).getServiceReferences(anyString(), anyString());

		when(bc.getAllServiceReferences(anyString(), anyString())).thenReturn(new ServiceReference[]{});
		
		doAnswer( new Answer<ServiceReference>() {
			public ServiceReference answer(InvocationOnMock invocation) {
				if (ConfigurationAdmin.class.getName().equals((String)invocation.getArguments()[0])) {
					return configAdminServiceReference;
				} 
				ServiceReference serviceReference = mock(ServiceReference.class);
				when(serviceReference.getPropertyKeys()).thenReturn(new String[]{"castClass"});
				when(serviceReference.getProperty(eq("castClass"))).thenReturn(invocation.getArguments()[0]);
				return serviceReference;
			}
		}).when(bc).getServiceReference(anyString());
		
		doAnswer( new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws ClassNotFoundException {
				if (cyPropertyServiceReference == invocation.getArguments()[0]) {
					return cyProperty;
				} else if (cyCommandPropertyServiceReference == invocation.getArguments()[0]) {
					return cyCommandProperty;
				} else if (configAdminServiceReference  == invocation.getArguments()[0]) {
					return configAdmin;
				} 
				Object classCast = ((ServiceReference)invocation.getArguments()[0]).getProperty("castClass");
				if (classCast != null) {
					return mock(Class.forName((String) classCast));
				} else {
					System.out.print(invocation.getArguments()[0]);
					return new Object();
				}
			}
		}).when(bc).getService(any(ServiceReference.class));
		
	
	}

	@Test
	public void normalStartTest() throws Exception {

		//System.out.println(bc.getBundle().getVersion().toString() + " " +  properties.getProperty("cyrest.version"));
		CyActivator cyActivator = new CyActivator();

		assertEquals(CyActivator.ServerState.STOPPED, cyActivator.getServerState());
		cyActivator.start(bc);

		while(cyActivator.getServerState() == ServerState.STARTING || cyActivator.getServerState() == ServerState.STOPPED)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {	
				e.printStackTrace();
				fail();
			}
		}

		assertEquals(ServerState.STARTED, cyActivator.getServerState());
	}
	
	@Test
	public void freshUpdateStartTest() throws Exception {
		properties.put("cyrest.version", "1.2.2");
		CyActivator cyActivator = new CyActivator();

		assertEquals(CyActivator.ServerState.STOPPED, cyActivator.getServerState());
		cyActivator.start(bc);

		while(cyActivator.getServerState() == ServerState.STARTING || cyActivator.getServerState() == ServerState.STOPPED)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {	
				e.printStackTrace();
				fail();
			}
		}

		assertEquals(ServerState.SUGGEST_RESTART, cyActivator.getServerState());
	}
	
	@Test
	public void noPreviousCyRestStartTest() throws Exception {
		properties.remove("cyrest.version");
		CyActivator cyActivator = new CyActivator();

		assertEquals(CyActivator.ServerState.STOPPED, cyActivator.getServerState());
		cyActivator.start(bc);

		while(cyActivator.getServerState() == ServerState.STARTING || cyActivator.getServerState() == ServerState.STOPPED)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {	
				e.printStackTrace();
				fail();
			}
		}

		assertEquals(ServerState.SUGGEST_RESTART, cyActivator.getServerState());
	}
}
