package bimsl.bimserver.exception;

public class BIMServerConnectionException extends Exception {

	/** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    @Override
    public String getMessage() {
        return "Impossible to connect to BIMserver.";
    }
}
