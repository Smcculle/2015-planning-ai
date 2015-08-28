package edu.uno.ai.planning.io;

import edu.uno.ai.planning.Settings;

/**
 * A format exception is thrown when the input to a
 * {@link edu.uno.ai.planning.io.Parser} is improperly formatted.
 * 
 * @author Stephen G. Ware
 */
public class FormatException extends RuntimeException {

	/** Version ID */
	private static final long serialVersionUID = Settings.VERSION_UID;
	
	/**
	 * Constructs a new format exception with the given message.
	 * 
	 * @param message the message
	 */
	FormatException(String message) {
		super(message);
	}
}
