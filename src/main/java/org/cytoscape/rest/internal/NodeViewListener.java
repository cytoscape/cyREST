package org.cytoscape.rest.internal;

import java.util.HashSet;
import java.util.Properties;

import org.cytoscape.model.CyNode;
import org.cytoscape.property.CyProperty;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.events.AddedNodeViewsEvent;
import org.cytoscape.view.model.events.AddedNodeViewsListener;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

public class NodeViewListener implements AddedNodeViewsListener {

	private static final String DEF_LAYOUT = "force-directed";
	
	private VisualMappingManager visMan;
	private CyLayoutAlgorithmManager layMan;
	private TaskManager tm;
	private Properties props;
	
	public NodeViewListener(VisualMappingManager visMan, CyLayoutAlgorithmManager layMan, TaskManager tm,
							CyProperty<Properties> cyPropertyServiceRef) {
		this.visMan = visMan;
		this.layMan = layMan;
		this.tm = tm;
		this.props = cyPropertyServiceRef.getProperties();
	}
	
	public void handleEvent(AddedNodeViewsEvent e) {
		//update layout automatically (of new nodes)
		CyNetworkView networkView = e.getSource();
		VisualStyle style = visMan.getVisualStyle(networkView);
		for (View<CyNode> v : e.getNodeViews()) {
			style.apply(networkView.getModel().getRow(v.getModel()), networkView.getNodeView(v.getModel()));
		}
		
		String pref = CyLayoutAlgorithmManager.DEFAULT_LAYOUT_NAME;/*
		if (props != null)
			pref = props.getProperty("preferredLayoutAlgorithm", DEF_LAYOUT);*/
		
		final CyLayoutAlgorithm layout = layMan.getLayout(pref);
		if (layout != null) {
			TaskIterator ti = layout.createTaskIterator(networkView, layout.getDefaultLayoutContext(),
					new HashSet<View<CyNode>>(e.getNodeViews()), "");
			tm.execute(ti);
		} else {
			throw new IllegalArgumentException("Couldn't find layout algorithm: " + pref);
		}
		
		networkView.updateView();
	}

}
