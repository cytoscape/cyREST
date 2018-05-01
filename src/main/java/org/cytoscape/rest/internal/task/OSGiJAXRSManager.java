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
			//JERSEY_MISC_PATH + "javax.annotation-api-1.2.jar",
			JERSEY_MISC_PATH + "validation-api-1.1.0.Final.jar",
			JERSEY_MISC_PATH + "javassist-3.18.1-GA.jar",
			JERSEY_MISC_PATH + "mimepull-1.9.6.jar",
	};

	private static final String OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH = "osgi-jax-rs-connector/";

	private static final String[] OSGI_JAX_RS_CONNECTOR_BUNDLES = {
			//OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH + "javax.servlet-api-3.1.0.jar",
			//OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH + "jersey-min-2.22.1.jar",
			//OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH + "consumer-5.3.jar",
			OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH + "publisher-5.3.jar",
			//OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH + "gson-2.3.jar",
			OSGI_JAX_RS_CONNECTOR_BUNDLES_PATH + "provider-gson-2.3.jar",
	};

	public void installOSGiJAXRSBundles(BundleContext bundleContext, FeaturesService featuresService, String port) throws Exception 
	{
		this.featuresService = featuresService;
		context = bundleContext;
		
	/*
		String proxyHost = System.getProperty(SCHEMA_HTTPS + "." + PROXY_HOST);
		if (proxyHost == null || proxyHost.length() == 0) {
	        System.clearProperty(SCHEMA_HTTPS + "." + PROXY_HOST);
	    }
		
		String schema = (proxyHost != null) ? SCHEMA_HTTPS : SCHEMA_HTTP;
        if (proxyHost == null) {
            proxyHost = System.getProperty(schema + "." + PROXY_HOST);
        }
       
        if (proxyHost == null || proxyHost.length() == 0) {
        	System.clearProperty(schema + "." + PROXY_HOST);
        }
        proxyHost = System.getProperty(SCHEMA_HTTPS + "." + PROXY_HOST);
        
    	System.out.println("Proxy Host: " + proxyHost);
    	System.out.println("Schema: " + schema);
    	
		String proxyUser = System.getProperty(schema + "." + PROXY_USER);
	    String proxyPassword = System.getProperty(schema + "." + PROXY_PASSWORD);
	    String proxyPort = System.getProperty(schema + "." + PROXY_PORT, "8080");
	    String nonProxyHosts = System.getProperty(schema + "." + NON_PROXY_HOSTS);
	
		System.out.println("Proxy User: " + proxyUser);
		System.out.println("Proxy Password: " + proxyPassword);
		System.out.println("Proxy Port: " + proxyPort);
		System.out.println("Non Proxy Hosts: " + nonProxyHosts);
		*/
		//System.out.println("Installing Jetty");
		installFeature(context, featuresService.getFeature("jetty", "9.4.6.v20170531"));
		
		//featuresService.installFeature("jetty", "9.4.6.v20170531");
		//System.out.println("Installed Jetty");
		
		installFeature(context, featuresService.getFeature("scr"));
		//System.out.println("Installed SCR");
		
		installFeature(context, featuresService.getFeature("pax-web-core"));
		//System.out.println("Installed PAX Core");
		
		installFeature(context, featuresService.getFeature("pax-jetty"));
		//System.out.println("Installed PAX Jetty");
		
		installFeature(context, featuresService.getFeature("pax-http-jetty"));
		//System.out.println("Installed PAX HTTP Jetty");
		
		installFeature(context, featuresService.getFeature("pax-http"));
		//System.out.println("Installed PAX HTTP");
		
		installFeature(context, featuresService.getFeature("pax-http-service"));
		//System.out.println("Installed PAX HTTP Service");
		
		installFeature(context, featuresService.getFeature("http"));
		//System.out.println("Installed HTTP");
				
		
		this.bundles = new ArrayList<Bundle>();
		this.port = port;
		setPortConfig(context);

		setRootResourceConfig(context);

		installBundlesFromResources(bundleContext, JERSEY_MISC_BUNDLES);
		installBundlesFromResources(bundleContext, HK2_BUNDLES);
		installBundlesFromResources(bundleContext, GLASSFISH_JERSEY_BUNDLES);
		installBundlesFromResources(bundleContext, OSGI_JAX_RS_CONNECTOR_BUNDLES);

	}

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
			//System.out.println("Installing bundle " + bundle.getSymbolicName() + " from feature " + feature.getName());
			bundle.start();
		}
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
