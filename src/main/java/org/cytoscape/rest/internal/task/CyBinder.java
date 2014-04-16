package org.cytoscape.rest.internal.task;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.rest.TaskFactoryManager;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class CyBinder extends AbstractBinder {

	private final CyNetworkManager networkManager;
	private final CyNetworkViewManager networkViewManager;
	private final CyNetworkFactory networkFactory;
	private final TaskFactoryManager tfManager;
	private final CyApplicationManager applicationManager;
	private final VisualMappingManager vmm;
	
	private final CyNetworkViewWriterFactory cytoscapeJsWriterFactory;
	private final InputStreamTaskFactory cytoscapeJsReaderFactory;

	public CyBinder(final CyNetworkManager networkManager, final CyNetworkViewManager networkViewManager,
			final CyNetworkFactory networkFactory,
			final TaskFactoryManager tfManager, final CyApplicationManager applicationManager,
			final VisualMappingManager vmm, final CyNetworkViewWriterFactory cytoscapeJsWriterFactory,
			final InputStreamTaskFactory cytoscapeJsReaderFactory) 
	{
		this.networkManager = networkManager;
		this.networkViewManager = networkViewManager;
		this.networkFactory = networkFactory;
		this.tfManager = tfManager;
		this.applicationManager = applicationManager;
		this.vmm = vmm;
		this.cytoscapeJsReaderFactory = cytoscapeJsReaderFactory;
		this.cytoscapeJsWriterFactory = cytoscapeJsWriterFactory;
	}

	@Override
	protected void configure() {
		bind(networkManager).to(CyNetworkManager.class);
		bind(networkViewManager).to(CyNetworkViewManager.class);
		bind(networkFactory).to(CyNetworkFactory.class);
		bind(tfManager).to(TaskFactoryManager.class);
		bind(vmm).to(VisualMappingManager.class);
		bind(applicationManager).to(CyApplicationManager.class);
		bind(cytoscapeJsReaderFactory).to(InputStreamTaskFactory.class);
		bind(cytoscapeJsWriterFactory).to(CyNetworkViewWriterFactory.class);
	}
}