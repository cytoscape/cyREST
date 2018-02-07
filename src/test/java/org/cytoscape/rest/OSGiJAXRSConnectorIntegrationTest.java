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
	private static final String PAX_JETTY_PATH = "pax-jetty/";

	private static final String[] PAX_JETTY_BUNDLES = {
			PAX_JETTY_PATH + "org.apache.servicemix.specs.activation-api-1.1-2.2.0.jar",
			PAX_JETTY_PATH + "geronimo-servlet_3.0_spec-1.0.jar",
			PAX_JETTY_PATH + "mail-1.4.4.jar",
			PAX_JETTY_PATH + "geronimo-jta_1.1_spec-1.1.1.jar",
			PAX_JETTY_PATH + "geronimo-annotation_1.1_spec-1.0.1.jar",
			PAX_JETTY_PATH + "geronimo-jaspic_1.0_spec-1.1.jar",
			PAX_JETTY_PATH + "asm-all-5.0.2.jar",
			PAX_JETTY_PATH + "jetty-all-server-8.1.15.v20140411.jar"
	};

	private static final String PAX_HTTP_PATH = "pax-http/";

	private static final String[] PAX_HTTP_BUNDLES = {
			PAX_HTTP_PATH + "ops4j-base-lang-1.4.0.jar",
			PAX_HTTP_PATH + "pax-swissbox-core-1.7.0.jar",
			PAX_HTTP_PATH + "xbean-bundleutils-3.18.jar",
			PAX_HTTP_PATH + "xbean-reflect-3.18.jar",
			PAX_HTTP_PATH + "xbean-finder-3.18.jar",
			PAX_HTTP_PATH + "pax-web-api-3.1.4.jar",
			PAX_HTTP_PATH + "pax-web-spi-3.1.4.jar",
			PAX_HTTP_PATH + "pax-web-runtime-3.1.4.jar",
			PAX_HTTP_PATH + "pax-web-jetty-3.1.4.jar"
	};

	private static final String KARAF_SCR_PATH = "karaf-scr/";

	private static final String[] KARAF_SCR_BUNDLES = {
			KARAF_SCR_PATH + "org.apache.felix.scr-2.0.12.jar"
	};

	private static final String KARAF_HTTP_PATH = "karaf-http/";

	private static final String[] KARAF_HTTP_BUNDLES = {
			KARAF_HTTP_PATH + "org.apache.karaf.http.core-4.2.0-SNAPSHOT.jar"
	};

	private static final String HK2_PATH = "hk2/";

	private static final String[] HK2_BUNDLES = {
			HK2_PATH + "hk2-api-2.4.0.jar",
			HK2_PATH + "hk2-locator-2.4.0.jar",
			HK2_PATH + "hk2-utils-2.4.0.jar",
			HK2_PATH + "osgi-resource-locator-1.0.1.jar",
			HK2_PATH + "javax.inject-2.4.0.jar",
			HK2_PATH + "aopalliance-repackaged-2.4.0.jar",

	};

	private static final String GLASSFISH_JERSEY_PATH = "glassfish-jersey/";

	private static final String[] GLASSFISH_JERSEY_BUNDLES = {
			GLASSFISH_JERSEY_PATH + "jersey-container-servlet-2.23.jar",
			GLASSFISH_JERSEY_PATH + "jersey-media-sse-2.23.jar",
			GLASSFISH_JERSEY_PATH + "jersey-media-multipart-2.23.jar",
			GLASSFISH_JERSEY_PATH + "jersey-container-servlet-core-2.23.jar",
			GLASSFISH_JERSEY_PATH + "jersey-common-2.23.jar",
			GLASSFISH_JERSEY_PATH + "jersey-guava-2.23.jar",
			GLASSFISH_JERSEY_PATH + "jersey-server-2.23.jar",
			GLASSFISH_JERSEY_PATH + "jersey-client-2.23.jar",
			GLASSFISH_JERSEY_PATH + "jersey-media-jaxb-2.23.jar"

	};

	private static final String JERSEY_MISC_PATH = "jersey-misc/";

	private static final String[] JERSEY_MISC_BUNDLES = {
			//JERSEY_MISC_PATH + "javax.annotation-api-1.2.jar",
			JERSEY_MISC_PATH + "validation-api-1.1.0.Final.jar",
			JERSEY_MISC_PATH + "javassist-3.18.1-GA.jar",
			JERSEY_MISC_PATH + "mimepull-1.9.6.jar",
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
			JERSEY_MISC_BUNDLES,
			HK2_BUNDLES,
			GLASSFISH_JERSEY_BUNDLES,
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
		manager.installOSGiJAXRSBundles(bc, "6666");
		for (String[] resourceGroup : allBundles) {
			for (String resource : resourceGroup) {
				verify(bundle).getResource(resource);
			}
		}
	}

	@Test 
	public void testBundleUninstallation() throws Exception {
		OSGiJAXRSManager manager = new OSGiJAXRSManager();	
		final List<Bundle> bundleList = new ArrayList<Bundle>();
		
		doAnswer( new Answer<Bundle>() {
		    public Bundle answer(InvocationOnMock invocation) {
		    	Bundle bundle = mock(Bundle.class);
		    	when(bundle.getSymbolicName()).thenReturn(invocation.getArguments()[0].toString());
		    	bundleList.add(bundle);
		    	return bundle;
		    	}
		    }).when(bc).installBundle(anyString(), any(InputStream.class) );
		manager.installOSGiJAXRSBundles(bc, "6666");
		manager.uninstallOSGiJAXRSBundles();
		
		for (Bundle bundle : bundleList) {
			System.out.println(bundle.getSymbolicName() + " uninstall");
			verify(bundle).stop();
			verify(bundle).uninstall();
		}
	}
}
