package org.cytoscape.rest.internal;

import org.cytoscape.model.CyEdge;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.events.AddedEdgeViewsEvent;
import org.cytoscape.view.model.events.AddedEdgeViewsListener;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;

public class EdgeViewListener implements AddedEdgeViewsListener {

	private VisualMappingManager visMan;
	
	public EdgeViewListener(VisualMappingManager visMan) {
		this.visMan = visMan;
	}
	
	public void handleEvent(AddedEdgeViewsEvent e) {
		//update layout automatically (of new nodes)
		CyNetworkView networkView = e.getSource();
		VisualStyle style = visMan.getVisualStyle(networkView);
		for (View<CyEdge> view : e.getEdgeViews()) {
			style.apply(networkView.getModel().getRow(view.getModel()), networkView.getEdgeView(view.getModel()));
		}
		networkView.updateView();
	}

}