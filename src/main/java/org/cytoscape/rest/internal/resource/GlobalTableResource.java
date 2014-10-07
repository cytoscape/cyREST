package org.cytoscape.rest.internal.resource;

import java.util.Set;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.rest.internal.serializer.TableModule;

import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
@Path("/v1/tables")
public class GlobalTableResource extends AbstractResource {

	@Context
	@NotNull
	private CyTableFactory tableFactory;

	private final ObjectMapper tableObjectMapper;

	public GlobalTableResource() {
		super();
		this.tableObjectMapper = new ObjectMapper();
		this.tableObjectMapper.registerModule(new TableModule());
	}

	/**
	 * 
	 * @summary Get number of global tables
	 * 
	 * @return Number of global tables.
	 */
	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTableCount() {
		final Set<CyTable> globals = tableManager.getGlobalTables();
		return getNumberObjectString(JsonTags.COUNT, globals.size());
	}
}