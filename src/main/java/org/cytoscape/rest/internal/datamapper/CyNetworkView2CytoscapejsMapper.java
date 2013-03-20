package org.cytoscape.rest.internal.datamapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.rest.DataMapper;
import org.cytoscape.rest.internal.translator.CytoscapejsModule;
import org.cytoscape.view.model.CyNetworkView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CyNetworkView2CytoscapejsMapper implements DataMapper<CyNetworkView> {

	private final static Logger logger = LoggerFactory.getLogger(CyNetworkView2CytoscapejsMapper.class);

	private final ObjectMapper jacksonNetworkMapper;


	public CyNetworkView2CytoscapejsMapper() {
		this.jacksonNetworkMapper = new ObjectMapper();
		jacksonNetworkMapper.registerModule(new CytoscapejsModule());
	}
	

	@Override
	public CyNetworkView read(final InputStream is) {
		return null;
	}

	@Override
	public CyNetworkView read(final String textData) {
		return null;
	}

	@Override
	public void write(final OutputStream os, final CyNetworkView networkView) throws IOException {
		jacksonNetworkMapper.writeValue(os, networkView);
	}

	@Override
	public String writeAsString(final CyNetworkView networkView) {
		String json = null;
		
		try {
			json = jacksonNetworkMapper.writeValueAsString(networkView);
		} catch (JsonProcessingException e) {
			logger.warn("Could not serialize CynetworkView to JSON.", e);
			return json;
		}
		return json;
	}
}
