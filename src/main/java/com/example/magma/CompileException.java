package com.example.magma;

/**
 * Simple checked exception used to indicate compile-time errors in the test
 * compiler.
 */
public class CompileException extends RuntimeException {
  public CompileException(String message) {
    super(message);
  }

  public CompileException(String message, Throwable cause) {
    super(message, cause);
  }
}
