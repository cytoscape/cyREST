package org.cytoscape.rest.internal.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Resource to provide metrics from Prometheus. 
 * 
 */
@Api(tags = {CyRESTSwagger.CyRESTSwaggerConfig.REST_SERVICE_TAG,CyRESTSwagger.CyRESTSwaggerConfig.CYTOSCAPE_SYSTEM_TAG})
@Singleton
@Path("/v1/metrics")
public class MetricsResource extends AbstractResource {

	private CollectorRegistry registry;


	public MetricsResource() {
		this(CollectorRegistry.defaultRegistry);
	}


	public MetricsResource(CollectorRegistry registry) {
		this.registry = registry;
	}

	@GET
	@Produces(TextFormat.CONTENT_TYPE_004)
	@ApiOperation(value="Prometheus metrics for CyREST")
	public StreamingOutput get(@Context UriInfo ui) {
		return new StreamingOutput() {
			@Override
			public void write(OutputStream output) 
					throws IOException, WebApplicationException {
				OutputStreamWriter writer = new OutputStreamWriter(output);
				try {
					TextFormat.write004(writer, registry.filteredMetricFamilySamples(parse(ui)));
					writer.flush();
				} finally {
					writer.close();
				}
			}
		}; 
	}

	private Set<String> parse(UriInfo ui) {
		MultivaluedMap<String, String> map = ui.getQueryParameters();
		List<String> includedParam =  map.get("name");
		if (includedParam == null) {
			return Collections.emptySet();
		} else {
			return new HashSet<String>(includedParam);
		}
	}
}
