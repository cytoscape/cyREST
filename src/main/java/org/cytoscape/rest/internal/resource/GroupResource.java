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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.group.CyGroup;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.rest.internal.datamapper.GroupMapper;
import org.cytoscape.rest.internal.serializer.GroupModule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
@Path("/v1/networks/{networkId}/groups")
public class GroupResource extends AbstractResource {

	private final ObjectMapper groupMapper;
	private final GroupMapper mapper;

	@Context
	private CyGroupFactory groupFactory;

	@Context
	private CyGroupManager groupManager;

	public GroupResource() {
		super();
		this.groupMapper = new ObjectMapper();
		this.groupMapper.registerModule(new GroupModule());
		this.mapper = new GroupMapper();
	}

	/**
	 * @summary Get all groups in the network
	 * 
	 * @param networkId
	 *            Network SUID
	 * 
	 * @return List of all groups in the network
	 * 
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllGroups(@PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		final Set<CyGroup> groups = groupManager.getGroupSet(network);
		try {
			return this.groupMapper.writeValueAsString(groups);
		} catch (JsonProcessingException e) {
			throw getError("Could not serialize groups.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}


	/**
	 * 
	 * @summary Get number of groups in the network
	 * 
	 * @param networkId
	 *            Network SUID
	 * 
	 * @return Number of groups in the network
	 */
	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	public String getGroupCount(@PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		return getNumberObjectString(JsonTags.COUNT, groupManager.getGroupSet(network).size());
	}

	/**
	 * @summary Get group for a node
	 * 
	 * @param networkId
	 *            Networks SUID
	 * @param nodeId
	 *            Node SUID
	 * 
	 * @return A group where the node belongs to
	 */
	@GET
	@Path("/{nodeId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getGroup(@PathParam("networkId") Long networkId, @PathParam("nodeId") Long nodeId) {
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

	/**
	 * @summary Delete all groups in the network
	 * 
	 * @param networkId
	 *            Network SUID
	 * 
	 */
	@DELETE
	@Path("/")
	public void deleteAllGroups(@PathParam("networkId") Long networkId) {
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

	/**
	 * 
	 * @summary Delete a group
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param groupNodeId
	 *            Group node SUID
	 */
	@DELETE
	@Path("/{groupId}")
	public void deleteGroup(@PathParam("networkId") Long networkId, @PathParam("groupNodeId") Long groupNodeId) {
		final CyGroup group = getGroupById(networkId, groupNodeId);
		try {
			groupManager.destroyGroup(group);
		} catch (Exception e) {
			throw getError("Could not delete group.", e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * @summary Expand group nodes
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param groupNodeId
	 *            Group node SUID
	 * 
	 */
	@GET
	@Path("/{groupNodeId}/expand")
	public void expandGroup(@PathParam("networkId") Long networkId, @PathParam("groupNodeId") Long groupNodeId) {
		toggle(networkId, groupNodeId, false);
	}

	/**
	 * @summary Collapse group nodes
	 * 
	 * @param networkId
	 *            Network SUID
	 * @param groupNodeId
	 *            Group node SUID
	 * 
	 */
	@GET
	@Path("/{groupNodeId}/collapse")
	public void collapseGroup(@PathParam("networkId") Long networkId, @PathParam("groupNodeId") Long groupNodeId) {
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

	/**
	 * Create a new group from a list of nodes.  The Body should be in the following format:
	 * 
	 * <pre>
	 * 	{
	 * 		"name": (New group node name),
	 * 		"nodes": [
	 * 			nodeSUID1, nodeSUID2, ...
	 * 		]
	 * 	}
	 * </pre>
	 * 
	 * 
	 * @summary Create a new group
	 * 
	 * @param networkId
	 *            Network SUID
	 * 
	 * @return New group node's SUID
	 * 
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createGroup(@PathParam("networkId") Long networkId, final InputStream is) {
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