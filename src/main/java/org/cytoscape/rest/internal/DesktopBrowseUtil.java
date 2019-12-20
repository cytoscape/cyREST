package org.cytoscape.rest.internal;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class DesktopBrowseUtil {
	public static void browse(String url) throws IOException, URISyntaxException {
		URI uri = null;
		uri = new URI(url);
		Desktop.getDesktop().browse(uri);
	}
}
