package org.cytoscape.rest.internal.systemtest;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.rest.internal.CyRESTConstants;
import org.cytoscape.rest.internal.resource.RootResource;
import org.cytoscape.rest.internal.resource.SessionResource;
import org.cytoscape.service.util.CyServiceRegistrar;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CyRESTTestAction extends AbstractCyAction {

	private final CyServiceRegistrar serviceRegistrar;

	public CyRESTTestAction(CyServiceRegistrar serviceRegistrar) {
		super("Run CyREST system tests");
		this.setPreferredMenu(CyRESTConstants.CY_REST_HELP_MENU_ANCHOR);
		this.serviceRegistrar = serviceRegistrar;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Thread thread = new Thread(() -> {
			for (int i = 0; i < 1; i++) {
				runTests();
			}
		});
		thread.start();
	}

	private void runTests() {
		CyRESTTests cyRESTTests;
		try {
			cyRESTTests = new CyRESTTests(serviceRegistrar);
		} catch (Exception e2) {
			e2.printStackTrace();
			return;
		}

		LinkedHashMap<Method, Boolean> testsRun = new LinkedHashMap<Method, Boolean>();
		Method[] methods = CyRESTTests.class.getMethods();
		for (Method method : methods) {
			if (method.getDeclaredAnnotationsByType(CyRESTTest.class).length > 0) {
				System.out.println("Running: " + method.getName() + " ... ");
				try {
					method.invoke(cyRESTTests);
					testsRun.put(method, true);
				} catch (InvocationTargetException ex) {
					ex.getCause().printStackTrace();
					testsRun.put(method, false);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				afterEach();
			}
		}

		int failed = 0;
		int passed = 0;
		for (Map.Entry<Method, Boolean> entry : testsRun.entrySet()) {
			System.out.println(entry.getKey().getName() + ":\t" + (entry.getValue() ? "PASSED" : "FAILED"));
			failed += !entry.getValue() ? 1 : 0;
			passed += entry.getValue() ? 1 : 0;
		}
		
		System.out.println("Tests completed:\t" + testsRun.size());
		System.out.println("Failed:\t" + failed);
		System.out.println("Passed:\t" + passed);
		
	}

	private void afterEach() {
		SessionResource sessionResource = serviceRegistrar.getService(SessionResource.class);
		sessionResource.deleteSession();
		Runtime.getRuntime().gc();
	}
}
