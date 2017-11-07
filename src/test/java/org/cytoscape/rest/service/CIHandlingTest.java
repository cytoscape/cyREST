package org.cytoscape.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Application;

import org.cytoscape.ci.model.CIError;
import org.cytoscape.ci.model.CIResponse;
import org.cytoscape.rest.internal.resource.CIResponseFilter;
import org.cytoscape.rest.internal.resource.CyExceptionMapper;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.spi.TestContainer;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Test;

import com.eclipsesource.jaxrs.provider.gson.GsonProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class CIHandlingTest extends BasicResourceTest {
	
	private ObjectMapper mapper = new ObjectMapper();
	
	public CIHandlingTest(){
		super();
	}
	@Override
	protected Application configure() {
		return new ResourceConfig(CIHandlingResource.class);
	}
	
	@Override
	protected TestContainerFactory getTestContainerFactory()
			throws TestContainerException {
		return new TestContainerFactory() {
			@Override
			public TestContainer create(final URI baseUri,
					DeploymentContext arg1) throws IllegalArgumentException {
				return new TestContainer() {
					private HttpServer server;

					@Override
					public ClientConfig getClientConfig() {
						return null;
					}

					@Override
					public URI getBaseUri() {
						return baseUri;
					}

					@Override
					public void start() {
						try {	
							final Set<Class<?>> resourceClasses = new HashSet<Class<?>>();
							
							resourceClasses.add(CIHandlingResource.class);
							resourceClasses.add(CIResponseFilter.class);
							resourceClasses.add(CyExceptionMapper.class);
							final ResourceConfig rc = new ResourceConfig();

							Injector injector = Guice.createInjector(binder);

							for (Class<?> clazz : resourceClasses){
								Object instance = injector.getInstance(clazz);
								rc.register(instance);
							}
							
							// Note: This should match the POJO/JSON serializer we use for the OSGi JAX RS Connector
							//rc.register(JacksonFeature.class); //Old feature
							rc.register(GsonProvider.class);

							this.server = GrizzlyHttpServerFactory
									.createHttpServer(baseUri, rc);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void stop() {
						this.server.stop();
					}
				};

			}
		};
	}
	
	@Test
	public void testWrappedResource() throws JsonProcessingException, IOException {
			String response = target("/ciresource/success").request().get(String.class);
			final JsonNode root = mapper.readTree(response);
			assertTrue(root.has("data"));
			JsonNode dataField = root.get("data");
			assertEquals("Hello!", dataField.get("message").asText());
			assertTrue(root.has("errors"));
			assertEquals(0, root.get("errors").size());
	}

	@Test
	public void testWrappedEmptyResource() throws JsonProcessingException, IOException {
			String response = target("/ciresource/successFromEmptyResponse").request().get(String.class);
			final JsonNode root = mapper.readTree(response);
			
			assertTrue(root.has("errors"));
			assertEquals(0, root.get("errors").size());
	}
	
	@Test
	public void testWrappedObjectResource() throws JsonProcessingException, IOException {
			String response = target("/ciresource/successFromEntityResponse").request().get(String.class);
			final JsonNode root = mapper.readTree(response);
			assertTrue(root.has("data"));
			JsonNode dataField = root.get("data");
			assertEquals("Hello!", dataField.get("message").asText());
			
			assertTrue(root.has("errors"));
			assertEquals(0, root.get("errors").size());
	}
	
	/**
	 * This is the expected behavior for wrapping strings. Default for JAX-RS is to pass through the value, but since
	 * we're embedding in other JSON, it gets enclosed in quotations. So, if you intend to wrap manually generated JSON, 
	 * you'll have to use some other method.
	 * 
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	@Test
	public void testWrappedStringResource() throws JsonProcessingException, IOException {
			String response = target("/ciresource/successFromStringResponse").request().get(String.class);
			final JsonNode root = mapper.readTree(response);
			assertTrue(root.has("data"));
			JsonNode dataField = root.get("data");
			assertEquals("Hello!", dataField.asText());
			assertTrue(root.has("errors"));
			assertEquals(0, root.get("errors").size());
	}
	
	@Test
	public void testUncaughtException() {
		try {
			String response = target("/ciresource/fail").request().get(String.class);
			} catch (InternalServerErrorException e) {
				CIResponse<?> ciResponse = e.getResponse().readEntity(CIResponse.class);
				Map<String, Object> object = (Map<String, Object>) ciResponse.data;
				assertEquals(0, object.size());
				assertEquals(1, ciResponse.errors.size());
				CIError error = ciResponse.errors.get(0);
				assertTrue(error.message.startsWith("Uncaught exception while processing resource ["));
				assertTrue(error.message.endsWith("]: Kaboom."));
				assertEquals(new Integer(500), error.status);
				assertEquals("dummyLogLocation", error.link.toString());
				assertEquals("urn:cytoscape:ci:cyrest-core:v1:error-handling:errors:0", error.type);
			
			}
	}
	
	@Test
	public void testWrapped404() {
		try {
			String response = target("/ciresource/fail404").request().get(String.class);
		} catch (NotFoundException e) {
			CIResponse<?> ciResponse = e.getResponse().readEntity(CIResponse.class);
			Map<String, Object> object = (Map<String, Object>) ciResponse.data;
			assertEquals(0, object.size());
			assertEquals(1, ciResponse.errors.size());
			CIError error = ciResponse.errors.get(0);
			System.out.println(error.message);
			assertEquals("Not Found", error.message);
			assertEquals(new Integer(404), error.status);
			assertEquals("dummyLogLocation", error.link.toString());
			assertEquals("urn:cytoscape:ci:cyrest-core:v1:ciresponsefilter:0", error.type);
		}
			
	}
	
	
	@Test
	public void testExplicitCIError() {
		try {
		String response = target("/ciresource/failwithresource").request().get(String.class);
		} catch (ServerErrorException e) {
			CIResponse<?> ciResponse = e.getResponse().readEntity(CIResponse.class);
			System.out.println(ciResponse.errors.size());
			Map<String, Object> object = (Map<String, Object>) ciResponse.data;
			assertEquals(0, object.size());
			assertEquals(1, ciResponse.errors.size());
			CIError error = ciResponse.errors.get(0);
			assertEquals("Intentional fail to report with CI Resource.", error.message);
			assertEquals(new Integer(500), error.status);
			assertEquals("http://www.google.ca", error.link.toString());
			assertEquals("urn:cytoscape:ci:ci-wrap-test:v1:fail-with-ci-error:errors:1", error.type);
		}
	}
	
	@Test
	public void testExplicitCIError501() {
		try {
		String response = target("/ciresource/failwithresource501").request().get(String.class);
		} catch (ServerErrorException e) {
			CIResponse<?> ciResponse = e.getResponse().readEntity(CIResponse.class);
			System.out.println(e.getResponse().getStatus());
			System.out.println(ciResponse.errors.size());
			Map<String, Object> object = (Map<String, Object>) ciResponse.data;
			assertEquals(0, object.size());
			assertEquals(1, ciResponse.errors.size());
			CIError error = ciResponse.errors.get(0);
			assertEquals("Intentional fail to report with CI Resource.", error.message);
			assertEquals(new Integer(500), error.status);
			assertEquals("http://www.google.ca", error.link.toString());
			assertEquals("urn:cytoscape:ci:ci-wrap-test:v1:fail-with-ci-error:errors:1", error.type);
		}
	}
	
	@Test
	public void testExplicitCIErrorAutoLink() {
		try {
		String response = target("/ciresource/failwithautolinkedresource").request().get(String.class);
		} catch (InternalServerErrorException e) {
			CIResponse<?> ciResponse = e.getResponse().readEntity(CIResponse.class);
			System.out.println(ciResponse.errors.size());
			Map<String, Object> object = (Map<String, Object>) ciResponse.data;
			assertEquals(0, object.size());
			assertEquals(1, ciResponse.errors.size());
			CIError error = ciResponse.errors.get(0);
			assertEquals("Intentional fail to report with CI Resource.", error.message);
			assertEquals(new Integer(500), error.status);
			assertEquals("dummyLog", error.link.toString());
			assertEquals("urn:cytoscape:ci:ci-wrap-test:v1:fail-with-ci-error:errors:1", error.type);
		}
	
	}
}
