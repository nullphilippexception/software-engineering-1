package server.exceptions;

// exception that is thrown by TurnValidator if client sends an action even though it is not its turn
public class WrongTurnException extends GenericExampleException {
	private final String errorName;
	public WrongTurnException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
		this.errorName = errorName;
	}
}
