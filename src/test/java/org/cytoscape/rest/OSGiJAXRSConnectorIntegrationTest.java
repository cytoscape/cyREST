package org.cytoscape.rest;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.any;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.List;

import org.apache.karaf.features.FeaturesService;
import org.cytoscape.rest.internal.task.OSGiJAXRSManager;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class OSGiJAXRSConnectorIntegrationTest 
{

	private static final String JERSEY_MIN_PATH = "jersey-min/";

	private static final String[] JERSEY_MIN_BUNDLES = {
			//JERSEY_MISC_PATH + "javax.annotation-api-1.2.jar",
			JERSEY_MIN_PATH + "jersey-min-2.27.jar"
	};

	private static final String OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH = "osgi-jax-rs-connector/";

	private static final String[] OSGI_JAX_RS_CONNECTOR_BUNDLES = {
			//OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH + "javax.servlet-api-3.1.0.jar",
			
			//OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH + "consumer-5.3.jar",
			OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH + "publisher-5.3.jar",
			OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH + "provider-gson-2.3.jar",
	};

	private static final String[][] allBundles = {
			//PAX_JETTY_BUNDLES,
			//PAX_HTTP_BUNDLES,
			//KARAF_SCR_BUNDLES,
			//KARAF_HTTP_BUNDLES,
			JERSEY_MIN_BUNDLES,
			
			
			OSGI_JAX_RS_CONNECTOR_BUNDLES
	};



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
	public void testResourcesExist() 
	{	
		for (String[] resourceGroup : allBundles) {
			for (String resource : resourceGroup) {
				File f = new File("./src/main/resources/" + resource);
				assertTrue("File does not exist: " + f.getAbsolutePath(), f.exists());
			}
		}
	}

	@Test
	public void testBundleInstallation() throws Exception {
		
		OSGiJAXRSManager manager = new OSGiJAXRSManager();
		FeaturesService featuresService = mock(FeaturesService.class);
		manager.installOSGiJAXRSBundles(bc, featuresService, "6666");
		for (String[] resourceGroup : allBundles) {
			for (String resource : resourceGroup) {
				verify(bundle).getResource(resource);
			}
		}
	}

	@Test 
	public void testBundleUninstallation() throws Exception {
		OSGiJAXRSManager manager = new OSGiJAXRSManager();	
		FeaturesService featuresService = mock(FeaturesService.class);
	
		final List<Bundle> bundleList = new ArrayList<Bundle>();
		
		doAnswer( new Answer<Bundle>() {
		    public Bundle answer(InvocationOnMock invocation) {
		    	Bundle bundle = mock(Bundle.class);
		    	when(bundle.getSymbolicName()).thenReturn(invocation.getArguments()[0].toString());
		    	bundleList.add(bundle);
		    	return bundle;
		    	}
		    }).when(bc).installBundle(anyString(), any(InputStream.class) );
		
		manager.installOSGiJAXRSBundles(bc, featuresService, "6666");
		manager.uninstallOSGiJAXRSBundles();
		
		for (Bundle bundle : bundleList) {
			System.out.println(bundle.getSymbolicName() + " uninstall");
			verify(bundle).stop();
			verify(bundle).uninstall();
		}
	}
}
