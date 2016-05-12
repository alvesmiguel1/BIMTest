package bimsl.bimserver.exception;

public class PropertiesLoadingException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "Configuration properties could not be read.";
	}

}
