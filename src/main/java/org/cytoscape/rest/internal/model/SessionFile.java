package org.cytoscape.rest.internal.model;

/**
 * 
 * Session file data for changes to Session file location or save status.
 * 
 * @author David Otasek (dotasek.dev@gmail.com)
 *
 */
public class SessionFile 
{
	private String file;
	
	public SessionFile(String file)
	{
		this.setFile(file);
	}
	
	/**
	 * 
	 * @return the file path.
	 */
	public String getFile()
	{
		return file;
	}
	
	/**
	 * 
	 * @param file
	 *  	the file path to set
	 */
	public void setFile(String file)
	{
		this.file = file;
	}
}
