package bimsl.bimserver.exception;

public class ProjectAlreadyExistsException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The Project name. */
	private final String name;

	public ProjectAlreadyExistsException(String name) {
		this.name = name;
	}

	@Override
	public String getMessage() {
		return "There is already a project named " + name + ".";
	}

}