package bimsl.bimserver.exception;

public class ProjectCheckException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The IFC file name. */
	private final String name;

	public ProjectCheckException(String name) {
		this.name = name;
	}

	@Override
	public String getMessage() {
		return "An error occured checking " + name + ".";
	}
	
}
