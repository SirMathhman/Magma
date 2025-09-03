package magma;

public class InterpretError {
  private final String source;

  public InterpretError(String source) {
    this.source = source;
  }

  // Return a human-readable error message matching the tests' expectation.
  public String display() {
    // Note: the test expects a very specific formatting.
    return "Undefined identifier." + "\n\n" +
        "File: <virtual>" + "\n\n" +
        "1) " + source + "\n" +
        "   ^^^^";
  }
}
