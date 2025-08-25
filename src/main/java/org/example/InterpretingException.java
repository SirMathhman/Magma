package org.example;

/**
 * Thrown when interpretation of an input fails due to invalid input.
 */
public class InterpretingException extends RuntimeException {
  public InterpretingException(String message, String context) {
    super(message + ": " + context);
  }

  public InterpretingException(String message, Throwable cause, String context) {
    super(message + ": " + context, cause);
  }
}
