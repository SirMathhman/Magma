package magma;

public final class InterpretError {
  private final String message;

  public InterpretError(String message) {
    this.message = message;
  }

  public String message() {
    return message;
  }

  @Override
  public String toString() {
    return message;
  }

  public String display() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'display'");
  }
}
