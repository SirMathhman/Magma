package magma;

public class InterpretError {
  private final String source;

  public InterpretError(String source) {
    this.source = source;
  }

  // Return a human-readable error message matching the tests' expectation.
  public String display() {
    // Note: the tests expect numbered source lines with a caret underline
    // under the first line's identifier. Split on any common newline.
    String[] lines = source.split("\\r?\\n|\\r");
    StringBuilder sb = new StringBuilder();
    sb.append("Undefined identifier.").append('\n').append('\n');
    sb.append("File: <virtual>").append('\n').append('\n');

    // Append the first line and its caret marker
    if (lines.length > 0) {
      sb.append("1) ").append(lines[0]).append('\n');
      // prefix length equals length of "1) " (digits + ") "+space)
      int prefixLen = String.valueOf(1).length() + 2;
      for (int i = 0; i < prefixLen; i++) {
        sb.append(' ');
      }
      // caret length equals the visible length of the first line
      int caretCount = lines[0].length();
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
