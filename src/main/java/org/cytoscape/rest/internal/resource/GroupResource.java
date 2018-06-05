package org.cytoscape.rest.internal.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.group.CyGroup;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.rest.internal.datamapper.GroupMapper;
import org.cytoscape.rest.internal.model.CountModel;
import org.cytoscape.rest.internal.model.GroupModel;
import org.cytoscape.rest.internal.model.GroupSUIDModel;
import org.cytoscape.rest.internal.serializer.GroupModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Singleton
@Path("/v1/networks/{networkId}/groups")
@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.GROUPS_TAG})
public class GroupResource extends AbstractResource {

	private static final String RESOURCE_URN = "networks:groups";

	@Override
	public String getResourceURI() {
		return RESOURCE_URN;
	}
	
	private final static Logger logger = LoggerFactory.getLogger(GroupResource.class);
	
	@Override
	public Logger getResourceLogger() {
		return logger;
	}

	private static final int NOT_FOUND_ERROR= 1;
	
	private final GroupMapper mapper;

	@Inject
	private CyGroupFactory groupFactory;

	@Inject
	private CyGroupManager groupManager;

	public GroupResource() {
		super();
		this.mapper = new GroupMapper();
	}

	@GET
	
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get all groups in the network",
			notes="Returns a list of all the groups in the network specified by the `networkId` parameter.",
			response=GroupModel.class, responseContainer="List"
			)
	public String getAllGroups(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(NOT_FOUND_ERROR, networkId);
		final Set<CyGroup> groups = groupManager.getGroupSet(network);
		try {
			ObjectMapper groupMapper = new ObjectMapper();
			groupMapper.registerModule(new GroupModule(network));
			return groupMapper.writeValueAsString(groups);
		} catch (JsonProcessingException e) {
			throw getError("Could not serialize groups.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}


	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get number of groups in the network",
		notes="Returns the number of groups in the network"
			)
	public CountModel getGroupCount(
			@ApiParam(value="Network SUID")@PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(NOT_FOUND_ERROR, networkId);
		return new CountModel(Integer.valueOf(groupManager.getGroupSet(network).size()).longValue());
	}

	@GET
	@Path("/{groupNodeId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get a group by SUID",
		notes="Returns the group specified by the `groupNodeId` and `networkId` parameters.",
		response=GroupModel.class
	)
	public String getGroup(
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId, 
			@ApiParam(value="SUID of the Node representing the Group") @PathParam("groupNodeId") Long groupNodeId) {
		final CyNetwork network = getCyNetwork(NOT_FOUND_ERROR, networkId);
		final CyGroup group = getGroupById(networkId, groupNodeId);
		try {
			ObjectMapper groupMapper = new ObjectMapper();
			groupMapper.registerModule(new GroupModule(network));
			return groupMapper.writeValueAsString(group);
		} catch (JsonProcessingException e) {
			throw getError("Could not serialize Group.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@DELETE
	
	@ApiOperation(value="Delete all groups in the network",
			notes="Deletes all groups in the network specified by `networkId` parameter. The nodes and edges that the groups contained will remain present in the network, however the nodes used to identify the Groups will be deleted.")
	public void deleteAllGroups(
			@ApiParam(value = "SUID of the Network") @PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(NOT_FOUND_ERROR, networkId);
		final Set<CyGroup> groups = groupManager.getGroupSet(network);
		try {
			for (final CyGroup group : groups) {
				groupManager.destroyGroup(group);
			}
		} catch (Exception e) {
			throw getError("Could not delete group.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@DELETE
	@Path("/{groupNodeId}")
	@ApiOperation(value="Delete a group", 
			notes="Deletes the group specified by the `groupNodeId` and `networkId` parameters. The nodes and edges that the group contained will remain present in the network, however the node used to identify the Group will be deleted.")
	public void deleteGroup(
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId, 
			@ApiParam(value="SUID of the Node representing the Group")@PathParam("groupNodeId") Long groupNodeId) {
		final CyGroup group = getGroupById(networkId, groupNodeId);
		try {
			groupManager.destroyGroup(group);
		} catch (Exception e) {
			throw getError("Could not delete group.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("/{groupNodeId}/expand")
	@ApiOperation(value="Expand group",
			notes="Expands the group specified by the `groupNodeId` and `networkId` parameters.")
	@ApiResponses(value = { 
			  @ApiResponse(code = 204, message = "Group expanded"),
			  @ApiResponse(code = 500, message=  "Failed to expand group")
	}) 
	public Response expandGroup(
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId, 
			@ApiParam(value="SUID of the Node representing the Group") @PathParam("groupNodeId") Long groupNodeId) {
		return toggle(networkId, groupNodeId, false);
	}

	@GET
	@Path("/{groupNodeId}/collapse")
	@ApiOperation(value="Collapse group",
			notes="Collapses the group specified by the `groupNodeId` and `networkId` parameters.")
	@ApiResponses(value = { 
			  @ApiResponse(code = 204, message = "Group collapsed"),
			  @ApiResponse(code = 500, message=  "Failed to collapse group")
	}) 
	public Response collapseGroup(
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId, 
			@ApiParam(value="SUID of the Node representing the Group") @PathParam("groupNodeId") Long groupNodeId) {
		return toggle(networkId, groupNodeId, true);
	}

	private final Response toggle(final Long networkId, final Long suid, boolean collapse) {
		final CyGroup group = getGroupById(networkId, suid);
		final CyNetwork network = getCyNetwork(NOT_FOUND_ERROR, networkId);
		try {
			if (collapse) {
				group.collapse(network);
			} else {
				group.expand(network);
			}
		} catch (Exception e) {
			throw getError("Could not toggle group state. Collapse: " + collapse, e, Response.Status.INTERNAL_SERVER_ERROR);
		}
		return Response.noContent().build();
	}

	private final CyGroup getGroupById(final Long networkId, final Long suid) {
	
		final CyNetwork network = getCyNetwork(NOT_FOUND_ERROR, networkId);
	
		//Fun fact: we need to scan the group set to get the group from an SUID, because an un-collapsed group node 
		//won't be found in the the network. This works whether the group is collapsed or not. 
		Set<CyGroup> groupSet = groupManager.getGroupSet(network);
		
		for (CyGroup group : groupSet) {
			if (group.getGroupNode().getSUID().longValue() == suid.longValue()) {
				return group;
			}
		}
		throw new NotFoundException("Could not find group.");
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Create a new group",
		notes="Create a new group in the network specified by the parameter `networkId`. The contents are specified the message body.",
		response=GroupSUIDModel.class)
	@ApiImplicitParams(
			@ApiImplicitParam(value="New Group name and contents", dataType="org.cytoscape.rest.internal.model.NewGroupParameterModel", paramType="body", required=true)
	)
	public String createGroup(
			@ApiParam(value="SUID of the Network") @PathParam("networkId") Long networkId, 
			@ApiParam(hidden=true) final InputStream is) {
		final CyNetwork network = getCyNetwork(NOT_FOUND_ERROR, networkId);
		final ObjectMapper objMapper = new ObjectMapper();

		JsonNode rootNode = null;
		try {
			rootNode = objMapper.readValue(is, JsonNode.class);
		} catch (IOException ex) {
			throw getError("Could not create JSON root node.", ex, Response.Status.INTERNAL_SERVER_ERROR);
		}
		try {
			final CyGroup newGroup = mapper.createGroup(rootNode, groupFactory, network);
			return getNumberObjectString("groupSUID", newGroup.getGroupNode().getSUID());
		} catch (Exception e) {
			throw getError("Could not create group.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
}