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
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.rest.internal.datamapper.GroupMapper;
import org.cytoscape.rest.internal.model.Count;
import org.cytoscape.rest.internal.serializer.GroupModule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Singleton
@Path("/v1/networks/{networkId}/groups")
@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.TABLES_TAG})
public class GroupResource extends AbstractResource {

	private final ObjectMapper groupMapper;
	private final GroupMapper mapper;

	@Inject
	private CyGroupFactory groupFactory;

	@Inject
	private CyGroupManager groupManager;

	public GroupResource() {
		super();
		this.groupMapper = new ObjectMapper();
		this.groupMapper.registerModule(new GroupModule());
		this.mapper = new GroupMapper();
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get all groups in the network",
			notes="Returns a list of all groups in the network"
			)
	public String getAllGroups(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		final Set<CyGroup> groups = groupManager.getGroupSet(network);
		try {
			return this.groupMapper.writeValueAsString(groups);
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
	public Count getGroupCount(
			@ApiParam(value="Network SUID")@PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		return new Count(Integer.valueOf(groupManager.getGroupSet(network).size()).longValue());
	}

	@GET
	@Path("/{groupNodeId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get group for a node",
		notes="Returns a group the node belongs to."
			)
	public String getGroup(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Group Node SUID") @PathParam("groupNodeId") Long nodeId) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyNode node = network.getNode(nodeId);
		if (node == null) {
			throw new NotFoundException("Could not find the node with SUID: " + nodeId);
		}

		final CyGroup group = groupManager.getGroup(node, network);
		if (group == null) {
			throw new NotFoundException("Could not find group.");
		}
		try {
			return groupMapper.writeValueAsString(group);
		} catch (JsonProcessingException e) {
			throw getError("Could not serialize Group.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@DELETE
	@Path("/")
	@ApiOperation(value="Delete all groups in the network")
	public void deleteAllGroups(
			@ApiParam(value = "Network SUID") @PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
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
	@ApiOperation(value="Delete a group")
	public void deleteGroup(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Group Node SUID")@PathParam("groupNodeId") Long groupNodeId) {
		final CyGroup group = getGroupById(networkId, groupNodeId);
		try {
			groupManager.destroyGroup(group);
		} catch (Exception e) {
			throw getError("Could not delete group.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("/{groupNodeId}/expand")
	@ApiOperation(value="Expand group nodes")
	public void expandGroup(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Group Node SUID") @PathParam("groupNodeId") Long groupNodeId) {
		toggle(networkId, groupNodeId, false);
	}

	@GET
	@Path("/{groupNodeId}/collapse")
	@ApiOperation(value="Collapse group nodes")
	public void collapseGroup(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, 
			@ApiParam(value="Group Node SUID") @PathParam("groupNodeId") Long groupNodeId) {
		toggle(networkId, groupNodeId, true);
	}

	private final void toggle(final Long networkId, final Long suid, boolean collapse) {
		final CyGroup group = getGroupById(networkId, suid);
		final CyNetwork network = getCyNetwork(networkId);
		try {
			if (collapse) {
				group.collapse(network);
			} else {
				group.expand(network);
			}
		} catch (Exception e) {
			throw getError("Could not toggle group state. Collapse: " + collapse, e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	private final CyGroup getGroupById(final Long networkId, final Long suid) {
		final CyNetwork network = getCyNetwork(networkId);
		CyNode node = network.getNode(suid);
		if (node == null) {
			node = ((CySubNetwork) network).getRootNetwork().getNode(suid);
			if (node == null)
				throw new NotFoundException("Could not find the node with SUID: " + suid);
		}

		final CyGroup group = groupManager.getGroup(node, network);
		if (group == null) {
			throw new NotFoundException("Could not find group.");
		}
		return group;
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Create a new group",
		notes="Create a new group from a list of nodes.  The Body should be in the following format:\n"
		+ "\n"
		+ "```\n"
		+ "{\n"
		+ "  \"name\": (New group node name),\n"
		+ "  \"nodes\": [\n"
		+ "    nodeSUID1, nodeSUID2, ...\n"
		+ "  ]\n"
		+ "}\n"
		+ "```"
			)
	public String createGroup(
			@ApiParam(value="Network SUID") @PathParam("networkId") Long networkId, final InputStream is) {
		final CyNetwork network = getCyNetwork(networkId);
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