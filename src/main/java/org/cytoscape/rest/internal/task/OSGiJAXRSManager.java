package org.cytoscape.rest.internal.task;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.features.BundleInfo;
import org.apache.karaf.features.Feature;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * This class manages the installation of the OSGi JAX-RS Connector and its dependencies.
 * 
 * https://github.com/hstaudacher/osgi-jax-rs-connector
 * 
 * Unlike a standard JAR, this must be started and installed as an OSGi bundle.
 * 
 * Its dependencies are also bundles or collections of bundles called 'Features', which are included in Karaf's distribution.
 * These bundles and features are installed and started first.
 * 
 * @author David Otasek
 *
 */
public class OSGiJAXRSManager
{
	   private static final String SCHEMA_HTTP = "http";
	    private static final String SCHEMA_HTTPS = "https";
	    private static final String PROXY_HOST = "proxyHost";
	    private static final String PROXY_PORT = "proxyPort";
	    private static final String PROXY_USER = "proxyUser";
	    private static final String PROXY_PASSWORD = "proxyPassword";
	    private static final String NON_PROXY_HOSTS = "nonProxyHosts";

	
	private BundleContext context;

	private FeaturesService featuresService;
	
	private List<Bundle> bundles; 

	private String port;

	
	private static final String JERSEY_VERSION = "2.23";
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
			JERSEY_MISC_PATH + "validation-api-1.1.0.Final.jar",
			JERSEY_MISC_PATH + "javassist-3.18.1-GA.jar",
			JERSEY_MISC_PATH + "mimepull-1.9.6.jar",
	};

	private static final String OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH = "osgi-jax-rs-connector/";

	private static final String[] OSGI_JAX_RS_CONNECTOR_BUNDLES = {
			OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH + "publisher-5.3.jar",
			OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH + "provider-gson-2.3.jar",
	};

	/**
	 * Installs and starts features and individual bundles required for the OSGi JAX-RS Connector.
	 * 
	 * @param bundleContext
	 * @param featuresService
	 * @param port
	 * @throws Exception
	 */
	public void installOSGiJAXRSBundles(BundleContext bundleContext, FeaturesService featuresService, String port) throws Exception 
	{
		context = bundleContext;
		
		// There are two jetty features included in Karaf 4.2.8; we must specify the latest one.
		installFeature(context, featuresService.getFeature("jetty", "9.4.22.v20191022"));
		
		installFeature(context, featuresService.getFeature("scr"));
		installFeature(context, featuresService.getFeature("pax-web-core"));
		installFeature(context, featuresService.getFeature("pax-jetty"));
		installFeature(context, featuresService.getFeature("pax-http-jetty"));
		installFeature(context, featuresService.getFeature("pax-http"));
		installFeature(context, featuresService.getFeature("pax-http-service"));
		installFeature(context, featuresService.getFeature("http"));
		
		this.bundles = new ArrayList<Bundle>();
		this.port = port;
		setPortConfig(context);
		setRootResourceConfig(context);

		installBundlesFromResources(bundleContext, JERSEY_MISC_BUNDLES);
		installBundlesFromResources(bundleContext, HK2_BUNDLES);
		installBundlesFromResources(bundleContext, GLASSFISH_JERSEY_BUNDLES);
		
		// These are the main OSGi JAX-RS Connector bundles.
		installBundlesFromResources(bundleContext, OSGI_JAX_RS_CONNECTOR_BUNDLES);

	}

	/**
	 * This function installs a standard Karaf Feature, a collection of bundles defined in the Karaf framework in this file:
	 * 
	 * cytoscape\framework\system\org\apache\karaf\features\standard\4.2.8\standard-4.2.8-features.xml
	 * 
	 * Note that there is no requirement to use these Features; different combinations of Bundles might satisfy the same requirements 
	 * and could be installed using the installBundlesFromResources(...) method. However, Karaf maintains these features from release to
	 * release, so using them instead of composing them ourselves reduces our workload. Should we need fall back on installBundlesFromResources, 
	 * the contents of the karaf features xml file could be used to compose our own features.
	 * 
	 * @param bc
	 * @param feature 
	 * @throws BundleException
	 * @throws IOException
	 */
	private void installFeature(BundleContext bc, Feature feature) throws BundleException, IOException{
		if (feature == null) {
			return;
		}
		List<Bundle> bundleList = new LinkedList<Bundle>();
		List<BundleInfo> bundleInfos = feature.getBundles();
		for (BundleInfo bundle : bundleInfos) {
			bundleList.add(bc.installBundle(bundle.getLocation()));
		}
		for (Bundle bundle : bundleList) {
			bundle.start();
		}
	}
	
	/**
	 * Configures the root path for the http server using the configuration for the OSGi JAX-RS Connector
	 * 
	 * @param context
	 * @throws Exception
	 */
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

	/**
	 * Installs and starts OSGi bundles, in order, from JAR files included as resources for the CyREST bundle.
	 * 
	 * Note that the order of bundle loading is extremely important. For some bundles to work, others must already be installed and started.
	 * 
	 * @param bundleContext
	 * @param resources
	 * @throws BundleException
	 * @throws IOException
	 */
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

	/**
	 * Install an OSGi bundle included as a resource in the main CyREST bundle.
	 * 
	 * These are added in the pom.xml file using the maven-dependency-plugin
	 * 
	 * @param bundleContext
	 * @param resourceName
	 * @return
	 * @throws BundleException
	 * @throws IOException
	 */
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
