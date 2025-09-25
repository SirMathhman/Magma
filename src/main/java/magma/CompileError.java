package magma;

/**
 * Simple compile error wrapper used as the error type from Compiler.compile.
 */
public final class CompileError {
    private final String message;

    public CompileError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
