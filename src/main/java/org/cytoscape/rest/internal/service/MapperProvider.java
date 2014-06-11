package org.cytoscape.rest.internal.service;

import javax.ws.rs.ext.ContextResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class MapperProvider implements ContextResolver<ObjectMapper> {

	@Override
	public ObjectMapper getContext(Class<?> type) {
		return null;
	}

	private ObjectMapper createDefaultMapper() {

		ObjectMapper result = new ObjectMapper();
		result.configure(SerializationFeature.INDENT_OUTPUT, true);

		return result;
	}

	private static ObjectMapper createCombinedObjectMapper() {

//		AnnotationIntrospector jaxbIntrospector = new JaxbAnnotationIntrospector();
//        AnnotationIntrospector jacksonIntrospector = new JacksonAnnotationIntrospector();
//
//		Object combinedIntrospector = new AnnotationIntrospector.Pair(jacksonIntrospector, jaxbIntrospector);
//		ObjectMapper result = new ObjectMapper();
//		result.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, true);
//		result.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
//		result.setDeserializationConfig(result.getDeserializationConfig().withAnnotationIntrospector(
//				combinedIntrospector));
//		result.setSerializationConfig(result.getSerializationConfig().withAnnotationIntrospector(combinedIntrospector));

		return null;
	}
}