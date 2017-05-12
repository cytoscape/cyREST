package org.cytoscape.rest.internal;

import java.awt.event.ActionEvent;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.rest.internal.task.ResourceManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.util.swing.OpenBrowser;

public abstract class CyRESTSwaggerAction extends AbstractCyAction{

	private final CyServiceRegistrar serviceRegistrar;
	
	private final String cyRESTPort;
	
	public CyRESTSwaggerAction(String name, CyServiceRegistrar serviceRegistrar, String cyRESTPort) {
		super(name);
		this.setPreferredMenu(CyRESTConstants.cyRESTHelpMenu);
		this.serviceRegistrar = serviceRegistrar;
		this.cyRESTPort = cyRESTPort;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final OpenBrowser openBrowser = serviceRegistrar.getService(OpenBrowser.class);
		try {
			openBrowser.openURL(rootURL() + "?url=" + URLEncoder.encode("http://" + ResourceManager.HOST + ":" + this.cyRESTPort + "/" + swaggerPath(), "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	}

	protected abstract String rootURL();
	
	protected abstract String swaggerPath();
	
	protected final String getCyRESTPort(){
		return cyRESTPort;
	}
	
}
