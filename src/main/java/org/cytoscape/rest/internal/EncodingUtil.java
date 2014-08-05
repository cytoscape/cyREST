package org.cytoscape.rest.internal;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

final class EncodingUtil {

	private static final String ENCODING = "UTF-8";

	public static CharsetEncoder getEncoder() {
		final CharsetEncoder encoder;

		if (Charset.isSupported(ENCODING)) {
			// UTF-8 is supported by system
			encoder = Charset.forName(ENCODING).newEncoder();
		} else {
			// Use default.
			encoder = Charset.defaultCharset().newEncoder();
		}
		
		return encoder;
	}
}
