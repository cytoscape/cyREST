package org.cytoscape.rest.internal.task;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.rest.TaskFactoryManager;
import org.cytoscape.rest.internal.CyActivator.WriterListener;
import org.cytoscape.rest.internal.MappingFactoryManager;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskMonitor;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class CyBinder extends AbstractBinder {

	private final TaskMonitor headlessMonitor;

	private final CyNetworkManager networkManager;
	private final CyNetworkViewManager networkViewManager;
	private final CyNetworkFactory networkFactory;
	private final TaskFactoryManager tfManager;
	private final CyApplicationManager applicationManager;
	private final VisualMappingManager vmm;
	private final CyLayoutAlgorithmManager layoutManager;
	private final CyTableManager tableMamanger;

	private final CyNetworkViewWriterFactory cytoscapeJsWriterFactory;
	private final InputStreamTaskFactory cytoscapeJsReaderFactory;
	private final WriterListener vizmapWriterFactoryListener;
	private final VisualStyleFactory vsFactory;

	private final MappingFactoryManager mappingFactoryManager;
	private final CyGroupFactory groupFactory;
	private final CyGroupManager groupManager;

	public CyBinder(final CyNetworkManager networkManager, final CyNetworkViewManager networkViewManager,
			final CyNetworkFactory networkFactory, final TaskFactoryManager tfManager,
			final CyApplicationManager applicationManager, final VisualMappingManager vmm,
			final CyNetworkViewWriterFactory cytoscapeJsWriterFactory,
			final InputStreamTaskFactory cytoscapeJsReaderFactory, final CyLayoutAlgorithmManager layoutManager,
			final WriterListener vizmapWriterFactoryListener, final TaskMonitor headlessMonitor,
			final CyTableManager tableManager, final VisualStyleFactory vsFactory,
			final MappingFactoryManager mappingFactoryManager, final CyGroupFactory groupFactory,
			final CyGroupManager groupManager) {
		this.networkManager = networkManager;
		this.networkViewManager = networkViewManager;
		this.networkFactory = networkFactory;
		this.tfManager = tfManager;
		this.applicationManager = applicationManager;
		this.vmm = vmm;
		this.cytoscapeJsReaderFactory = cytoscapeJsReaderFactory;
		this.cytoscapeJsWriterFactory = cytoscapeJsWriterFactory;
		this.layoutManager = layoutManager;
		this.vizmapWriterFactoryListener = vizmapWriterFactoryListener;
		this.headlessMonitor = headlessMonitor;
		this.tableMamanger = tableManager;
		this.vsFactory = vsFactory;
		this.mappingFactoryManager = mappingFactoryManager;
		this.groupFactory = groupFactory;
		this.groupManager = groupManager;
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
		bind(layoutManager).to(CyLayoutAlgorithmManager.class);
		bind(vizmapWriterFactoryListener).to(WriterListener.class);
		bind(headlessMonitor).to(TaskMonitor.class);
		bind(tableMamanger).to(CyTableManager.class);
		bind(vsFactory).to(VisualStyleFactory.class);
		bind(mappingFactoryManager).to(MappingFactoryManager.class);
		bind(groupFactory).to(CyGroupFactory.class);
		bind(groupManager).to(CyGroupManager.class);
	}
}