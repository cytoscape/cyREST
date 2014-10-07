package org.cytoscape.rest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.NetworkTestSupport;
import org.cytoscape.rest.internal.serializer.TableModule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TableModuleTest {

	private ObjectMapper tableObjectMapper;
	private final NetworkTestSupport testSupport = new NetworkTestSupport();

	protected ScriptEngine jsEngine;
	protected Bindings bindings;

	@Before
	public void setUp() throws Exception {
		final ScriptEngineManager manager = new ScriptEngineManager();
		jsEngine = manager.getEngineByExtension("js");
		
		assertNotNull(jsEngine);
		
		bindings = jsEngine.createBindings();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTableSerializer() throws Exception {
		this.tableObjectMapper = new ObjectMapper();
		this.tableObjectMapper.registerModule(new TableModule());
		
		final CyNetwork network = buildNetwork();
		final Collection<CyTable> tables = new ArrayList<CyTable>();
		tables.add(network.getDefaultNetworkTable());
		
		final String serializedTable = tableObjectMapper.writeValueAsString(tables);
		assertNotNull(serializedTable);
		
		System.out.println(serializedTable);
		
		assertTrue(serializedTable.contains("\"description\""));
		
		// parse JSON with JS Engine
		parseJsonInJavaScript(serializedTable);
		
	}
	
	private final CyNetwork buildNetwork() {
		
		final CyNetwork network = testSupport.getNetwork();
		network.getRow(network).set(CyNetwork.NAME, "network1");
		network.getDefaultNetworkTable().createColumn("description", String.class, false);
		network.getRow(network).set("description", "this is a test");
		
		// final CyNode node1 = network.addNode();
		// final CyNode node2 = network.addNode();
		// final CyNode node3 = network.addNode();
		// final CyEdge edge = network.addEdge(node1, node2, true);
		
		return network;
	}


	private final void parseJsonInJavaScript(final String result) throws Exception {
		bindings.put("result", result);
		
		// This is the actual test cases written in JavaScript.
		final Reader scriptReader = new FileReader("./src/test/resources/table_test.js");
		
		final Object jsResult = jsEngine.eval(scriptReader, bindings);
		scriptReader.close();
		
		assertNotNull(jsResult);
		assertTrue(jsResult instanceof Boolean);
		assertTrue((Boolean)jsResult);
	}
}