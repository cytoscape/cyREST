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
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.rest.internal.datamapper.TableMapper;
import org.cytoscape.rest.internal.model.UpdateRow;
import org.cytoscape.rest.internal.serializer.CyTableSerializer;
import org.cytoscape.rest.internal.serializer.TableModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * REST API for CyTable objects. This is for assigned table only.
 * 
 */
@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.TABLES_TAG})
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
	 * 			"local": "Optional.  If true, it will be a local column"
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
	public Response createColumn(@PathParam("networkId") Long networkId, 
			@ApiParam(allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType,
			final InputStream is) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType, null);
		final CyTable localTable = getTableByType(
				network, tableType, JsonTags.COLUMN_IS_LOCAL);
		
		final ObjectMapper objMapper = new ObjectMapper();
		try {
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
			if(rootNode.isArray()) {
				for(JsonNode node: rootNode) {
					tableMapper.createNewColumn(node, table, localTable);
				}
			} else {
				tableMapper.createNewColumn(rootNode, table, localTable);
			}
			
			// Use 201 for created resource
			return Response.status(Response.Status.CREATED).build();
			
		} catch (Exception e) {
			throw getError("Could not process column JSON.", 
					e, Response.Status.PRECONDITION_FAILED);
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
	public Response deleteColumn(
			@PathParam("networkId") Long networkId, 
			@ApiParam(allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType,
			@PathParam("columnName") String columnName) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType, null);
		if (table != null) {
			table.deleteColumn(columnName);
			return Response.ok().build();
		} else {
			logger.error("Failed to delete a column. (Missing table?)");
			
			throw new NotFoundException("Could not find the table.  (This should not happen!)");
		}
	}

	/**
	 * To update the column name, you need to provide the parameters in the body:
	 * 
	 * <pre>
	 * {
	 * 		"oldName": OLD_COLUMN_NAME,
	 * 		"newName": NEW_COLUMN_NAME
	 * }
	 * </pre>
	 * 
	 * Both parameters are required.
	 * 
	 * 
	 * @summary Update a column name
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param tableType
	 *            Table type: "defaultnode", "defaultedge" or "defaultnetwork"
	 * 
	 */
	@PUT
	@Path("/{tableType}/columns")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateColumnName(
			@PathParam("networkId") Long networkId,
			@ApiParam(allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType,
			final InputStream is) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType, null);
		final ObjectMapper objMapper = new ObjectMapper();
		
		try {
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
			tableMapper.updateColumnName(rootNode, table);
			return Response.ok().build();
		} catch (Exception e) {
			throw getError("Could not parse the input JSON for updating "
					+ "column name.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}


	/**
	 * 
	 * @summary Update values in a column
	 * 
	 * By default, you need to provide key-value pair to set values.
	 * However, if "default" is provided, it will be used for the entire column.
	 * 
	 * This is useful to set columns like "selected."
	 * 
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param tableType
	 *            Table type: "defaultnode", "defaultedge" or "defaultnetwork"
	 * @param columnName
	 *            Target column name
	 * @param default
	 *            Optional. If this value is provided, all cells will be set to this.
	 * 
	 */
	@PUT
	@Path("/{tableType}/columns/{columnName}")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Update values in a column", notes="By default, you need to provide key-value pair to set values. However, if \"default\" is provided, it will be used for the entire column. This is useful to set columns like \"selected.\"")
	public Response updateColumnValues(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId,
			@ApiParam(allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType,
			@ApiParam(value="Column Name") @PathParam("columnName") String columnName,
			@ApiParam(value="Default Value. If this value is provided, all cells will be set to this.", required=false) @QueryParam("default") String defaultValue, final InputStream is) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType, null);

		if (defaultValue != null) {
			tableMapper.updateAllColumnValues(defaultValue, table, columnName);
		} else {
			final ObjectMapper objMapper = new ObjectMapper();
			try {
				final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
				tableMapper.updateColumnValues(rootNode, table, columnName);
			} catch (IOException e) {
				throw getError(
						"Could not parse the input JSON for updating column values.",
						e, Response.Status.INTERNAL_SERVER_ERROR);
			}
		}
		return Response.ok().build();
	}


	/**
	 * 
	 * This API is for updating default node/edge/network data table.  New columns will be created if they
	 * does not exist in the target table.  
	 * 
	 * The BODY of the data should be in the following format:<br/>
	 * 
	 * <pre>
	 * 	{
	 * 		"key":"SUID",  		// This is the unique key column in the existing table
	 * 		"dataKey": "id",		// Mapping key for the new values
	 * 		"data": [
	 * 			{
	 * 				"id": 12345,		// Required. Field name should be same as "dataKey."
	 * 								// In this case, it is "id," but can be anything.
	 * 				"gene_name": "brca1",
	 * 				"exp1": 0.11,
	 * 				"exp2": 0.2
	 *  			}, ...
	 * 			
	 * 		]
	 * 	}
	 * </pre>
	 * 
	 * Current limitations:
	 * <ul>
	 * 	<li>	If key is not specified, SUID will be used for mapping</li>
	 * 	<li>Numbers are handled as Double</li>
	 * 	<li>List column is not supported in this version</li>
	 * </ul>
	 *
	 * @summary Update default table with new values.
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param tableType
	 *            Table type (defaultnode, defaultedge or defaultnetwork)
	 * @param class Optional.  If this query parameter is set to local, 
	 * 				local table column will be updated.
	 */
	@PUT
	@Path("/{tableType}")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Update default node/edge/network data table")
	public Response updateTable(
			@ApiParam() @PathParam("networkId") Long networkId, 
			@ApiParam(allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType,
			@ApiParam(allowableValues="local", required=false) @QueryParam("class") String tableClass,
		 final InputStream is) {
		
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType, tableClass);
		final ObjectMapper objMapper = new ObjectMapper();

		try {
			// This should be an JSON array.
			final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
			tableMapper.updateTableValues(rootNode, table);
		} catch (Exception e) {
			
			e.printStackTrace();
			
			throw getError("Could not parse the input JSON for updating table because: " + e.getMessage(), e, Response.Status.INTERNAL_SERVER_ERROR);
		}
		
		return Response.ok().build();
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
	 * @return A row in the table
	 */
	@GET
	@Path("/{tableType}/rows/{primaryKey}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRow(
			@PathParam("networkId") Long networkId,
			@ApiParam(allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType,
			@PathParam("primaryKey") Long primaryKey) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType, null);
		if (!table.rowExists(primaryKey)) {
			throw new NotFoundException("Could not find the row "
					+ "with primary key: " + primaryKey);
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
	public Object getCell(@PathParam("networkId") Long networkId,
			@ApiParam(allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType,
			@PathParam("primaryKey") Long primaryKey,
			@PathParam("columnName") String columnName) {
		final CyNetwork network = getCyNetwork(networkId);

		final CyTable table = getTableByType(network, tableType, null);
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
	 * @summary Get all rows in a table
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
	@ApiOperation(value="Get all rows in a table")
	public String getRows(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId,
			@ApiParam(allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType, null);
		try {
			return this.serializer.serializeAllRows(table.getAllRows());
		} catch (IOException e) {
			throw getError("Could not serialize rows.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	
	/**
	 * 
	 * @summary Get all columns in a table
	 * 
	 * @param networkId Network SUID
	 * @param tableType
	 *            Table type (defaultnode, defaultedge or defaultnetwork)
	 *
	 * @return All columns in the specified table.
	 * 
	 */
	@GET
	@Path("/{tableType}/columns")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get all columns in a table")
	public String getColumnNames(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId,
			@ApiParam(allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType, null);
		try {
			return this.serializer.serializeColumns(table.getColumns());
		} catch (IOException e) {
			throw getError("Could not serialize column names.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	
	/**
	 * 
	 * @summary Get all values in a column
	 * 
	 * @param networkId Network SUID
	 * @param tableType
	 *            Table type (defaultnode, defaultedge or defaultnetwork)
	 * @param columnName Column name
	 *
	 * @return All values under the specified column.
	 * 
	 */
	@GET
	@Path("/{tableType}/columns/{columnName}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getColumnValues(
			@PathParam("networkId") Long networkId,
			@ApiParam(allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType,
			@PathParam("columnName") String columnName) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType, null);
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

	private final CyTable getTableByType(final CyNetwork network, 
			final String tableType, final String tableClass) {
		
		// Check local or not
		final Boolean isLocal;
		if(tableClass == null || tableClass.isEmpty()) {
			isLocal = false;
		} else if(tableClass.equals("local")) {
			isLocal = true;
		} else {
			isLocal = false;
		}
		
		CyTable table;
		if (tableType.equals(TableType.DEFAULT_NODE.getType())) {
			if(isLocal) {
				table = network.getTable(CyNode.class, CyNetwork.LOCAL_ATTRS);
			} else {
				table = network.getDefaultNodeTable();
			}
		} else if (tableType.equals(TableType.DEFAULT_EDGE.getType())) {
			if(isLocal) {
				table = network.getTable(CyEdge.class, CyNetwork.LOCAL_ATTRS);
			} else {
				table = network.getDefaultEdgeTable();
			}
		} else if (tableType.equals(TableType.DEFAULT_NETWORK.getType())) {
			if(isLocal) {
				table = network.getTable(CyNetwork.class, CyNetwork.LOCAL_ATTRS);
			} else {
				table = network.getDefaultNetworkTable();
			}
		} else {
			// No such table.
			throw new NotFoundException("No such table type: " + tableType);
		}
		return table;
	}

	@GET
	@Path("/{tableType}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a default table", notes="Table output as JSON")
	public String getTable(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId,
			@ApiParam(allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType) {

		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType, null);

		try {
			return this.tableObjectMapper.writeValueAsString(table);
		} catch (JsonProcessingException e) {
			throw getError("Could not serialize table.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("/{tableType}.csv")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(value="Get a table as CSV")
	public String getTableAsCsv(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType) {
		return getTableString(networkId, tableType, ",");
	}
	
	@GET
	@Path("/{tableType}.tsv")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(value="Get a table as TSV (tab delimited text)")
	public String getTableAsTsv(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType) {
		return getTableString(networkId, tableType, "\t");
	}
	
	
	/**
	 * Actual function to generate CSV/TSV
	 * 
	 * @param networkId
	 * @param tableType
	 * @param separator
	 * @return
	 */
	private final String getTableString(final Long networkId, 
			final String tableType, final String separator) {

		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType, null);
		try {
			final String result = tableSerializer.toCSV(table, separator);
			return result;
		} catch (Exception e) {
			throw getError("Could not serialize table into CSV.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
}