package edu.uno.ai.planning.pop;

public class CloneTypeMismatchException extends Exception {

	private static final long serialVersionUID = -1071861803071902656L;

	public CloneTypeMismatchException() {
		this("A clone did not match its parent type.");
	}

	public CloneTypeMismatchException(String message) {
		super(message);
	}

	public CloneTypeMismatchException(Throwable cause) {
		super(cause);
	}

	public CloneTypeMismatchException(String message, Throwable cause) {
		super(message, cause);
	}

	public CloneTypeMismatchException(
			String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
