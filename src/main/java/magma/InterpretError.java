package magma;

public class InterpretError {
  private final String source;
  private final String message;
  private final int caretPos; // position within the first line to start carets, -1 = first token
  private final boolean caretOnEachLine; // when true, underline first token on every line (for duplicate-let)

  public InterpretError(String source) {
    this("Undefined identifier.", source, -1);
  }

  public InterpretError(String message, String source) {
    this(message, source, -1);
  }

  public InterpretError(String message, String source, int caretPos) {
    this(message, source, caretPos, false);
  }

  public InterpretError(String message, String source, int caretPos, boolean caretOnEachLine) {
    this.message = message;
    this.source = source;
    this.caretPos = caretPos;
    this.caretOnEachLine = caretOnEachLine;
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

      // Determine caret start and length for the first line.
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
        // first token on first line
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

      // Output spaces to caret position for the first line, then caret
      for (int i = 0; i < caretStartInLine; i++)
        sb.append(' ');
      for (int i = 0; i < caretLength; i++)
        sb.append('^');

      // token text to search for on subsequent lines when caretOnEachLine is set
      String firstTokenFromFirstLine = "";
      if (caretLength > 0 && caretStartInLine < first.length()) {
        int end = Math.min(first.length(), caretStartInLine + caretLength);
        firstTokenFromFirstLine = first.substring(caretStartInLine, end).strip();
      }

      // If there are additional lines, either underline the first token on each
      // additional line (if caretOnEachLine) or just append numbered lines.
      for (int li = 1; li < lines.length; li++) {
        if (caretOnEachLine) {
          // blank line before the next numbered line (matches tests)
          sb.append('\n');
          sb.append('\n');
          sb.append(li + 1).append(") ").append(lines[li]).append('\n');

          // compute caret for this additional line: try to find the same token as on the first line
          int localCaretStart = 0;
          int localCaretLength = 1;
          String thisLine = lines[li];
          if (!firstTokenFromFirstLine.isEmpty()) {
            int found = thisLine.indexOf(firstTokenFromFirstLine);
            if (found >= 0) {
              localCaretStart = found;
              localCaretLength = Math.max(1, firstTokenFromFirstLine.length());
            } else {
              // fallback to first token
              String strippedLine = thisLine.strip();
              if (!strippedLine.isEmpty()) {
                String firstToken = strippedLine.split("\\s+", 2)[0];
                localCaretLength = Math.max(1, firstToken.length());
                localCaretStart = thisLine.indexOf(firstToken);
                if (localCaretStart < 0)
                  localCaretStart = 0;
              }
            }
          } else {
            String strippedLine = thisLine.strip();
            if (!strippedLine.isEmpty()) {
              String firstToken = strippedLine.split("\\s+", 2)[0];
              localCaretLength = Math.max(1, firstToken.length());
              localCaretStart = thisLine.indexOf(firstToken);
              if (localCaretStart < 0)
                localCaretStart = 0;
            }
          }

          // prefix spaces (digits + ") ")
          int innerPrefixLen = String.valueOf(li + 1).length() + 2;
          for (int i = 0; i < innerPrefixLen; i++)
            sb.append(' ');
          for (int i = 0; i < localCaretStart; i++)
            sb.append(' ');
          for (int i = 0; i < localCaretLength; i++)
            sb.append('^');
        } else {
          sb.append('\n');
          sb.append(li + 1).append(") ").append(lines[li]);
        }
      }
    }

    return sb.toString();
  }
}
