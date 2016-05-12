package bimsl.bimserver.exception;

public class ProjectAddException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The Project name. */
	private final String name;

	public ProjectAddException(String name) {
		this.name = name;
	}

	@Override
	public String getMessage() {
		return "An error occurred while adding a new project named " + name + ".";
	}

}
