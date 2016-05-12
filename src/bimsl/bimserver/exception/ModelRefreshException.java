package bimsl.bimserver.exception;

public class ModelRefreshException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "An error occurred while refreshing the object model of the current working project.";
	}
	
}