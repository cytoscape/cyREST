package org.cytoscape.rest.internal;

import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;

public class CytoBridgeAction extends AbstractCyAction {

	private CySwingApplication desktopApp;
	
	private ImageIcon icon, smallIcon, warn, success;
	private NetworkManager myManager;
	private String message = "Cytobridge is currently ready.";
	
	public CytoBridgeAction(CySwingApplication desktopApp, NetworkManager myManager){
		// Add a menu item -- Plugins->sample03
		super("CytoBridge");
		setPreferredMenu("Apps");

		icon = new ImageIcon(getClass().getResource("/images/bridgeIdle.png"));
		smallIcon = new ImageIcon(getClass().getResource("/images/bridge_small.png"));
		warn = new ImageIcon(getClass().getResource("/images/bridgeWarn.png"));
		success = new ImageIcon(getClass().getResource("/images/bridgeSuccess.png"));
				
				
		// Add image icons on tool-bar and menu item
		putValue(LARGE_ICON_KEY, icon);
		putValue(SMALL_ICON, smallIcon);
		
		this.desktopApp = desktopApp;
		this.myManager = myManager;
	}
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(this.desktopApp.getJFrame(), message, "CytoBridge", 
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	/**
	 * Make sure the plugin shows up in the Cytoscape toolbar.
	 * @return  True, since it should show up.
	 */
	public boolean isInToolBar() {
		return true;
	}

	/**
	 * Make sure the plugin shows up in the Cytoscape menubar.
	 * @return  True, since it should show up.
	 */
	public boolean isInMenuBar() {
		return true;
	}
	
	public void setWarn(String message) {
		this.message = "ERROR: "+message;
		putValue(LARGE_ICON_KEY, warn);
	}
	
	public void setSuccess(String message) {
		this.message = message;
		putValue(LARGE_ICON_KEY, success);
	}
	
	public void setIdle() {
		message = "Cytobridge is currently ready.";
		putValue(LARGE_ICON_KEY, icon);
	}
}
