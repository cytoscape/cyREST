package org.cytoscape.rest.internal.resource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.rest.internal.CyNetworkViewWriterFactoryManager;
import org.cytoscape.rest.internal.CyRESTConstants;
import org.cytoscape.rest.internal.datamapper.TableMapper;
import org.cytoscape.rest.internal.model.CountModel;
import org.cytoscape.rest.internal.model.CyColumnModel;
import org.cytoscape.rest.internal.model.CyTableWithRowsModel;
import org.cytoscape.rest.internal.serializer.TableModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.COLLECTIONS_TAG})
@Singleton
@Path("/v1/collections")
public class CollectionResource extends AbstractResource {

	private final ObjectMapper mapper;
	private final TableMapper tableMapper;

	private static final String RESOURCE_URN = "collections";

	@Override
	public String getResourceURI() {
		return RESOURCE_URN;
	}
	
	private final static Logger logger = LoggerFactory.getLogger(CollectionResource.class);
	
	@Override
	public Logger getResourceLogger() {
		return logger;
	}
	
	public static final int INTERNAL_METHOD_ERROR = 1;
	public static final int NOT_FOUND_ERROR= 2;
	public static final int SERIALIZATION_ERROR = 3;
	public static final int CX_SERVICE_UNAVAILABLE_ERROR = 4;
	private static final int INVALID_PARAMETER_ERROR = 5;
	
	private static final String COLLECTION_ASCII_ART =
			"Cytoscape can contain multiple Root Networks, each with their own Sub-Networks\n```\n" + 
			"── Root Network 1\n" + 
			"   ├── Sub-Network A\n" + 
			"   └── Sub-Network B\n" + 
			"── Root Network 2\n" + 
			"   └── Sub-Network C\n" + 
			"```";
	
	private static final String TABLE_TYPE_DESCRIPTION = "The `default` table contains data relevant to a Root Network. The `shared` table contains data shared by all Sub-Networks of a Root Network.";
	
	private static final String TABLE_TYPE_NOTES_DESCRIPTION = "Root network tables, particularly the shared table accessible by specifying the `tableType` parameter as `shared`, are available through Collection operations, and are not normally accessible in other contexts.";
	
	public CollectionResource() {
		super();
		mapper = new ObjectMapper();
		this.tableMapper = new TableMapper();
		this.mapper.registerModule(new TableModule());
	}

	private final Set<CyRootNetwork> getRootNetworks() {
		return networkManager.getNetworkSet().stream().map(net -> cyRootNetworkManager.getRootNetwork(net))
				.collect(Collectors.toSet());
	}

	/**
	 * Returns the root network for the SUID.
	 * @param suid
	 * @return
	 */
	private final CyRootNetwork getRootNetwork(final Long suid) {
		final Set<CyRootNetwork> roots = getRootNetworks();
		for (final CyRootNetwork root : roots) {
			if (root.getSUID().equals(suid)) {
				return root;
			}
		}
		return null;
	}

	private final Response getResponse(final Object val) {
		String result = null;
		try {
			result = mapper.writeValueAsString(val);
		} catch (Exception e) {
			//throw getError("Could not serialize result: " + val, e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					SERIALIZATION_ERROR, 
					"Could not serialize result: " + val, 
					getResourceLogger(), e);
		}
		return Response.ok(result).build();

	}

	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a count of all root networks.", notes="Returns a count of all root networks.")
	public CountModel getCollectionCount() {
		return new CountModel((long) getRootNetworks().size());
	}

	@GET
	
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@ApiOperation(value="Get one or all root networks.", notes="Returns all Root Networks as an array of SUIDs.\n\n"
			+ "If a valid Sub-Network SUID is specified in the `subsuid` parameter, the list will contain only the SUID of that Sub-Network's Root Network.")
	public Collection<Long> getCollectionsAsSUID(
			@ApiParam(value="Sub-Network SUID. If this parameter is used, the Root Network of this Sub-Network will be returned.\n\n" + COLLECTION_ASCII_ART, required=false) @QueryParam("subsuid") Long subsuid) {
		if(subsuid == null) {
			// Return all collection SUIDs
			return getRootNetworks().stream().map(root -> root.getSUID()).collect(Collectors.toSet());
		} else {
			// Return parent collection's SUID
			final CyNetwork subnetwork = networkManager.getNetwork(subsuid);
			if(subnetwork == null) {
				//throw new NotFoundException();
				throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
						getResourceURI(), 
						NOT_FOUND_ERROR, 
						"Could not find Network with SUID: " + subsuid, 
						getResourceLogger(), null);
			}
			
			final CyRootNetwork root = cyRootNetworkManager.getRootNetwork(subnetwork);
			if(root == null) {
				//throw new NotFoundException();
				throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
						getResourceURI(), 
						NOT_FOUND_ERROR, 
						"Could not find Root Network for Network with SUID: " + subsuid, 
						getResourceLogger(), null);
			} else {
				final List<Long> rootId = new ArrayList<>();
				rootId.add(root.getSUID());
				
				return rootId;
			}
		}
	}

	@GET
	@Path("/{networkId}.cx")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a collection as CX", notes="Returns a Root Network or Sub-Network in [CX format]("+CyRESTConstants.CX_FILE_FORMAT_LINK+").\n\nIf the `networkId` parameter is a Root Network, this returns that Root Network.\n\nIf the `networkId` parameter is a Sub-Network, this returns the Root Retwork that contains that Sub-Network.")
	public Response getCollectionAsCx(
			@ApiParam(value="Root Network or Sub-Network SUID. \n\n" + COLLECTION_ASCII_ART) @PathParam("networkId") Long networkId) {
		return getCX(networkId);
	}

	/**
	 * The below is literally an exact copy of getCollectionAsCx, and as such is hidden from Swagger
	 * using the hidden=true field in ApiOperation. It should still remain an endpoint to maintain API.
	 * @param networkId
	 * @return
	 */
	@GET
	@Path("/{networkId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a collection", notes="", hidden=true)
	public Response getCollection(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId) {
		return getCX(networkId);
	}

	@GET
	@Path("/{networkId}/subnetworks")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get SubNetworks", notes="Returns a list of SUIDs representing Sub-Networks that belong to the Root Network specified by the `networkId` parameter.")
	public Collection<Long> getSubnetworks(
			@ApiParam(value="Root Network SUID\n\n" + COLLECTION_ASCII_ART) @PathParam("networkId") Long networkId
			) {
		final CyRootNetwork root = getRootNetwork(networkId);
		final List<CySubNetwork> subnetworks = root.getSubNetworkList();
		final Set<Long> subnetIds = subnetworks.stream().map(subNet -> subNet.getSUID()).collect(Collectors.toSet());

		return subnetIds;
	}

	@GET
	@Path("/{networkId}/tables")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get Tables in a Root Network", notes="Returns a collection of tables belonging to the Root Network specified by the `networkId` parameter. ", response=CyTableWithRowsModel.class, responseContainer="list")
	public Response getRootTables(
			@ApiParam(value="Root Network SUID\n\n" + COLLECTION_ASCII_ART) @PathParam("networkId") Long networkId) {
		final CyRootNetwork root = getRootNetwork(networkId);
		final CyTable table = root.getDefaultNetworkTable();
		final CyTable shared = root.getSharedNetworkTable();
		final Set<CyTable> tables = new HashSet<>();
		tables.add(shared);
		tables.add(table);
		
		return getResponse(tables);
	}
	
	@GET
	@Path("/{networkId}/tables/{tableType}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a specific Table in a Root Network", 
		response=CyTableWithRowsModel.class, 
		notes="Returns either the `default` or `shared` table from the Root Network specified by the `networkId` parameter.\n\n" + TABLE_TYPE_NOTES_DESCRIPTION)
	public Response getRootTable(
			@ApiParam(value="Root Network SUID\n\n" + COLLECTION_ASCII_ART) @PathParam("networkId") Long networkId,
			@ApiParam(value=TABLE_TYPE_DESCRIPTION, allowableValues="default,shared") @PathParam("tableType") String tableType) {
		return getResponse(getTable(networkId, tableType));
	}
	
	@DELETE
	@Path("/{networkId}/tables/{tableType}/columns/{columnName}")
	@ApiOperation(value="Delete a column", notes="Deletes the column specified by the `columnName` parameter from the table specified the `tableType` parameter in the Root Network specified by the `networkId` parameter.\n\n"  + TABLE_TYPE_NOTES_DESCRIPTION)
	public Response deleteColumn(
			@ApiParam(value="Root Network SUID\n\n" + COLLECTION_ASCII_ART) @PathParam("networkId") Long networkId, 
			@ApiParam(value=TABLE_TYPE_DESCRIPTION, allowableValues="default,shared") @PathParam("tableType") String tableType,
			@ApiParam(value="Column Name") @PathParam("columnName") String columnName) {
		
		final CyTable table = getTable(networkId, tableType);
		if (table != null) {
			try {
				table.deleteColumn(columnName);
			} catch(Exception e) {
				//throw getError("Could not delete column: " + columnName, e, Response.Status.INTERNAL_SERVER_ERROR);
				throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
						getResourceURI(), 
						INTERNAL_METHOD_ERROR, 
						"Could not delete column: " + columnName, 
						getResourceLogger(), e);
			}
			return Response.ok().build();
		} else {
			//throw getError("No such table type: " + tableType, 
			//		new NullPointerException(), Response.Status.NOT_FOUND);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					NOT_FOUND_ERROR, 
					"No such Table type: " + tableType, 
					getResourceLogger(), null);
		}
	}
	
	@GET
	@Path("/{networkId}/tables/{tableType}/columns")
	@ApiOperation(value="Get a list of columns for a Root Network table", notes="Return a list of the columns in the table specified by the `tableType` parameter in the Root Network specified by the `networkId` parameter.\n\n"  + TABLE_TYPE_NOTES_DESCRIPTION, response=CyColumnModel.class, responseContainer="list")
	public Response getColumns(
			@ApiParam(value="Root Network SUID\n\n" + COLLECTION_ASCII_ART) @PathParam("networkId") Long networkId, 
			@ApiParam(value=TABLE_TYPE_DESCRIPTION, allowableValues="default,shared") @PathParam("tableType") String tableType) {
		final CyTable table = getTable(networkId, tableType);
		
		try {
			final String result = this.serializer.serializeColumns(table.getColumns());
			return Response.ok(result).build();
		} catch (Exception e) {
			//throw getError("Could not serialize column names.", 
			//		e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					SERIALIZATION_ERROR, 
					"Could not serialize column names.", 
					getResourceLogger(), null);
		}
	}
	
	private final CyTable getTable(Long networkId, final String tableType) {
		final CyRootNetwork root = getRootNetwork(networkId);
		if(tableType.equals("default")) {
			return root.getDefaultNetworkTable();
		} else if(tableType.equals("shared")) {
			return root.getSharedNetworkTable();
		} else {
			return null;
		}
	}
	
	@PUT
	@Path("/{networkId}/tables/{tableType}")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Update table values", notes="Updates the values in a table. New columns will be created if they do not exist in the target table.\n\n"  + TABLE_TYPE_NOTES_DESCRIPTION)
	@ApiImplicitParams(
			@ApiImplicitParam(value="The data with which to update the table.", dataType="org.cytoscape.rest.internal.model.UpdateTableModel", paramType="body", required=true)
			)
	public Response updateTable(
			@ApiParam(value="Root Network SUID\n\n" + COLLECTION_ASCII_ART) @PathParam("networkId") Long networkId, 
			@ApiParam(value=TABLE_TYPE_DESCRIPTION, allowableValues="default,shared") @PathParam("tableType") String tableType, 
			@ApiParam(hidden=true) final InputStream is) {
		final CyTable table = getTable(networkId, tableType);
		if(table == null) {
			//throw getError("No such table type", new NullPointerException(), Response.Status.NOT_FOUND);
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
					getResourceURI(), 
					NOT_FOUND_ERROR, 
					"No such Table type: " + tableType, 
					getResourceLogger(), null);
		}
		JsonNode rootNode = null;
		try {
			rootNode = mapper.readValue(is, JsonNode.class);
			
		} catch (Exception e) {
			//throw getError("Could not parse the input JSON for updating table because: " + e.getMessage(), 
			//		e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					INVALID_PARAMETER_ERROR, 
					"Could not parse the input JSON", 
					getResourceLogger(), e);
		}
		try {
			tableMapper.updateTableValues(rootNode, table);
		} catch (IllegalArgumentException e) {
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
				RESOURCE_URN, 
				INVALID_PARAMETER_ERROR, 
				e.getMessage(), 
				logger, e);
		} catch (TableMapper.ColumnNotFoundException e) {
			throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
				RESOURCE_URN, 
				NOT_FOUND_ERROR, 
				e.getMessage(), 
				logger, e);
		}
		return Response.ok().build();
	}

	private final Response getCX(final Long networkId) {
		
		final CyNetworkViewWriterFactory cxWriterFactory = 
				viewWriterFactoryManager.getFactory(CyNetworkViewWriterFactoryManager.CX_WRITER_ID);
		
		if (cxWriterFactory == null) {
			//throw getError("CX writer is not supported.  Please install CX Support App to use this API.",
			//		new RuntimeException(), Status.NOT_IMPLEMENTED);
			throw this.getCIWebApplicationException(Status.SERVICE_UNAVAILABLE.getStatusCode(), 
					getResourceURI(), 
					CX_SERVICE_UNAVAILABLE_ERROR, 
					"No CX Writer available. Please install CX Support App to use this API.", 
					getResourceLogger(), null);
		}
		
		CyRootNetwork root = null;

		if (!getCollectionsAsSUID(null).contains(networkId)) {
			// This is not a root network
			// Try find one
			final CyNetwork subNet = networkManager.getNetwork(networkId);
			if (subNet == null) {
				//throw getError("No such network: " + networkId, new IllegalArgumentException("Network does not exist"),
				//		Response.Status.NOT_FOUND);
				throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
						getResourceURI(), 
						NOT_FOUND_ERROR, 
						"Network not found: "+ networkId, 
						getResourceLogger(), null);
			}
			root = cyRootNetworkManager.getRootNetwork(subNet);

		} else {
			root = getRootNetwork(networkId);
			if (root == null) {
				//throw getError("No such Collection: " + networkId,
				//		new IllegalArgumentException("Collection does not exist"), Response.Status.NOT_FOUND);
				throw this.getCIWebApplicationException(Status.NOT_FOUND.getStatusCode(), 
						getResourceURI(), 
						NOT_FOUND_ERROR, 
						"Collection not found: "+ networkId, 
						getResourceLogger(), null);
			}
		}
		return Response.ok(getNetworkViewsAsCX(root)).build();
	}

	private final String getNetworkViewsAsCX(final CyRootNetwork root) {
		final CyNetworkViewWriterFactory cxWriterFactory = 
				viewWriterFactoryManager.getFactory(CyNetworkViewWriterFactoryManager.CX_WRITER_ID);
		
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		CyWriter writer = cxWriterFactory.createWriter(stream, root.getSubNetworkList().get(0));
		String jsonString = null;
		try {
			writer.run(null);
			jsonString = stream.toString("UTF-8");
			stream.close();
		} catch (Exception e) {
			//throw getError("Failed to serialize network into CX: " + root, e, Response.Status.INTERNAL_SERVER_ERROR);
			throw this.getCIWebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					getResourceURI(), 
					SERIALIZATION_ERROR, 
					"Failed to serialize network into CX: " + root, 
					getResourceLogger(), e);
		}
		return jsonString;
	}
}
