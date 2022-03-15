package server.exceptions;

//exception that is thrown by classes that implement IMapRule and who check halfmap for validity
public class InvalidMapException extends GenericExampleException {
	private final String errorName;
	public InvalidMapException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
		this.errorName = errorName;
	}
}
