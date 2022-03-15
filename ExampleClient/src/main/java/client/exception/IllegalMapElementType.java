package client.exception;

// Exception that is thrown when a non existent MapElementType is demanded
public class IllegalMapElementType extends Exception {

	String message;
	public IllegalMapElementType(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return "IllegalMapElementType [message=" + message + "]";
	}
}
