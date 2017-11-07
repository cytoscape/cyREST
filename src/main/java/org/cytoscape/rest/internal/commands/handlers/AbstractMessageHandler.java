package org.cytoscape.rest.internal.commands.handlers;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.rest.internal.commands.handlers.Message.Type;

public abstract class AbstractMessageHandler implements MessageHandler {
	final List<Message> messages;
	
	public AbstractMessageHandler() {
		messages = new ArrayList<Message>();
	}
	@Override
	public void appendCommand(String s) {
		messages.add(new Message(Type.COMMAND, s));
		
	}
	@Override
	public void appendError(String s) {
		messages.add(new Message(Type.ERROR, s));
		
	}
	@Override
	public void appendWarning(String s) {
		messages.add(new Message(Type.WARNING,s));
		
	}
	@Override
	public void appendResult(Object s) {
		messages.add(new Message(Type.RESULT, s.toString()));
		
	}
	@Override
	public void appendMessage(String s) {
		messages.add(new Message(Type.MESSAGE, s.toString()));
	}
	@Override
	public List<Message> getMessages() {
		return messages;
	}
}
