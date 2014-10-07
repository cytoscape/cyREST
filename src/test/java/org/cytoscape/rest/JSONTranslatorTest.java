package org.cytoscape.rest;

import static org.junit.Assert.assertNotNull;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.After;
import org.junit.Before;

public abstract class JSONTranslatorTest {

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

}
