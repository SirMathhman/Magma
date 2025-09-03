package magma;

public class InterpretError {
  private final String source;
  private final String message;
  private final int caretPos; // position within the first line to start carets, -1 = first token

  public InterpretError(String source) {
    this("Undefined identifier.", source, -1);
  }

  public InterpretError(String message, String source) {
    this(message, source, -1);
  }

  public InterpretError(String message, String source, int caretPos) {
    this.message = message;
    this.source = source;
    this.caretPos = caretPos;
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

      // Determine caret start and length. If caretPos was provided, use it;
      // otherwise underline the first token.
      int caretStartInLine = -1;
      int caretLength = 0;
      if (caretPos >= 0) {
        caretStartInLine = Math.max(0, Math.min(caretPos, first.length()));
        // compute caret length as token length at caretStartInLine
        int j = caretStartInLine;
        while (j < first.length() && !Character.isWhitespace(first.charAt(j)))
          j++;
        caretLength = Math.max(1, j - caretStartInLine);
      } else {
        // first token
        String stripped = first.strip();
        if (!stripped.isEmpty()) {
          String firstToken = stripped.split("\\s+", 2)[0];
          caretLength = Math.max(1, firstToken.length());
          // caretStartInLine is index of first token in original first line
          caretStartInLine = first.indexOf(firstToken);
          if (caretStartInLine < 0)
            caretStartInLine = 0;
        } else {
          caretStartInLine = 0;
          caretLength = 1;
        }
      }

      // Output spaces to caret position
      for (int i = 0; i < caretStartInLine; i++)
        sb.append(' ');
      for (int i = 0; i < caretLength; i++)
        sb.append('^');

      // If there are additional lines, append them each numbered on their own line
      for (int i = 1; i < lines.length; i++) {
        sb.append('\n');
        sb.append(i + 1).append(") ").append(lines[i]);
      }
    }

    return sb.toString();
  }
}
