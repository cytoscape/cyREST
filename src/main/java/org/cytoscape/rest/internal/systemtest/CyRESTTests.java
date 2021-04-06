package org.cytoscape.rest.internal.systemtest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.ws.rs.core.Response;

import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.ci.model.CIResponse;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.rest.internal.model.NetworkSUIDModel;
import org.cytoscape.rest.internal.model.ServerStatusModel;
import org.cytoscape.rest.internal.resource.MiscResource;
import org.cytoscape.rest.internal.resource.NetworkResource;
import org.cytoscape.rest.internal.resource.SessionResource;
import org.cytoscape.service.util.CyServiceRegistrar;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CyRESTTests {

	private static final String SAMPLE_DATA_DIR = "sampleData";

	final CyServiceRegistrar serviceRegistrar;
	final File sampleDataDirectory;
	final CyEventHelper cyEventHelper;

	final CyRESTResources resources;

	public CyRESTTests(final CyServiceRegistrar serviceRegistrar) throws Exception {
		this.serviceRegistrar = serviceRegistrar;

		this.resources = new CyRESTResources(serviceRegistrar);

		final CyApplicationConfiguration applicationCfg = serviceRegistrar.getService(CyApplicationConfiguration.class);

		if (applicationCfg != null) {
			sampleDataDirectory = new File(applicationCfg.getInstallationDirectoryLocation().getAbsolutePath()
					+ File.separatorChar + SAMPLE_DATA_DIR + File.separatorChar);
		} else {
			throw new Exception("No CyApplicationConfiguration available. Unable to run tests.");
		}

		cyEventHelper = serviceRegistrar.getService(CyEventHelper.class);
	}

	public void throwExceptionIfFalse(final String message, boolean value) throws Exception {
		if (value == false) {
			throw new Exception(message);
		}
	}

	@CyRESTTest
	public void testRoot() throws Exception {
		MiscResource miscResource = serviceRegistrar.getService(MiscResource.class);
		ServerStatusModel serverStatus = miscResource.getStatus();
		throwExceptionIfFalse("All apps should be started.", serverStatus.getAllAppsStarted() == true);
	}

	@CyRESTTest
	public void flushPayloadEvents() throws Exception {
		cyEventHelper.flushPayloadEvents();
	}

	@CyRESTTest
	public void loadGalFilteredApplyStyle() throws Exception {

		resources.sessionResource.getSessionFromFile(sampleDataDirectory.getAbsolutePath() + "/galFiltered.cys");

		cyEventHelper.flushPayloadEvents();

		Collection<Long> allNetworks = resources.networkResource.getNetworksAsSUID(null, null);

		throwExceptionIfFalse("Only one network should be loaded.", allNetworks.size() == 1);

		long network = (long) allNetworks.toArray()[0];

		Collection<Long> allNetworkViews = resources.networkViewResource.getAllNetworkViews(network);

		throwExceptionIfFalse("Only one network view should be loaded.", allNetworkViews.size() == 1);

		long networkView = (long) allNetworkViews.toArray()[0];

		final String newColumn = "{" + "\"name\": \"passthrough_col\"," + "\"type\": \"String\"}";

		InputStream columnInputStream = new ByteArrayInputStream(newColumn.getBytes());

		resources.tableResource.createColumn(network, "defaultnode", columnInputStream);
		cyEventHelper.flushPayloadEvents();
		
		String columnValues = resources.tableResource.getColumnValues(network, "defaultnode", "SUID");

		final ObjectMapper objMapper = new ObjectMapper();

		final JsonNode nodesJSON = objMapper.readValue(columnValues, JsonNode.class);

		Long firstSuid = nodesJSON.get("values").get(0).asLong();

		String backgroundColor = resources.networkViewResource.getSingleVisualPropertyValue(network, networkView,
				"nodes", firstSuid, "NODE_FILL_COLOR");

		final JsonNode colorJSON = objMapper.readValue(backgroundColor, JsonNode.class);

		String initialColor = colorJSON.get("value").asText();

		String tableUpdateBody = "{\r\n" + "  \"key\": \"SUID\",\r\n" + "  \"dataKey\": \"SUID\",\r\n"
				+ "  \"data\": [\r\n" + "    {\r\n" + "\"SUID\": 513,\r\n" + "\"passthrough_col\" : \"#FF0000\"\r\n"
				+ "}]}";

		InputStream tableInputStream = new ByteArrayInputStream(tableUpdateBody.getBytes());

		resources.tableResource.updateTable(network, "defaultnode", null, tableInputStream);
		cyEventHelper.flushPayloadEvents();
		
		String passthroughMappingBody = "[{ \"mappingType\": \"passthrough\",\r\n"
				+ "  \"mappingColumn\": \"passthrough_col\",\r\n"
				+ "  \"mappingColumnType\": \"String\",\r\n"
				+ "  \"visualProperty\": \"NODE_FILL_COLOR\" \r\n"
				+ "}]";
		
		InputStream passthroughMappingStream = new ByteArrayInputStream(passthroughMappingBody.getBytes());
		
		resources.styleResource.updateMapping("galFiltered Style", "NODE_FILL_COLOR", passthroughMappingStream);
		cyEventHelper.flushPayloadEvents();
		
		String newColorResponse = resources.networkViewResource.getSingleVisualPropertyValue(network, networkView,
				"nodes", firstSuid, "NODE_FILL_COLOR");
		
		final JsonNode newColorJSON = objMapper.readValue(newColorResponse, JsonNode.class);

		String newColor = newColorJSON.get("value").asText();
		
		throwExceptionIfFalse("New color should be #FF0000 and is " + newColor, "#FF0000".equals(newColor));
	}

	public static void main(String[] args) {
		String vp = "{\r\n" + "			  \"visualProperty\" : \"NODE_FILL_COLOR\",\r\n"
				+ "			  \"value\" : \"#F3F8FD\"\r\n" + "			}";

		final ObjectMapper objMapper = new ObjectMapper();

		JsonNode rootNode;
		try {
			rootNode = objMapper.readValue(vp, JsonNode.class);
			System.out.println(rootNode.get("value").asText());

		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
