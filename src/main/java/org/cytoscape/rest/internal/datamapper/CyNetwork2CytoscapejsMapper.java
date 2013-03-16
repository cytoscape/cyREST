package org.cytoscape.rest.internal.datamapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.rest.DataMapper;
import org.cytoscape.rest.internal.translator.CytoscapejsModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * CyNetwork <--> cytoscpae.js JSON format
 *
 */
public class CyNetwork2CytoscapejsMapper implements DataMapper<CyNetwork> {

	private final static Logger logger = LoggerFactory.getLogger(CyNetwork2CytoscapejsMapper.class);

	private final ObjectMapper jacksonNetworkMapper;


	public CyNetwork2CytoscapejsMapper() {
		this.jacksonNetworkMapper = new ObjectMapper();
		jacksonNetworkMapper.registerModule(new CytoscapejsModule());
	}
	

	@Override
	public CyNetwork read(final InputStream is) {
		return null;
	}

	@Override
	public CyNetwork read(final String textData) {
		return null;
	}

	@Override
	public void write(final OutputStream os, final CyNetwork network) throws IOException {
		jacksonNetworkMapper.writeValue(os, network);
	}

	@Override
	public String writeAsString(final CyNetwork network) {
		String json = null;
		
		try {
			json = jacksonNetworkMapper.writeValueAsString(network);
		} catch (JsonProcessingException e) {
			logger.warn("Could not serialize Cynetwork to JSON.", e);
			return json;
		}
		return json;
	}
}
