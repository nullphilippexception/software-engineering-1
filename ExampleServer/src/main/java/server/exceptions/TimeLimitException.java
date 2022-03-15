package server.exceptions;

// exception that is thrown by TimeValidator if client takes too much time ( > 3 sec) for action
public class TimeLimitException extends GenericExampleException {
	private final String errorName;
	public TimeLimitException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
		this.errorName = errorName;
	}
}

