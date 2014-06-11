package org.cytoscape.rest.internal.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.rest.internal.serializer.CyTableSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Path("/v1")
// API version
public class TableDataService extends AbstractDataService {

	private final static Logger logger = LoggerFactory.getLogger(TableDataService.class);

	public TableDataService() {
		super();
	}

	@GET
	@Path("/networks/{id}/tables")
	@Produces(MediaType.TEXT_PLAIN)
	public String getTables(@PathParam("id") Long id) {

		final Set<CyTable> tables = this.tableManager.getAllTables(true);

		return id.toString();

	}


	@GET
	@Path("/networks/{id}/tables/{tableType}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getTable(@PathParam("id") Long id, @PathParam("tableType") String tableType,
			@QueryParam("format") String format) {

		final CyNetwork network = getCyNetwork(id);
		final CyTable table;
		List<? extends CyIdentifiable> objects = null;
		
		if (tableType.equals("node")) {
			table = network.getDefaultNodeTable();
			objects = network.getNodeList();
		} else if (tableType.equals("edge")) {
			table = network.getDefaultEdgeTable();
			objects = network.getEdgeList();
		} else if (tableType.equals("network")) {
			table = network.getDefaultNetworkTable();
		} else {
			// No such table.
			throw new WebApplicationException(404);
		}

		if (format.equals("csv")) {
			final CyTableSerializer tableSerializer = new CyTableSerializer();
			try {
				final String result = tableSerializer.toCSV(table);
				return result;
			} catch (Exception e) {
				throw new WebApplicationException(e, 500);
			}
		} else {
			// Return as JSON
			final StringBuilder builder = new StringBuilder();
			builder.append("[");
			for(final CyIdentifiable obj:objects) {
				CyRow row = network.getRow(obj);
				try {
					builder.append(this.serializer.serializeGraphObject(obj, row) + ",");
				} catch (IOException e) {
					throw new WebApplicationException(e, 500);
				}
			}
			String result = builder.toString();
			result = result.substring(0, result.length()-1);
			
			return result + "]";
		}
	}
}