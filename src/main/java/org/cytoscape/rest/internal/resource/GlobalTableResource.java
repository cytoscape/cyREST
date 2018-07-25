package org.cytoscape.rest.internal.resource;

import java.util.Set;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.rest.internal.model.CountModel;
import org.cytoscape.rest.internal.serializer.TableModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Singleton
@Path("/v1/tables")
@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.TABLES_TAG})
public class GlobalTableResource extends AbstractResource {

	private static final String RESOURCE_URN = "tables";

	@Override
	public String getResourceURI() {
		return RESOURCE_URN;
	}
	
	private final static Logger logger = LoggerFactory.getLogger(GlobalTableResource.class);
	
	@Override
	public Logger getResourceLogger() {
		return logger;
	}
	
	@Inject
	@NotNull
	private CyTableFactory tableFactory;

	private final ObjectMapper tableObjectMapper;

	public GlobalTableResource() {
		super();
		this.tableObjectMapper = new ObjectMapper();
		this.tableObjectMapper.registerModule(new TableModule());
	}

	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get number of global tables", 
			notes="Returns the number of global tables.")
	public CountModel getTableCount() {
		final Set<CyTable> globals = tableManager.getGlobalTables();
		return new CountModel(Integer.valueOf(globals.size()).longValue());
	}
}