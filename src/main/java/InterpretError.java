public class InterpretError {
  private final String message;

  public InterpretError(String message) {
    this.message = message;
  }

  public String message() {
    return message;
  }

  @Override
  public String toString() {
    return "InterpretError{" + message + '}';
  }
}
