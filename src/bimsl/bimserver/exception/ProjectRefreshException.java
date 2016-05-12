package bimsl.bimserver.exception;

public class ProjectRefreshException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "An error occurred while refreshing the current working project.";
	}
	
}