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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.cytoscape.group.CyGroup;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.rest.internal.datamapper.GroupMapper;
import org.cytoscape.rest.internal.serializer.GroupModule;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
	 * Get all groups for a network
	 * 
	 * @param networkId
	 *            Network SUID
	 * 
	 * @return List of all groups for the network
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
			e.printStackTrace();
			throw new WebApplicationException(e, 500);
		}
	}

	/**
	 * Get number of groups for a network
	 * 
	 * @param networkId
	 *            Network SUID
	 * 
	 * @return Number of groups for the network
	 */
	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Integer getGroupCount(@PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		return groupManager.getGroupSet(network).size();
	}

	/**
	 * Get group for a node
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
			e.printStackTrace();
			throw new WebApplicationException("Could not serialize Group.", e, 500);
		}
	}

	/**
	 * Delete all groups for a network
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
			e.printStackTrace();
			throw new WebApplicationException(e, 500);
		}
	}

	/**
	 * 
	 * Delete a group
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
			e.printStackTrace();
			throw new WebApplicationException("Could not delete a group.", e, 500);
		}
	}

	/**
	 * Expand group nodes
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
	 * Collapse group nodes
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
			e.printStackTrace();
			throw new WebApplicationException(e, 500);
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
	 * Create a new group node
	 * 
	 * @param networkId
	 *            Network SUID
	 * 
	 * @return New group node's SUID
	 * 
	 * @throws Exception
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Long createGroup(@PathParam("networkId") Long networkId, final InputStream is) throws JsonParseException,
			JsonMappingException, IOException {
		final CyNetwork network = getCyNetwork(networkId);
		final ObjectMapper objMapper = new ObjectMapper();
		final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
		try {
			final CyGroup newGroup = mapper.createGroup(rootNode, groupFactory, network);
			return newGroup.getGroupNode().getSUID();
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException("Could not create new group.", e, 500);
		}
	}
}