package bimsl.bimserver.exception;

public class ProjectGetException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "An error occurred while retrieving the project(s) requested.";
	}
	
}
