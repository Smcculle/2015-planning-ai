package edu.uno.ai.planning;

import java.nio.ByteBuffer;

/**
 * Project settings
 * 
 * @author Stephen G. Ware
 */
public class Settings {

	/** Major version number */
	public static final int MAJOR_VERSION = 1;
	
	/** Minor version number */
	public static final int MINOR_VERSION = 0;
	
	/** A UID number constructed from the major and minor version numbers */
	public static final long VERSION_UID = ByteBuffer.allocate(8).putInt(MAJOR_VERSION).putInt(MINOR_VERSION).getLong(0);
	
	/** The name of the supertype of all types */
	public static final String DEFAULT_TYPE = "object";
}
