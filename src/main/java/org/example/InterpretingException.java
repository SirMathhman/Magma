package org.example;

/**
 * Thrown when interpretation of an input fails due to invalid input.
 */
public class InterpretingException extends RuntimeException {
  public InterpretingException(String message) {
    super(message);
  }

  public InterpretingException(String message, Throwable cause) {
    super(message, cause);
  }
}
