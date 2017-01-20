package org.cytoscape.rest.internal.task;

import io.swagger.annotations.ExternalDocs;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.ReaderListener;
import io.swagger.models.Swagger;

@SwaggerDefinition(
		info = @Info(
				description = "A RESTful service for accessing Cytoscape 3.",
				version = "V2.0.0",
				title = "CyREST API"
				//termsOfService = "http://theweatherapi.io/terms.html",
				// contact = @Contact(
				//   name = "Rain Moore", 
				//    email = "rain.moore@theweatherapi.io", 
				//    url = "http://theweatherapi.io"
				// ),
				// license = @License(
				//    name = "Apache 2.0", 
				//    url = "http://www.apache.org/licenses/LICENSE-2.0"
				// )
				),
		//Be wary of this host parameter if you are using BeanConfig; use one or the other, as they will
		//cause conflicts.
		//host = "localhost:1234",
		basePath = "/",
		consumes = {"application/json", "application/xml"},
		produces = {"application/json", "application/xml"},
		schemes = {SwaggerDefinition.Scheme.HTTP},
		tags = 
	{
			@Tag(name = CyRESTSwaggerConfig.SESSION_TAG),
			
	}, 
	externalDocs = @ExternalDocs(value = "Cytoscape", url = "http://cytoscape.org/")
		)
public class CyRESTSwaggerConfig implements ReaderListener
{

	public static final String SESSION_TAG = "Session";

	@Override
	public void beforeScan(Reader arg0, Swagger arg1) 
	{
		
	}

	public void afterScan(Reader reader, Swagger swagger)
	{
	}
}
