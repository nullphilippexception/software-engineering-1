package server.exceptions;

// super class for my own exceptions
public class GenericExampleException extends RuntimeException {

	private final String errorName;

	public GenericExampleException(String errorName, String errorMessage) {
		super(errorMessage);
		this.errorName = errorName;
	}

	public String getErrorName() {
		return errorName;
	}
}
