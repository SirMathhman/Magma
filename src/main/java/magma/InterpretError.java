package magma;

public class InterpretError {
  private final String source;
  private final String message;

  public InterpretError(String source) {
    this("Undefined identifier.", source);
  }

  public InterpretError(String message, String source) {
    this.message = message;
    this.source = source;
  }

  // Return a human-readable error message matching the tests' expectation.
  public String display() {
    // Split on any common newline.
    String[] lines = source.split("\\r?\\n|\\r");
    StringBuilder sb = new StringBuilder();
    sb.append(message).append('\n').append('\n');
    sb.append("File: <virtual>").append('\n').append('\n');

    if (lines.length > 0) {
      String first = lines[0];
      sb.append("1) ").append(first).append('\n');

      // prefix length equals length of "1) " (digits + ") "+space)
      int prefixLen = String.valueOf(1).length() + 2;
      for (int i = 0; i < prefixLen; i++) {
        sb.append(' ');
      }

      // Underline the first token (first word) on the first line, not the whole line.
      String firstToken = first.strip().split("\\s+", 2)[0];
      int caretCount = Math.max(firstToken.length(), 1);
      for (int i = 0; i < caretCount; i++) {
        sb.append('^');
      }

      // If there are additional lines, append them each numbered on their own line
      for (int i = 1; i < lines.length; i++) {
        sb.append('\n');
        sb.append(i + 1).append(") ").append(lines[i]);
      }
    }

    return sb.toString();
  }
}
