package org.cytoscape.rest.internal;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.ImageIcon;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;

public class CytoBridgeAction extends AbstractCyAction {

	//private CySwingApplication desktopApp;
	
	private boolean started = false;
	
	private ImageIcon icon, smallIcon, warn, success;
	private NetworkManager myManager;
	
	
	public CytoBridgeAction(NetworkManager myManager){
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
		
		//this.desktopApp = desktopApp;
		this.myManager = myManager;
	}
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		//show help
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
	
	public void setWarn() {
		putValue("tooltip","CytoBridge Error!");
		putValue(LARGE_ICON_KEY, warn);
	}
	
	public void setSuccess() {
		putValue("tooltip","CytoBridge Successful Data Transfer");
		putValue(LARGE_ICON_KEY, success);
	}
	
	public void setIdle() {
		putValue("tooltip","CytoBridge Ready");
		putValue(LARGE_ICON_KEY, icon);
	}
}
