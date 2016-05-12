package bimsl.bimserver.exception;

public class ProjectNotFoundException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "The project requested does not exist.";
	}
}
