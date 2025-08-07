package com.magma;

/**
 * Exception thrown when there is a compilation error in Magma code.
 */
public class CompileException extends RuntimeException {
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