package org.cytoscape.rest.internal.service;

import java.util.Map;
import java.util.Properties;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Singleton
@Path("/v1")
public class MiscDataService extends AbstractDataService {

	private static final Integer MB = 1024 * 1024;
	
	private static final String[] PRESET_PROPS = {
		"java.version", "os.name", "os.version", "karaf.version"
	};

	/**
	 * Return the Cytoscape and API version.
	 * 
	 * @return
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getStatus() {
		final StringBuilder builder = new StringBuilder();

		
		builder.append("{");
		builder.append("\"server\":\"Cytoscape RESTful API version " + API_VERSION + "\",");
		builder.append("\"processors\":" + Runtime.getRuntime().availableProcessors() + ",");

		appendEnv(builder);
		appendMemoryStatus(builder);

		builder.append("}");
		return builder.toString();
	}




	@GET
	@Path("/gc")
	@Produces(MediaType.APPLICATION_JSON)
	public String runGarbageCollection() {
		final StringBuilder builder = new StringBuilder();

		// Force to run GC
		Runtime.getRuntime().gc();
		
		builder.append("{");
		appendMemoryStatus(builder);
		builder.append("}");
		return builder.toString();
	}


	private final void appendEnv(final StringBuilder builder) {
		final Map<Object, Object> envVariables = System.getProperties();
		for(String key:PRESET_PROPS)
			builder.append("\"" + key + "\":\"" + envVariables.get(key).toString() + "\",");
	}


	private final void appendMemoryStatus(final StringBuilder builder) {
		final Runtime runtime = Runtime.getRuntime();
		final Long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / MB;
		builder.append("\"usedMemoryMB\":" + usedMemory + ",");
		final Long freeMemory = runtime.freeMemory() / MB;
		builder.append("\"freeMemoryMB\":" + freeMemory + ",");
		final Long totalMemory = runtime.totalMemory() / MB;
		builder.append("\"totalMemoryMB\":" + totalMemory + ",");
		final Long maxMemory = runtime.maxMemory() / MB;
		builder.append("\"maxMemoryMB\":" + maxMemory);
	}
	
	
	/**
	 * Return the Cytoscape and API version.
	 * 
	 * @return
	 */
	@GET
	@Path("/version")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCytoscapeVersion() {

		if (props == null) {
			throw new InternalServerErrorException("Could not find CyProperty object.");
		}

		final Properties property = (Properties) this.props.getProperties();
		final Object versionNumber = property.get("cytoscape.version.number");
		if (versionNumber != null) {

			return "{ \"apiVersion\":\"" + API_VERSION + "\",\"cytoscapeVersion\": \"" + versionNumber.toString()
					+ "\"}";
		} else {
			throw new NotFoundException("Could not find Cytoscape version number property.");
		}
	}
}
