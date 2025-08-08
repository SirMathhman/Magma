package magma;

/**
 * A custom checked exception for compilation errors in the Magma compiler.
 */
public class CompileException extends Exception {
    
    /**
     * Constructs a new CompileException with the specified detail message.
     *
     * @param message the detail message
     */
    public CompileException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new CompileException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public CompileException(String message, Throwable cause) {
        super(message, cause);
    }
}