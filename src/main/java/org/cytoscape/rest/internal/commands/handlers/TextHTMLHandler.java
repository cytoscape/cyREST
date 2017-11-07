/* vim: set ts=2: */
/**
 * Copyright (c) 2010 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package org.cytoscape.rest.internal.commands.handlers;

import java.util.ArrayList;
import java.util.List;

/**
 * This is just a lame HTML handler to set things up. There is a lot more we
 * could do to style the messages and make them clear, but I don't really
 * anticipate anyone will use this from a web browser except for debugging.
 */
public class TextHTMLHandler extends AbstractMessageHandler {
	

	public String getPStyle(Message.Type type) {
		switch (type) {
		case COMMAND: return "color:blue;margin-top:0px;margin-bottom:5px;";
		case ERROR: return "color:red;margin-top:0px;margin-bottom:5px;";
		case WARNING: return "color:yellow;margin-top:0px;margin-bottom:5px;";
		case RESULT: return "color:green;margin-left:10px;margin-top:0px;margin-bottom:5px;";
		case MESSAGE: return "color:black;margin-left:10px;margin-top:0px;margin-bottom:5px;";
		default: throw new IllegalArgumentException();
		}

	}
	
	public String getMessageString() {
		String str = "";
		for (Message s : messages) {
			str += "<p style=\""+ getPStyle(s.type)+"\">" +s.message + "</p>\n";
		}
		return str;
	}
}
