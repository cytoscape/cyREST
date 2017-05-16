package org.cytoscape.rest;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.cytoscape.rest.internal.CyActivator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
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
	

	@Before
	public void before() throws IOException {
		configAdmin = mock(ConfigurationAdmin.class);
		paxWebConfig = mock(Configuration.class);
		when(configAdmin.getConfiguration("org.ops4j.pax.web", null)).thenReturn(paxWebConfig);

		jaxRsConnectorConfig = mock(Configuration.class);
		when(configAdmin.getConfiguration("com.eclipsesource.jaxrs.connector", null)).thenReturn(jaxRsConnectorConfig);

		bc = mock(BundleContext.class);
		configAdminServiceReference = mock(ServiceReference.class);
		when(bc.getServiceReference(ConfigurationAdmin.class.getName())).thenReturn(configAdminServiceReference);
		when(bc.getService(configAdminServiceReference)).thenReturn(configAdmin);

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
	}
	
	@Test
	public void startTest() throws InvalidSyntaxException {
		
		CyActivator cyActivator = new CyActivator();
		cyActivator.start(bc);
	}
}
