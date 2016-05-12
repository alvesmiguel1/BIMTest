package bimsl.bimserver.exception;

public class ProjectRemoveException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "Impossible to remove the current working project.";
	}

}