package org.cytoscape.rest.internal.serializer;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * This serializer exists to provide serialization for Exceptions. Initially, this was handled by the Grizzly server and 
 * its Jackson libraries, but the switch from the OSGi JAX RS Connector changed both the exception types generated, and 
 * the way they were serialized. This class attempts to follow the original structure as closely as possible to support 
 * backward compatibility.
 * 
 * @author davidotasek
 *
 */
public class ExceptionSerializer extends JsonSerializer<Exception> {

	@Override
	public Class<Exception> handledType() {
		return Exception.class;
	}

	@Override
	public void serialize(Exception mapping, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.useDefaultPrettyPrinter();
		jgen.writeStartObject();
		
		if (mapping.getCause() == null) {
			jgen.writeNullField("cause");
		}
		else {
			provider.defaultSerializeField("cause", mapping.getCause(), jgen);
		}
		
		if (mapping.getStackTrace() != null)
		{
			provider.defaultSerializeField("stackTrace", mapping.getStackTrace(), jgen);
		}
		// This was originally provided for by Grizzly exceptions; it is now absent, and as such, the array is left 
		// empty.
		provider.defaultSerializeField("classContext", new Object[]{}, jgen);
		
		jgen.writeStringField("message", mapping.getMessage());
		jgen.writeStringField("localizedMessage", mapping.getLocalizedMessage());
		
		provider.defaultSerializeField("suppressed", mapping.getSuppressed(), jgen);
		
		jgen.writeEndObject();
	}
	
	public static void main(String[] args) throws JsonProcessingException
	{
		ObjectMapper mapper = new ObjectMapper();
		 
		SimpleModule module = new SimpleModule();
		module.addSerializer(Exception.class, new ExceptionSerializer());
		mapper.registerModule(module);
		
		String serialized = mapper.writeValueAsString(new Exception("flern", new Exception("blern")));
		System.out.println(serialized);
	}
}
