package org.cytoscape.rest.internal.resource;

import java.io.IOException;
import java.io.InputStream;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.model.CyColumn;
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
 */
@Singleton
@Path("/v1/networks/{networkId}/tables")
public class TableResource extends AbstractResource {

	private final static Logger logger = LoggerFactory.getLogger(TableResource.class);

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
	private final CyTableSerializer tableSerializer;

	public TableResource() {
		super();
		this.tableMapper = new TableMapper();
		this.tableObjectMapper = new ObjectMapper();
		this.tableObjectMapper.registerModule(new TableModule());
		this.tableSerializer = new CyTableSerializer();
	}

	/**
	 * Create new, empty column in an assigned table.
	 * This accepts the following object OR allay of this objects:
	 * 
	 * <pre>
	 * 		{
	 * 			"name":"COLUMN NAME",
	 * 			"type":"data type, Double, String, Boolean, Long, Integer",
	 * 			"immutable": "Optional: boolean value to specify immutable or not",
	 * 			"list": "Optional.  If true, return create List column for the given type."
	 * 		}
	 * </pre>
	 * 
	 * @summary Create new column(s) in the table
	 * 
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param tableType
	 *            Table type: "defaultnode", "defaultedge" or "defaultnetwork"
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
			if(rootNode.isArray()) {
				for(JsonNode node: rootNode) {
					tableMapper.createNewColumn(node, table);
				}
			} else {
				tableMapper.createNewColumn(rootNode, table);
			}
		} catch (Exception e) {
			throw getError("Could not process column JSON.", e, Response.Status.PRECONDITION_FAILED);
		}
	}

	/**
	 * 
	 * @summary Delete a column in a table
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param tableType
	 *            Table type: "defaultnode", "defaultedge" or "defaultnetwork"
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
	 * 
	 * @summary Update a column name
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param tableType
	 *            Table type: "defaultnode", "defaultedge" or "defaultnetwork"
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
			throw getError("Could not parse the input JSON for updating column name.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}


	/**
	 * 
	 * @summary Update values in a column
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param tableType
	 *            Table type: "defaultnode", "defaultedge" or "defaultnetwork"
	 * @param columnName
	 *            Target column name
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
			throw getError("Could not parse the input JSON for updating column values.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}


	/**
	 * 
	 * This API is for updating default node/edge/network data table at once.
	 * 
	 * If not specified, SUID will be used for mapping.
	 * 
	 * @summary Update table data
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
			// This should be an JSON array.
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
			tableMapper.updateTableValues(rootNode, table);
		} catch (Exception e) {
			throw getError("Could not parse the input JSON for updating table because: " + e.getMessage(), e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * @summary Get a row in a table
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
		if (!table.rowExists(primaryKey)) {
			throw new NotFoundException("Could not find the row with primary key: " + primaryKey);
		}

		final CyRow row = table.getRow(primaryKey);

		try {
			return this.serializer.serializeRow(row);
		} catch (IOException e) {
			throw getError("Could not serialize a row with primary key: " + primaryKey, e,
					Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 
	 * @summary Get a value in the cell
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
		if (!table.rowExists(primaryKey)) {
			throw new NotFoundException("Could not find the row with promary key: " + primaryKey);
		}

		final CyColumn column = table.getColumn(columnName);
		if (column == null) {
			throw new NotFoundException("Could not find the column: " + columnName);
		}

		final CyRow row = table.getRow(primaryKey);

		if (column.getType() == List.class) {
			List<?> listCell = row.getList(columnName, column.getListElementType());
			if (listCell == null) {
				throw new NotFoundException("Could not find list value.");
			} else {
				return listCell;
			}
		} else {
			final Object cell = row.get(columnName, column.getType());
			if (cell == null) {
				throw new NotFoundException("Could not find value.");
			}

			if (column.getType() == String.class) {
				return cell.toString();
			} else {
				return cell;
			}
		}
	}

	
	/**
	 * 
	 * @summary Get all rows in the table
	 * 
	 * @param networkId Network SUID
	 * @param tableType
	 *            Table type (defaultnode, defaultedge or defaultnetwork)
	 *
	 * @return All rows in the table
	 * 
	 */
	@GET
	@Path("/{tableType}/rows")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRows(@PathParam("networkId") Long networkId, @PathParam("tableType") String tableType) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType);
		try {
			return this.serializer.serializeAllRows(table.getAllRows());
		} catch (IOException e) {
			throw getError("Could not serialize rows.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	
	/**
	 * 
	 * @summary Get all columns in the table
	 * 
	 * @param networkId Network SUID
	 * @param tableType
	 *            Table type (defaultnode, defaultedge or defaultnetwork)
	 *
	 * @return All columns in the table
	 * 
	 */
	@GET
	@Path("/{tableType}/columns")
	@Produces(MediaType.APPLICATION_JSON)
	public String getColumnNames(@PathParam("networkId") Long networkId, @PathParam("tableType") String tableType) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType);
		try {
			return this.serializer.serializeColumns(table.getColumns());
		} catch (IOException e) {
			throw getError("Could not serialize column names.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	
	/**
	 * 
	 * @summary Get all values in the column
	 * 
	 * @param networkId Network SUID
	 * @param tableType
	 *            Table type (defaultnode, defaultedge or defaultnetwork)
	 * @param columnName Column name
	 *
	 * @return All values in the column
	 * 
	 */
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
			throw getError("Could not serialize column values.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	
	/**
	 * @summary Get all tables assigned to the network
	 * 
	 * @param networkId network SUID
	 * 
	 * @return All tables in JSON
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTables(@PathParam("networkId") Long networkId) {
		final Set<CyTable> tables = this.tableManager.getAllTables(true);

		try {
			return tableObjectMapper.writeValueAsString(tables);
		} catch (IOException e) {
			throw getError("Could not serialize tables.", e, Response.Status.INTERNAL_SERVER_ERROR);
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
			throw new NotFoundException("No such table type: " + tableType);
		}
		return table;
	}


	/**
	 * @summary Get a default table
	 * 
	 * @param networkId Network SUID
	 * @param tableType Table type (defaultnode, defaultedge or defaultnetwork)
	 * 
	 * @return The Table in JSON
	 * 
	 */
	@GET
	@Path("/{tableType}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTable(@PathParam("networkId") Long networkId, @PathParam("tableType") String tableType) {

		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType);

		try {
			return this.tableObjectMapper.writeValueAsString(table);
		} catch (JsonProcessingException e) {
			throw getError("Could not serialize table.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * @summary Get a table as CSV
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param tableType
	 *            Table type (defaultnode, defaultedge or defaultnetwork)
	 * 
	 * @return Table in CSV format
	 * 
	 */
	@GET
	@Path("/{tableType}.csv")
	@Produces(MediaType.TEXT_PLAIN)
	public String getTableAsCsv(@PathParam("networkId") Long networkId, @PathParam("tableType") String tableType,
			@QueryParam(JsonTags.TABLE_FORMAT) String format) {

		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType);
		try {
			final String result = tableSerializer.toCSV(table);
			return result;
		} catch (Exception e) {
			throw getError("Could not serialize table into CSV.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
}