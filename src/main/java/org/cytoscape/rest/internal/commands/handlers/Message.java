package org.cytoscape.rest.internal.commands.handlers;

public final class Message {
	public enum Type {
		COMMAND,
		ERROR,	
		WARNING,
		RESULT,
		MESSAGE
	}
	
	public final Type type;
	
	public final String message;
	
	public Message(Type type, String message) {
		this.type = type;
		this.message = message;
	}
}
