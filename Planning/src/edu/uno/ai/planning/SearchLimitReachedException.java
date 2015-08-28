package edu.uno.ai.planning;

/**
 * This exception is thrown any time a {@link Search} attempts to perform
 * more search than it is authorized to perform.
 * 
 * @author Stephen G. Ware
 */
public class SearchLimitReachedException extends RuntimeException {

	/** Version ID */
	private static final long serialVersionUID = Settings.VERSION_UID;

	/**
	 * Constructs a new search limit reached exception.
	 */
	public SearchLimitReachedException() {
		super("Search limit reached");
	}
}
