package magma;

public class CompileException extends ApplicationException {
    public CompileException(Throwable cause) {
        super(cause);
    }

    public CompileException(String message) {
        super(message);
    }
}
