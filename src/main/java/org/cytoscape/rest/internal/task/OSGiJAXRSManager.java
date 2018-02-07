package org.cytoscape.rest.internal.task;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class OSGiJAXRSManager
{
	private BundleContext context;

	private List<Bundle> bundles; 

	private String port;

	private static final String JERSEY_VERSION = "2.23";
	
	private static final String PAX_JETTY_PATH = "pax-jetty/";

	//Bundles should stay in this order.
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
			// Already included in pax-jetty
			// PAX_HTTP_PATH + "asm-all-5.0.2.jar",
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
			KARAF_SCR_PATH + "org.apache.felix.metatype-1.0.10.jar",
			KARAF_SCR_PATH + "org.apache.felix.scr-1.8.2.jar",
			KARAF_SCR_PATH + "org.apache.karaf.scr.command-3.0.3.jar"
	};

	private static final String KARAF_HTTP_PATH = "karaf-http/";

	private static final String[] KARAF_HTTP_BUNDLES = {
			KARAF_HTTP_PATH + "org.apache.karaf.http.core-3.0.3.jar",
			KARAF_HTTP_PATH + "org.apache.karaf.http.command-3.0.3.jar"
	};

	private static final String HK2_PATH = "hk2/";

	private static final String[] HK2_BUNDLES = {
			HK2_PATH + "hk2-api-2.4.0-b34.jar",
			HK2_PATH + "hk2-locator-2.4.0-b34.jar",
			HK2_PATH + "hk2-utils-2.4.0-b34.jar",
			HK2_PATH + "osgi-resource-locator-1.0.1.jar",
			HK2_PATH + "javax.inject-2.4.0-b34.jar",
			HK2_PATH + "aopalliance-repackaged-2.4.0-b34.jar",

	};

	private static final String GLASSFISH_JERSEY_PATH = "glassfish-jersey/";

	private static final String[] GLASSFISH_JERSEY_BUNDLES = {
			GLASSFISH_JERSEY_PATH + "jersey-container-servlet-" + JERSEY_VERSION +".jar",
			GLASSFISH_JERSEY_PATH + "jersey-media-sse-" + JERSEY_VERSION +".jar",
			GLASSFISH_JERSEY_PATH + "jersey-media-multipart-" + JERSEY_VERSION +".jar",
			GLASSFISH_JERSEY_PATH + "jersey-container-servlet-core-" + JERSEY_VERSION +".jar",
			GLASSFISH_JERSEY_PATH + "jersey-common-"+ JERSEY_VERSION +".jar",
			GLASSFISH_JERSEY_PATH + "jersey-guava-"+ JERSEY_VERSION +".jar",
			GLASSFISH_JERSEY_PATH + "jersey-server-"+ JERSEY_VERSION +".jar",
			GLASSFISH_JERSEY_PATH + "jersey-client-"+ JERSEY_VERSION +".jar",
			GLASSFISH_JERSEY_PATH + "jersey-media-jaxb-"+ JERSEY_VERSION +".jar"

	};

	private static final String JERSEY_MISC_PATH = "jersey-misc/";

	private static final String[] JERSEY_MISC_BUNDLES = {
			JERSEY_MISC_PATH + "javax.annotation-api-1.2.jar",
			JERSEY_MISC_PATH + "validation-api-1.1.0.Final.jar",
			JERSEY_MISC_PATH + "javassist-3.18.1-GA.jar",
			JERSEY_MISC_PATH + "mimepull-1.9.6.jar",
	};

	private static final String OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH = "osgi-jax-rs-connector/";

	private static final String[] OSGI_JAX_RS_CONNECTOR_BUNDLES = {
			//OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH + "javax.servlet-api-3.1.0.jar",
			//OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH + "jersey-min-2.22.1.jar",
			OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH + "consumer-5.3.jar",
			OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH + "publisher-5.3.jar",
			//OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH + "gson-2.3.jar",
			OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH + "provider-gson-2.2.jar",
	};

	public void installOSGiJAXRSBundles(BundleContext bundleContext, String port) throws Exception 
	{
		context = bundleContext;

		this.bundles = new ArrayList<Bundle>();
		this.port = port;
		setPortConfig(context);

		installBundlesFromResources(bundleContext, PAX_JETTY_BUNDLES);
		installBundlesFromResources(bundleContext, PAX_HTTP_BUNDLES);
		installBundlesFromResources(bundleContext, KARAF_SCR_BUNDLES);
		installBundlesFromResources(bundleContext, KARAF_HTTP_BUNDLES);

		setRootResourceConfig(context);

		installBundlesFromResources(bundleContext, JERSEY_MISC_BUNDLES);
		installBundlesFromResources(bundleContext, HK2_BUNDLES);
		installBundlesFromResources(bundleContext, GLASSFISH_JERSEY_BUNDLES);
		installBundlesFromResources(bundleContext, OSGI_JAX_RS_CONNECTOR_BUNDLES);

	}

	private void setRootResourceConfig(BundleContext context) throws Exception
	{
		ServiceReference configurationAdminReference = 
				context.getServiceReference(ConfigurationAdmin.class.getName());

		if (configurationAdminReference != null) 
		{  
			ConfigurationAdmin configurationAdmin = (ConfigurationAdmin) context.getService(configurationAdminReference);

			Configuration config = configurationAdmin.getConfiguration("com.eclipsesource.jaxrs.connector", null);

			Dictionary<String, Object> dictionary = new Hashtable<String, Object>();
			dictionary.put("root", "/*");

			config.update(dictionary);
			context.ungetService(configurationAdminReference);
		}
		else{
			throw new IllegalStateException("No available ConfigurationAdmin service.");
		}
	}

	/**
	 * Set the port the CyREST service will be listening on.
	 * 
	 * @param context
	 * @throws Exception
	 */
	private void setPortConfig(BundleContext context) throws Exception
	{
		ServiceReference configurationAdminReference = 
				context.getServiceReference(ConfigurationAdmin.class.getName());

		if (configurationAdminReference != null) 
		{  
			ConfigurationAdmin configurationAdmin = (ConfigurationAdmin) context.getService(configurationAdminReference);

			Configuration config = configurationAdmin.getConfiguration("org.ops4j.pax.web", null);

			Dictionary<String, Object> dictionary = new Hashtable<String, Object>();
			dictionary.put("org.osgi.service.http.port", port);
			//Set session timeout to infinite (while Cytoscape is running)
			dictionary.put("org.ops4j.pax.web.session.timeout", "0");

			config.update(dictionary);

			context.ungetService(configurationAdminReference);
		}
		else
		{
			throw new IllegalStateException("No available ConfigurationAdmin service.");
		}
	}

	private void installBundlesFromResources(BundleContext bundleContext, final String[] resources) throws BundleException, IOException
	{	
		List<Bundle> bundleList = new LinkedList<Bundle>();
		for (String bundle : resources)
		{
			bundleList.add(installBundleFromResource(bundleContext, bundle));
		}

		for (Bundle bundle :bundleList)
		{
			if (bundle != null) {
				bundle.start();
				this.bundles.add(bundle);
			}
		}
	}

	private Bundle installBundleFromResource(BundleContext bundleContext, String resourceName) throws BundleException, IOException
	{
		Bundle bundle = null;

		URL url = bundleContext.getBundle().getResource(resourceName);
		bundle = bundleContext.installBundle(url.toString() ,url.openConnection().getInputStream());

		return bundle;
	}

	public void uninstallOSGiJAXRSBundles() throws BundleException
	{
		if (bundles != null) {
			for (Bundle bundle : bundles){
				bundle.stop();
				bundle.uninstall();
			}
		}
	}
}
