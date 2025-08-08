package magma;

/**
 * Exception thrown when a compilation error occurs.
 * Used to indicate problems during the compilation process,
 * such as type incompatibility between variable declarations and their values.
 */
public class CompileException extends RuntimeException {

    /**
     * Constructs a new CompileException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public CompileException(String message) {
        super(message);
    }

    /**
     * Constructs a new CompileException with the specified detail message and cause.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause the cause of the exception
     */
    public CompileException(String message, Throwable cause) {
        super(message, cause);
    }
}