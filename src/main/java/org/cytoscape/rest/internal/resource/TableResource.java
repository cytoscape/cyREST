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
import org.cytoscape.rest.internal.model.CyColumnModel;
import org.cytoscape.rest.internal.model.CyColumnValuesModel;
import org.cytoscape.rest.internal.model.CyRowModel;
import org.cytoscape.rest.internal.model.CyTableModel;
import org.cytoscape.rest.internal.model.CyTableWithRowsModel;
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
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * REST API for CyTable objects. This is for assigned table only.
 * 
 */
@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.TABLES_TAG})
@Singleton
@Path("/v1/networks/{networkId}/tables")
public class TableResource extends AbstractResource {

	private final static Logger logger = LoggerFactory.getLogger(TableResource.class);
	
	
	public final static String ROW_EXAMPLE="A row contains one or more entries of column names to values.\n\n"
			+ "```json\n{\n" 
			+ "  \"SUID\": 101,\n"  
			+ "  \"gene_name\": \"brca1\",\n" 
			+ "  \"exp\": 0.1\n" 
			+ "}\n```";
			
	
	public final static String ROW_ARRAY_EXAMPLE="```\n[\n" 
			+ "  {\n" 
			+ "    \"SUID\": 101,\n"  
			+ "    \"gene_name\": \"brca1\",\n" 
			+ "    \"exp\": 0.1\n" 
			+ "  },\n"
			+ "  {\n" 
			+ "    \"SUID\": 102,\n"  
			+ "    \"gene_name\": \"brca2\",\n" 
			+ "    \"exp\": 0.2\n" 
			+ "  }\n"
			+ "]\n"
			+ "```";
	
	
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

	
	@POST
	@Path("/{tableType}/columns")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Create new column(s) in the table", notes="Creates a new, empty column in an assigned table.\n\n"
	 + "This resource can also accept an array of NewColumn objects to create multiple columns.")
	@ApiImplicitParams( value= {
			@ApiImplicitParam(value="New Column Info", dataType="org.cytoscape.rest.internal.model.NewColumn", paramType="body", required=true),
	})
	@ApiResponses ( value = {
			@ApiResponse(code=201, message="Column(s) createed"),
			@ApiResponse(code=412, message="Could not process column JSON")
	})
	public Response createColumn(@PathParam("networkId") Long networkId, 
			@ApiParam(value="Table Type", allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType,
			@ApiParam(hidden=true) final InputStream is) {
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

	@DELETE
	@Path("/{tableType}/columns/{columnName}")
	@ApiOperation(value="Delete a column in a table", notes="Deletes the column specified by the `columnName` parameter from the table speficied by the `tableType` parameter from the network specified by the `networkId` parameter.")
	public Response deleteColumn(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Table Type", allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType,
			@ApiParam(value="Column Name") @PathParam("columnName") String columnName) {
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

	@PUT
	@Path("/{tableType}/columns")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Update a column name", notes="Renames an existing column.")
	@ApiImplicitParams(
			@ApiImplicitParam(value="Old and new column name", dataType="org.cytoscape.rest.internal.model.Rename", paramType="body", required=true)
			)
	public Response updateColumnName(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId,
			@ApiParam(value="Table Type", allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType,
			@ApiParam(hidden=true) final InputStream is) {
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

	@PUT
	@Path("/{tableType}/columns/{columnName}")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Update values in a column", notes="By default, you need to provide key-value pair to set "
			+ "values. However, if \"default\" is provided, it will be used for the entire column. This is useful to "
			+ "set columns like \"selected.\"")
	@ApiImplicitParams(
			@ApiImplicitParam(value="Array of SUID Keyed values", dataType="[Lorg.cytoscape.rest.internal.model.SUIDKeyValue;", paramType="body", required=true)
			)
	public Response updateColumnValues(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId,
			@ApiParam(allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType,
			@ApiParam(value="Column Name") @PathParam("columnName") String columnName,
			@ApiParam(value="Default Value. If this value is provided, all cells will be set to this.", required=false) @QueryParam("default") String defaultValue, 
			@ApiParam(hidden=true) final InputStream is) {
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

	@PUT
	@Path("/{tableType}")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Update default node/edge/network data table",
			notes="This API is for updating default node/edge/network data table.  New columns will be created if they "
			+ "do not exist in the target table.\n"
			+ "\n"
	+ "Current limitations:\n"
	+ "* Numbers are handled as Double\n"
	+ "* List column is not supported in this version\n"
	)
	@ApiImplicitParams(
			@ApiImplicitParam(value="The data with which to update the table.", dataType="org.cytoscape.rest.internal.model.UpdateTable", paramType="body", required=true)
			)
	public Response updateTable(
			@ApiParam() @PathParam("networkId") Long networkId, 
			@ApiParam(allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType,
			@ApiParam(allowableValues="local", required=false) @QueryParam("class") String tableClass,
			@ApiParam(hidden=true) final InputStream is
			) {
		
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

	@GET
	@Path("/{tableType}/rows/{primaryKey}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a row in the table", notes="Gets a row from the table specified by the `tableType` and `networkId` parameters where the value of the SUID of the row matches the specified `primaryKey` parameter.\n\nThe returned data is is a JSON representation of a row.\n\n```\n" + ROW_EXAMPLE + "```", response=org.cytoscape.rest.internal.model.CyRowModel.class)
	public String getRow(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId,
			@ApiParam(value="Table type", allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType,
			@ApiParam(value="Primary key") @PathParam("primaryKey") Long primaryKey) {
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

	@GET
	@Path("/{tableType}/rows/{primaryKey}/{columnName}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a value from a cell", notes="Return the value of a cell from the column specified with the `columnName` parameter in the table specified by the `tableType` and `networkId` parameters where the value of the SUID of the row matches the specified `primaryKey` parameter.\n\nReturns a JSON representation of a String, Boolean, Number, or List.")
	public Object getCell(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId,
			@ApiParam(value="Table type", allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType,
			@ApiParam(value="Name of primary key column") @PathParam("primaryKey") Long primaryKey,
			@ApiParam(value="Name of the column") @PathParam("columnName") String columnName) {
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

	@GET
	@Path("/{tableType}/rows")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get all rows in a table", notes="Returns all rows from the table speciefied using the `networkId` and `tableType` parameters. Returns a JSON representation of an array of rows.\n\n" + ROW_ARRAY_EXAMPLE, response=CyRowModel.class, responseContainer="List")
	public String getRows(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId,
			@ApiParam(value="Table Type", allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType, null);
		try {
			return this.serializer.serializeAllRows(table.getAllRows());
		} catch (IOException e) {
			throw getError("Could not serialize rows.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("/{tableType}/columns")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get all columns in a table", notes="Returns all the columns in the table specified by the `networkId` and `tableType` parameters.", response=CyColumnModel.class, responseContainer="List")
	public String getColumnNames(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId,
			@ApiParam(value="Table Type", allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyTable table = getTableByType(network, tableType, null);
		try {
			return this.serializer.serializeColumns(table.getColumns());
		} catch (IOException e) {
			throw getError("Could not serialize column names.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("/{tableType}/columns/{columnName}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get all values in a column", notes="Returns all the values for the column specified by the `columnType` parameter, in the table specified by the `networkId` and `tableType` parameters.", response=CyColumnValuesModel.class)
	public String getColumnValues(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId,
			@ApiParam(value="Table Type", allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType,
			@ApiParam(value="Column Name") @PathParam("columnName") String columnName) {
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

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get all tables assigned to the network", notes="Returns every table in the network specified by the `networkId` parameter.", response=CyTableWithRowsModel.class, responseContainer="List")
	public String getTables(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId) {
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
	@ApiOperation(value="Get a default table", notes="Returns the table specified by the `networkId` and 'tableType' parameters.", response=CyTableWithRowsModel.class)
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
	@ApiOperation(value="Get a table as CSV", notes="Returns a CSV representation of the table specified by the `networkId` and `tableType` parameters. All column names are included in the first row.")
	public String getTableAsCsv(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(allowableValues="defaultnode,defaultedge,defaultnetwork") @PathParam("tableType") String tableType) {
		return getTableString(networkId, tableType, ",");
	}
	
	@GET
	@Path("/{tableType}.tsv")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(value="Get a table as TSV (tab delimited text)", notes="Returns a TSV (tab delimited text) representation of the table specified by the `networkId` and `tableType` parameters. All column names are included in the first row.")
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