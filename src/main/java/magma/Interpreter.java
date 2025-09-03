package magma;

public class Interpreter {
  public static Result<String, InterpretError> interpret(String source, String input) {
    // Trim whitespace and handle a single integer literal as a value
    String trimmed = source.strip();
    // Accept plain integers only (optional leading + or - and digits)
    if (trimmed.matches("[+-]?\\d+")) {
      return new Ok<String, InterpretError>(trimmed);
    }
    // Accept boolean literals
    if (trimmed.equals("true") || trimmed.equals("false")) {
      return new Ok<String, InterpretError>(trimmed);
    }
    // Support chained addition: "<int> + <int> + ..."
    if (trimmed.contains("+")) {
      String firstLine = source.split("\\r?\\n|\\r")[0];
      java.util.List<String> parts = new java.util.ArrayList<>();
      java.util.List<Integer> starts = new java.util.ArrayList<>();
      int pos = 0;
      int len = firstLine.length();
      while (pos < len) {
        // skip leading whitespace
        while (pos < len && Character.isWhitespace(firstLine.charAt(pos)))
          pos++;
        int plus = firstLine.indexOf('+', pos);
        int tokenStart = pos;
        int tokenEnd = (plus == -1) ? len : plus;
        // trim trailing whitespace from token
        int tokenEndTrim = tokenEnd;
        while (tokenEndTrim > tokenStart && Character.isWhitespace(firstLine.charAt(tokenEndTrim - 1)))
          tokenEndTrim--;
        String token = firstLine.substring(tokenStart, tokenEndTrim);
        if (!token.isEmpty()) {
          parts.add(token);
          starts.add(tokenStart);
        }
        pos = (plus == -1) ? len : plus + 1;
      }

      if (parts.size() >= 2) {
        // Validate tokens and compute sum
        try {
          java.math.BigInteger sum = java.math.BigInteger.ZERO;
          for (int i = 0; i < parts.size(); i++) {
            String tok = parts.get(i);
            if (!tok.matches("[+-]?\\d+")) {
              // non-integer token -> return appropriate error
              String msg = (i == 0) ? "Addition requires integer on the left-hand side."
                  : "Addition requires integer on the right-hand side.";
              int caret = starts.get(i);
              return new Err<String, InterpretError>(new InterpretError(msg, source, caret));
            }
            sum = sum.add(new java.math.BigInteger(tok));
          }
          return new Ok<String, InterpretError>(sum.toString());
        } catch (Exception e) {
          // fall through
        }
      }
    }
    return new Err<String, InterpretError>(new InterpretError(source));
  }
}
