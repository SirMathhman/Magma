package magma;

public class CompileException extends ApplicationException {
    public CompileException(Throwable cause) {
        super(cause);
    }

    public CompileException(String message) {
        super(message);
    }

    public CompileException(String message, CompileException cause) {
        super(message, cause);
    }
}
