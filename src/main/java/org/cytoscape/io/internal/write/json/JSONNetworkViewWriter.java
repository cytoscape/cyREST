package org.cytoscape.io.internal.write.json;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.cytoscape.io.write.CyWriter;
import org.cytoscape.task.AbstractNetworkViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Writer for all JSON format.
 * Output format will be determined by ObjectMapper.
 *
 */
public class JSONNetworkViewWriter extends AbstractNetworkViewTask implements CyWriter {
	
	private static final Logger logger = LoggerFactory.getLogger(JSONNetworkViewWriter.class);
	
	protected final OutputStream outputStream;
	protected final ObjectMapper networkView2jsonMapper;

	public JSONNetworkViewWriter(final OutputStream outputStream, final CyNetworkView networkView, final ObjectMapper networkView2jsonMapper) {
		super(networkView);
		
		this.outputStream = outputStream;
		this.networkView2jsonMapper = networkView2jsonMapper;
	}
	

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		if(taskMonitor != null) {
			taskMonitor.setTitle("Writing Network View to JSON...");
			taskMonitor.setProgress(0);
		}
		networkView2jsonMapper.writeValue(new OutputStreamWriter(outputStream, EncodingUtil.getEncoder()), view);
		outputStream.close();
	}
}