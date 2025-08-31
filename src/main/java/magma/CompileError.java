package magma;

/**
 * Represents an error that occurs during compilation.
 */
public class CompileError {
  private final String message;
  private final String context;

  public CompileError(String message, String context) {
    this.message = message;
    this.context = context;
  }

  public String getMessage() {
    return message;
  }

  public String getContext() {
    return context;
  }

  @Override
  public String toString() {
    return message + ": " + context;
  }
}
