package org.cytoscape.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Convert objects from stream and write object to stream 
 *
 * @param <T>
 */
public interface DataMapper <T> {
	
	T read(InputStream is) throws IOException;
	
	T read(String textData);
	
	void write(OutputStream os, T data) throws IOException;
	
	String writeAsString(T data);

}
