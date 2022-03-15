package server.exceptions;

// exception that is thrown by MaxMoveValidator if the max number of moves (200) would be exceeded
public class MoveLimitException extends GenericExampleException {
	private final String errorName;
	public MoveLimitException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
		this.errorName = errorName;
	}
}
