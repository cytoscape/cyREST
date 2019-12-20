package org.cytoscape.rest.internal;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.service.util.CyServiceRegistrar;

public class CyAutomationAction extends AbstractCyAction{

	private final CyServiceRegistrar serviceRegistrar;
	
	public CyAutomationAction(CyServiceRegistrar serviceRegistrar) {
		super("Automation Examples");
		this.setPreferredMenu(CyRESTConstants.CY_REST_HELP_MENU_ANCHOR);
		this.serviceRegistrar = serviceRegistrar;
	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	
		try {
			DesktopBrowseUtil.browse("http://automation.cytoscape.org");
		} catch (IOException | URISyntaxException e1) {
			e1.printStackTrace();
		}
	}

	
}
