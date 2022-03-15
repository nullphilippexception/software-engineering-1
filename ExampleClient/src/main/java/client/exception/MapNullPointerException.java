package client.exception;

// Exception that is thrown when a Map is null
public class MapNullPointerException extends Exception{
	
	String message;
	public MapNullPointerException(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return "MapNullPointerException [message=" + message + "]";
	}
	
	
}
