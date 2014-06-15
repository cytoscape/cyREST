package org.cytoscape.rest.internal.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.rest.internal.datamapper.TableMapper;
import org.cytoscape.rest.internal.serializer.CyTableSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
@Path("/v1")
// API version
public class TableDataService extends AbstractDataService {

	private final static Logger logger = LoggerFactory.getLogger(TableDataService.class);

	private static enum TableType {
		DEFAULT_NODE("defaultnode"), DEFAULT_EDGE("defaultedge"), DEFAULT_NETWORK("defaultnetwork");

		private final String type;

		private TableType(final String type) {
			this.type = type;
		}

		private String getType() {
			return this.type;
		}
	}

	private final CyTableSerializer tableSerializer;
	private final TableMapper tableMapper;

	public TableDataService() {
		super();
		this.tableSerializer = new CyTableSerializer();
		this.tableMapper = new TableMapper();
	}

	// ////////////// Count objects //////////////////////
	@GET
	@Path("/networks/{id}/tables/count")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTableCount(@PathParam("id") Long id) {

		final CyNetwork network = getCyNetwork(id);
		return id.toString();
	}

	// //////////////// Delete Table ////////////////////
	@DELETE
	@Path("/networks/{id}/tables/{tableID}")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteTable(@PathParam("id") Long id) {
		final CyNetwork network = getCyNetwork(id);
		return id.toString();
	}

	@POST
	@Path("/tables")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createTableFor(@PathParam("id") Long id, final InputStream is) {
		final CyNetwork network = getCyNetwork(id);
		// TODO Implement this
		return id.toString();
	}

	/**
	 * Create new column to the target table.
	 * 
	 * @param id
	 * @param tableType
	 * @param is
	 */
	@POST
	@Path("/networks/{id}/tables/{tableType}/columns")
	@Consumes(MediaType.APPLICATION_JSON)
	public void createColumn(@PathParam("id") Long id, @PathParam("tableType") String tableType, final InputStream is) {
		final CyNetwork network = getCyNetwork(id);
		final CyTable table = getTableByType(network, tableType);
		final ObjectMapper objMapper = new ObjectMapper();
		try {
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
			tableMapper.createNewColumn(rootNode, table);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebApplicationException(e, 500);
		}
	}

	
	/**
	 * Delete a column from a table.
	 * 
	 * @param id
	 * @param tableType
	 * @param columnName
	 */
	@DELETE
	@Path("/networks/{id}/tables/{tableType}/columns/{columnName}")
	public void deleteColumn(@PathParam("id") Long id, @PathParam("tableType") String tableType,
			@PathParam("columnName") String columnName) {
		final CyNetwork network = getCyNetwork(id);
		final CyTable table = getTableByType(network, tableType);
		if(table != null) {
			table.deleteColumn(columnName);
		}
	}


	@PUT
	@Path("/networks/{id}/tables/{tableType}/columns/{columnName}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateColumn(@PathParam("id") Long id, @PathParam("tableType") String tableType,
			@PathParam("columnName") String columnName, final InputStream is) {
		final CyNetwork network = getCyNetwork(id);
		final CyTable table = getTableByType(network, tableType);
		final ObjectMapper objMapper = new ObjectMapper();
		try {
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
			final CyColumn column = table.getColumn(columnName);
			tableMapper.updateColumnName(rootNode, column);
		} catch (IOException e) {
			throw new WebApplicationException(e, 500);
		}
	}

	@GET
	@Path("/networks/{id}/tables/{tableType}/rows/{primaryKey}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRow(@PathParam("id") Long id, @PathParam("tableType") String tableType,
			@PathParam("primaryKey") Long primaryKey) {
		final CyNetwork network = getCyNetwork(id);
		final CyTable table = getTableByType(network, tableType);
		final CyRow row = table.getRow(primaryKey);
		try {
			return this.serializer.serializeRow(row);
		} catch (IOException e) {
			throw new WebApplicationException(e, 500);
		}
	}

	@GET
	@Path("/networks/{id}/tables/{tableType}/rows/{primaryKey}/{columnName}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCell(@PathParam("id") Long id, @PathParam("tableType") String tableType,
			@PathParam("primaryKey") Long primaryKey, @PathParam("columnName") String columnName) {
		final CyNetwork network = getCyNetwork(id);
		final CyTable table = getTableByType(network, tableType);
		final CyRow row = table.getRow(primaryKey);
		try {
			return this.serializer.serializeCell(row, columnName);
		} catch (IOException e) {
			throw new WebApplicationException(e, 500);
		}
	}

	@GET
	@Path("/networks/{id}/tables/{tableType}/rows")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRows(@PathParam("id") Long id, @PathParam("tableType") String tableType) {
		final CyNetwork network = getCyNetwork(id);
		final CyTable table = getTableByType(network, tableType);
		try {
			return this.serializer.serializeAllRows(table.getAllRows());
		} catch (IOException e) {
			throw new WebApplicationException(e, 500);
		}
	}

	@GET
	@Path("/networks/{id}/tables/{tableType}/columns")
	@Produces(MediaType.APPLICATION_JSON)
	public String getColumnNames(@PathParam("id") Long id, @PathParam("tableType") String tableType) {
		final CyNetwork network = getCyNetwork(id);
		final CyTable table = getTableByType(network, tableType);
		try {
			return this.serializer.serializeColumns(table.getColumns());
		} catch (IOException e) {
			throw new WebApplicationException(e, 500);
		}
	}

	@GET
	@Path("/networks/{id}/tables/{tableType}/columns/{columnName}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getColumnValues(@PathParam("id") Long id, @PathParam("tableType") String tableType,
			@PathParam("columnName") String columnName) {
		final CyNetwork network = getCyNetwork(id);
		final CyTable table = getTableByType(network, tableType);
		final CyColumn column = table.getColumn(columnName);
		final List<Object> values = column.getValues(column.getType());
		try {
			return this.serializer.serializeColumnValues(column, values);
		} catch (IOException e) {
			throw new WebApplicationException(e, 500);
		}
	}

	@GET
	@Path("/networks/{id}/tables")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTables(@PathParam("id") Long id) {
		final Set<CyTable> tables = this.tableManager.getAllTables(true);

		try {
			return tableSerializer.serializeTables(tables);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebApplicationException(e, 500);
		}
	}

	private final CyTable getTableByType(final CyNetwork network, final String tableType) {
		CyTable table;
		if (tableType.equals(TableType.DEFAULT_NODE.getType())) {
			table = network.getDefaultNodeTable();
		} else if (tableType.equals(TableType.DEFAULT_EDGE.getType())) {
			table = network.getDefaultEdgeTable();
		} else if (tableType.equals(TableType.DEFAULT_NETWORK.getType())) {
			table = network.getDefaultNetworkTable();
		} else {
			// No such table.
			throw new WebApplicationException("No such table type: " + tableType, 404);
		}
		return table;
	}

	private final List<? extends CyIdentifiable> getGraphObjectsByType(final CyNetwork network, final String tableType) {
		List<? extends CyIdentifiable> objects = null;
		if (tableType.equals(TableType.DEFAULT_NODE.getType())) {
			objects = network.getNodeList();
		} else if (tableType.equals(TableType.DEFAULT_EDGE.getType())) {
			objects = network.getEdgeList();
		} else if (tableType.equals(TableType.DEFAULT_NETWORK.getType())) {
			ArrayList<CyNetwork> list = new ArrayList<CyNetwork>();
			list.add(network);
			objects = list;
		} else {
			// No such table.
			throw new WebApplicationException("No such table type.", 404);
		}
		return objects;
	}

	@GET
	@Path("/networks/{id}/tables/{tableType}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getTable(@PathParam("id") Long id, @PathParam("tableType") String tableType,
			@QueryParam("format") String format) {

		final CyNetwork network = getCyNetwork(id);
		final CyTable table = getTableByType(network, tableType);
		final List<? extends CyIdentifiable> objects = getGraphObjectsByType(network, tableType);

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
			for (final CyIdentifiable obj : objects) {
				CyRow row = network.getRow(obj);
				try {
					builder.append(this.serializer.serializeGraphObject(obj, row) + ",");
				} catch (IOException e) {
					throw new WebApplicationException(e, 500);
				}
			}
			String result = builder.toString();
			result = result.substring(0, result.length() - 1);

			return result + "]";
		}
	}
}