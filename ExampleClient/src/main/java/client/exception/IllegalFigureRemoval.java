package client.exception;

// Exception that is thrown when a figure is illegally removed from a mapElement
public class IllegalFigureRemoval extends Exception {
	String message;
	public IllegalFigureRemoval(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return "IllegalFigureRemoval [message=" + message + "]";
	}
}
