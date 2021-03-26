package org.cytoscape.rest.internal.integrationtest;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.rest.internal.CyRESTConstants;
import org.cytoscape.rest.internal.resource.RootResource;
import org.cytoscape.rest.internal.resource.SessionResource;
import org.cytoscape.service.util.CyServiceRegistrar;

import java.util.List;
import java.util.ArrayList;

public class CyRESTTestAction extends AbstractCyAction{

	private final CyServiceRegistrar serviceRegistrar;
	
	public CyRESTTestAction(CyServiceRegistrar serviceRegistrar) {
		super("Run CyREST integration tests");
		this.setPreferredMenu(CyRESTConstants.CY_REST_HELP_MENU_ANCHOR);
		this.serviceRegistrar = serviceRegistrar;
	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		CyRESTTests cyRESTTests;
		try {
			cyRESTTests = new CyRESTTests(serviceRegistrar);
		} catch (Exception e2) {
			e2.printStackTrace();
			return;
		}
		
		long totalTestsRun = 0;
		List<Method> failedTests = new ArrayList<Method>();
		
		Method[] methods = CyRESTTests.class.getMethods();
		for (Method method : methods) {
			if (method.getDeclaredAnnotationsByType(CyRESTTest.class).length > 0) {
				System.out.print("Running: " + method.getName() + " ... ");
				try {
					method.invoke(cyRESTTests);
					System.out.println("PASS");
				} catch (InvocationTargetException ex) {
					System.out.println("");
					ex.getCause().printStackTrace();
					failedTests.add(method);
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				} catch (IllegalArgumentException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					totalTestsRun++;
				}
				afterEach();
			}
		}
		
		System.out.println("Tests completed: " + totalTestsRun);
		System.out.println("Failures: " + failedTests.size());
		for (Method method : failedTests) {
			System.out.println(method.getName() + " Failed");
		}
	}	
	
	private void afterEach() {
		SessionResource sessionResource = serviceRegistrar.getService(SessionResource.class);
		sessionResource.deleteSession();
		Runtime.getRuntime().gc();
	}
}
