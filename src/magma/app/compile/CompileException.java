package magma.app.compile;

import magma.app.ApplicationException;

public class CompileException extends ApplicationException {
    public CompileException(String message, String context) {
        super(message + ": " + context);
    }
}
