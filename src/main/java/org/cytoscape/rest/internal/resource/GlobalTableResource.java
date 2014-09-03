package org.cytoscape.rest.internal.resource;

import java.io.InputStream;
import java.util.Set;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.POST;
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
	@Produces(MediaType.TEXT_PLAIN)
	public Integer getTableCount() {
		final Set<CyTable> globals = tableManager.getGlobalTables();
		return globals.size();
	}

	/**
	 * Create global table.
	 * 
	 * @param is
	 * 
	 * TODO: Implement this.
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createTable(final InputStream is) {
		throw new NotSupportedException("CREATE method for Global table creation is not implemented.");
//		final CyTable newTable = tableFactory.createTable("foo", "SUID", Long.class, true, true);
//		tableManager.addTable(newTable);
//		
//		return getNumberObjectString("tableSUID", newTable.getSUID());
	}
}