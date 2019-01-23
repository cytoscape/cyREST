package org.cytoscape.rest.internal;

import java.awt.event.ActionEvent;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.util.swing.OpenBrowser;

public class CyAutomationAction extends AbstractCyAction{

	private final CyServiceRegistrar serviceRegistrar;
	
	public CyAutomationAction(CyServiceRegistrar serviceRegistrar) {
		super("Automation Examples");
		this.setPreferredMenu(CyRESTConstants.CY_REST_HELP_MENU_ANCHOR);
		this.serviceRegistrar = serviceRegistrar;
	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final OpenBrowser openBrowser = serviceRegistrar.getService(OpenBrowser.class);
		openBrowser.openURL("http://automation.cytoscape.org");
	}

	
}
