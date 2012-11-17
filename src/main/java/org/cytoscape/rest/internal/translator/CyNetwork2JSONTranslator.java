package org.cytoscape.rest.internal.translator;

import java.io.IOException;
import java.lang.reflect.Type;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.SavePolicy;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.rest.Translator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

public class CyNetwork2JSONTranslator implements Translator<String, CyNetwork> {

	
	//private final Gson gson;
	private final ObjectMapper jackson;

	public CyNetwork2JSONTranslator() {
		
		jackson = new ObjectMapper();
		SimpleModule testModule = new SimpleModule("MyModule", new Version(1, 0, 0, null));
		testModule.addSerializer(CyNetwork.class, new CyNetworkSerializer()); // assuming serializer declares correct class to bind to
		jackson.registerModule(testModule);
		
		/*
		GsonBuilder fu = new GsonBuilder();
		fu.disableInnerClassSerialization();
		fu.registerTypeAdapter(CyNetwork.class, new CyNetworkSerializer());
		this.gson =  fu.create();
		
		gson.toJson(fact.createNetwork());*/
	}
	
	
	/**
	 * Convert CyNetwork Object to JSON
	 */
	public String translate(final CyNetwork network) {
		

		try {
			return jackson.writeValueAsString(network);
		} catch (Exception e) {
			return "";
		}
		   /*
		//final String jsonNetwork = gson.toJson(network);
		ObjectMapper objmap = new ObjectMapper();
		final String jsonNetwork2 = "{\"network\":"+network.NAME+"}";
		
		return jsonNetwork2;*/
	}
	
	private class CyNetworkSerializer extends JsonSerializer<CyNetwork> {

		@Override
		public void serialize(CyNetwork network, JsonGenerator jgen,
		        SerializerProvider provider) throws IOException,
		        JsonProcessingException {
		    jgen.writeStartObject();
		    jgen.writeNumberField("SUID", network.getSUID());
		    jgen.writeEndObject();
		}
		
		public Class<CyNetwork> handledType() {
			return CyNetwork.class;
		}
	}
	/*
	private class CyNetworkSerializer implements JsonSerializer<CyNetwork>, JsonDeserializer<CyNetwork> {

		@Override
		public JsonElement serialize(CyNetwork src, Type typeOfSrc,
				JsonSerializationContext context) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CyNetwork deserialize(JsonElement src, Type type,
				JsonDeserializationContext context) throws JsonParseException {
			return null;
		}
		 
		}*/
		
}
