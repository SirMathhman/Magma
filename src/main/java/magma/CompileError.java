package magma;

/**
 * Represents an error that occurs during compilation.
 */
public class CompileError {
  private final String message;

  public CompileError(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return "CompileError: " + message;
  }
}
