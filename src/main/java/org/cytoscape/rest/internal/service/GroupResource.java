package org.cytoscape.rest.internal.service;

import java.io.ByteArrayOutputStream;
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
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.rest.internal.datamapper.GroupMapper;
import org.cytoscape.rest.internal.serializer.GroupModule;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
@Path("/v1/networks")
public class GroupResource extends AbstractDataService {

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

	@GET
	@Path("/{networkId}/groups/count")
	@Produces(MediaType.APPLICATION_JSON)
	public String getGroupCount(@PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		return getNumberObjectString("groupCount", groupManager.getGroupSet(network).size());
	}

	@GET
	@Path("/{networkId}/groups")
	@Produces(MediaType.APPLICATION_JSON)
	public String getGroups(@PathParam("networkId") Long networkId) {
		final CyNetwork network = getCyNetwork(networkId);
		final Set<CyGroup> groups = groupManager.getGroupSet(network);
		try {
			return this.groupMapper.writeValueAsString(groups);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new WebApplicationException(e, 500);
		}
	}

	@GET
	@Path("/{networkId}/groups/{suid}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getGroup(@PathParam("networkId") Long networkId, @PathParam("suid") Long suid) {
		final CyNetwork network = getCyNetwork(networkId);
		final CyNode node = network.getNode(suid);
		if (node == null) {
			throw new NotFoundException("Could not find the node with SUID: " + suid);
		}

		final CyGroup group = groupManager.getGroup(node, network);
		if (group == null) {
			throw new NotFoundException("Could not find group.");
		}
		try {
			return groupMapper.writeValueAsString(group);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new WebApplicationException(e, 500);
		}
	}

	@DELETE
	@Path("/{networkId}/groups")
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

	@DELETE
	@Path("/{networkId}/groups/{suid}")
	public void deleteGroup(@PathParam("networkId") Long networkId, @PathParam("suid") Long suid) {
		final CyGroup group = getGroupById(networkId, suid);
		try {
			groupManager.destroyGroup(group);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e, 500);
		}
	}

	@GET
	@Path("/{networkId}/groups/{suid}/expand")
	public void expandGroup(@PathParam("networkId") Long networkId, @PathParam("suid") Long suid) {
		toggle(networkId, suid, false);
	}

	@GET
	@Path("/{networkId}/groups/{suid}/collapse")
	public void collapseGroup(@PathParam("networkId") Long networkId, @PathParam("suid") Long suid) {
		toggle(networkId, suid, true);
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
			node = ((CySubNetwork)network).getRootNetwork().getNode(suid);
			if(node == null)
				throw new NotFoundException("Could not find the node with SUID: " + suid);
		}

		final CyGroup group = groupManager.getGroup(node, network);
		if (group == null) {
			throw new NotFoundException("Could not find group.");
		}
		return group;
	}

	@POST
	@Path("/{id}/groups")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createGroup(@PathParam("id") Long id, final InputStream is) throws Exception {
		final CyNetwork network = getCyNetwork(id);
		final ObjectMapper objMapper = new ObjectMapper();
		final JsonNode rootNode = objMapper.readValue(is, JsonNode.class);
		try {
			final CyGroup newGroup = mapper.createGroup(rootNode, groupFactory, network);
			return getNumberObjectString(CyIdentifiable.SUID, newGroup.getGroupNode().getSUID());
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e, 500);
		}
	}
}
