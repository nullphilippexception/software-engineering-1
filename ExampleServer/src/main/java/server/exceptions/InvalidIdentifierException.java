package server.exceptions;

// exception that is thrown by IDValidator if either the game id or the player id are not correct
public class InvalidIdentifierException extends GenericExampleException {
	private final String errorName;
	public InvalidIdentifierException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
		this.errorName = errorName;
	}
}

