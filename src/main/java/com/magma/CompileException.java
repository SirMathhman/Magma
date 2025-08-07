package com.magma;

/**
 * Exception thrown when there is a compilation error in Magma code.
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
}