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
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.ws.soap.AddressingFeature.Responses;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.rest.internal.datamapper.TableMapper;
import org.cytoscape.rest.internal.serializer.CyTableSerializer;
import org.cytoscape.rest.internal.serializer.TableModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * REST API for CyTable objects. This is for assigned table only.
 * 
 * @author kono
 * 
 */
@Singleton
@Path("/v1/networks/{networkId}/tables")
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

	private final TableMapper tableMapper;
	private final ObjectMapper tableObjectMapper;

	public TableDataService() {
		super();
		this.tableMapper = new TableMapper();
		this.tableObjectMapper = new ObjectMapper();
		this.tableObjectMapper.registerModule(new TableModule());
	}

	/**
	 * 
	 * @title Create new column in the target table.
	 * 
	 *        This method creates a new, empty column in the specified table.
	 * 
	 * @param networkId
	 *            Network ID
	 * @param tableType
	 *            Table type (defaultnode, defaultedge or defaultnetwork)
	 * 
	 */
	@POST
	@Path("/{tableType}/columns")
	@Consumes(MediaType.APPLICATION_JSON)
	public void createColumn(@PathParam("networkId") Long networkId, @PathParam("tableType") String tableType,
			final InputStream is) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType);
		final ObjectMapper objMapper = new ObjectMapper();
		try {
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
			tableMapper.createNewColumn(rootNode, table);
		} catch (IOException e) {
			throw getError(e, Response.Status.PRECONDITION_FAILED);
		}
	}

	/**
	 * Delete a column from a table
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param tableType
	 *            Table type (defaultnode, defaultedge or defaultnetwork)
	 * @param columnName
	 *            Name of the column to be deleted
	 * 
	 */
	@DELETE
	@Path("/{tableType}/columns/{columnName}")
	public void deleteColumn(@PathParam("networkId") Long networkId, @PathParam("tableType") String tableType,
			@PathParam("columnName") String columnName) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType);
		if (table != null) {
			table.deleteColumn(columnName);
		} else {
			logger.error("Failed to delete a column. (Missing table?)");
			throw new NotFoundException("Could not find the table.  (This should not happen!)");
		}
	}

	/**
	 * @title Update a column name
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param tableType
	 *            Table type (defaultnode, defaultedge or defaultnetwork)
	 * @param columnName
	 *            Original name of the column to be updated.
	 * 
	 */
	@PUT
	@Path("/{tableType}/columns")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateColumnName(@PathParam("networkId") Long networkId, @PathParam("tableType") String tableType,
			@PathParam("columnName") String columnName, final InputStream is) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType);
		final ObjectMapper objMapper = new ObjectMapper();
		try {
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
			tableMapper.updateColumnName(rootNode, table);
		} catch (IOException e) {
			logger.error("Could not parse the input JSON (new column name)", e);
			throw new WebApplicationException("Could not parse the input JSON.", e, 412);
		}
	}

	/**
	 * @title Update a values in a column
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param tableType
	 *            Table type (defaultnode, defaultedge or defaultnetwork)
	 * @param columnName
	 *            Target column name.
	 * 
	 */
	@PUT
	@Path("/{tableType}/columns/{columnName}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateColumnValues(@PathParam("networkId") Long networkId, @PathParam("tableType") String tableType,
			@PathParam("columnName") String columnName, final InputStream is) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType);
		final ObjectMapper objMapper = new ObjectMapper();
		try {
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
			tableMapper.updateColumnValues(rootNode, table, columnName);
		} catch (IOException e) {
			logger.error("Could not read JSON for new column values.", e);
			throw new WebApplicationException("Could not parse the input JSON.", e, 412);
		}
	}

	/**
	 * @title Update table data
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param tableType
	 *            Table type (defaultnode, defaultedge or defaultnetwork)
	 */
	@PUT
	@Path("/{tableType}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateTable(@PathParam("networkId") Long networkId, @PathParam("tableType") String tableType,
			final InputStream is) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType);

		final ObjectMapper objMapper = new ObjectMapper();
		try {
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
			tableMapper.updateTableValues(rootNode, table);
		} catch (IOException e) {
			logger.error("Could not parse JSON for new table entries.", e);
			throw new WebApplicationException("Could not parse new table data JSON.", e, 412);
		}
	}

	/**
	 * @title Get a row in a table
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param tableType
	 *            Table type (defaultnode, defaultedge or defaultnetwork)
	 * @param primaryKey
	 *            Name of primary key column
	 * 
	 * @return Row in the table
	 */
	@GET
	@Path("/{tableType}/rows/{primaryKey}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRow(@PathParam("networkId") Long networkId, @PathParam("tableType") String tableType,
			@PathParam("primaryKey") Long primaryKey) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType);
		if(!table.rowExists(primaryKey)) {
			throw getError("Could not find the row with primary key: " + primaryKey, Response.Status.NOT_FOUND);
		}
		
		final CyRow row = table.getRow(primaryKey);
		
		try {
			return this.serializer.serializeRow(row);
		} catch (IOException e) {
			logger.error("Copuld not serialize a table.");
			throw getError(e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 
	 * Get a cell entry
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param tableType
	 *            Table type (defaultnode, defaultedge or defaultnetwork)
	 * @param primaryKey
	 *            Name of primary key column
	 * @param columnName
	 *            Name of the column
	 * 
	 * @return Value in the cell. String, Boolean, Number, or List.
	 * 
	 */
	@GET
	@Path("/{tableType}/rows/{primaryKey}/{columnName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Object getCell(@PathParam("networkId") Long networkId, @PathParam("tableType") String tableType,
			@PathParam("primaryKey") Long primaryKey, @PathParam("columnName") String columnName) {
		final CyNetwork network = getCyNetwork(networkId);
		
		final CyTable table = getTableByType(network, tableType);
		if(!table.rowExists(primaryKey)) {
			throw getError("Could not find the row with promary key: " + primaryKey, Response.Status.NOT_FOUND);
		}
		
		final CyColumn column = table.getColumn(columnName);
		if(column == null) {
			throw getError("Could not find the column: " + columnName, Response.Status.NOT_FOUND);
		}
		
		final CyRow row = table.getRow(primaryKey);

		if (column.getType() == List.class) {
			List<?> listCell = row.getList(columnName, column.getListElementType());
			if (listCell == null) {
				throw getError("Could not find list value.",Response.Status.NOT_FOUND);
			} else {
				return listCell;
			}
		} else {
			final Object cell = row.get(columnName, column.getType());
			if (cell == null) {
				throw getError("Could not find value." ,Response.Status.NOT_FOUND); 
			}

			if (column.getType() == String.class) {
				return cell.toString();
			} else {
				return cell;
			}
		}
	}
	

	@GET
	@Path("/{tableType}/rows")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRows(@PathParam("networkId") Long networkId, @PathParam("tableType") String tableType) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType);
		try {
			return this.serializer.serializeAllRows(table.getAllRows());
		} catch (IOException e) {
			throw new WebApplicationException(e, 500);
		}
	}

	@GET
	@Path("/{tableType}/columns")
	@Produces(MediaType.APPLICATION_JSON)
	public String getColumnNames(@PathParam("networkId") Long networkId, @PathParam("tableType") String tableType) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType);
		try {
			return this.serializer.serializeColumns(table.getColumns());
		} catch (IOException e) {
			throw new WebApplicationException("Could not get column names.", e, 500);
		}
	}

	@GET
	@Path("/{tableType}/columns/{columnName}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getColumnValues(@PathParam("networkId") Long networkId, @PathParam("tableType") String tableType,
			@PathParam("columnName") String columnName) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType);
		final CyColumn column = table.getColumn(columnName);
		final List<Object> values = column.getValues(column.getType());

		try {
			return this.serializer.serializeColumnValues(column, values);
		} catch (IOException e) {
			throw new WebApplicationException(e, 500);
		}
	}

	/**
	 * Get all tables assigned for the network.
	 * 
	 * @param id
	 * @return
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTables(@PathParam("networkId") Long networkId) {
		final Set<CyTable> tables = this.tableManager.getAllTables(true);

		try {
			return tableObjectMapper.writeValueAsString(tables);
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
	@Path("/{tableType}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTable(@PathParam("networkId") Long networkId, @PathParam("tableType") String tableType,
			@QueryParam(JsonTags.TABLE_FORMAT) String format) {

		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType);

		if (format != null && format.equals("csv")) {
			final CyTableSerializer tableSerializer = new CyTableSerializer();
			try {
				final String result = tableSerializer.toCSV(table);
				return result;
			} catch (Exception e) {
				throw new WebApplicationException(e, 500);
			}
		} else {
			try {
				return this.tableObjectMapper.writeValueAsString(table);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				throw new WebApplicationException(e, 500);
			}
		}
	}

	/**
	 * @title Get a table as CSV
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param tableType
	 *            Table type (defaultnode, defaultedge or defaultnetwork)
	 * 
	 * @return Entire table as CSV
	 * 
	 */
	@GET
	@Path("/{tableType}.csv")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTableAsCsv(@PathParam("networkId") Long networkId, @PathParam("tableType") String tableType,
			@QueryParam(JsonTags.TABLE_FORMAT) String format) {

		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType);
		final CyTableSerializer tableSerializer = new CyTableSerializer();
		try {
			final String result = tableSerializer.toCSV(table);
			return result;
		} catch (Exception e) {
			throw new WebApplicationException("Could not serialize table to CSV.", e, 500);
		}
	}
}