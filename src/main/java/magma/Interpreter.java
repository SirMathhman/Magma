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
    // Try folding with '+' then '-' using a shared helper to avoid duplication.
    if (trimmed.contains("+")) {
      var res = tryFold(source, '+');
      if (res.isPresent())
        return res.get();
    }
    if (trimmed.contains("-")) {
      var res = tryFold(source, '-');
      if (res.isPresent())
        return res.get();
    }
    return new Err<String, InterpretError>(new InterpretError(source));
  }

  // Attempt to parse and fold a left-associative sequence of integers joined by
  // op
  // Returns Optional.empty() if the first line doesn't contain an op sequence to
  // fold,
  // otherwise returns Ok or Err as appropriate.
  private static java.util.Optional<Result<String, InterpretError>> tryFold(String source, char op) {
    String firstLine = source.split("\\r?\\n|\\r")[0];
    java.util.List<String> parts = new java.util.ArrayList<>();
    java.util.List<Integer> starts = new java.util.ArrayList<>();
    int pos = 0;
    int len = firstLine.length();
    while (pos < len) {
      while (pos < len && Character.isWhitespace(firstLine.charAt(pos)))
        pos++;
      int sep = firstLine.indexOf(op, pos);
      int tokenStart = pos;
      int tokenEnd = (sep == -1) ? len : sep;
      int tokenEndTrim = tokenEnd;
      while (tokenEndTrim > tokenStart && Character.isWhitespace(firstLine.charAt(tokenEndTrim - 1)))
        tokenEndTrim--;
      String token = firstLine.substring(tokenStart, tokenEndTrim);
      if (!token.isEmpty()) {
        parts.add(token);
        starts.add(tokenStart);
      }
      pos = (sep == -1) ? len : sep + 1;
    }

    if (parts.size() < 2)
      return java.util.Optional.empty();

    try {
      // Validate all tokens and convert to BigInteger list
      java.util.List<java.math.BigInteger> nums = new java.util.ArrayList<>();
      for (int i = 0; i < parts.size(); i++) {
        String tok = parts.get(i);
        if (!tok.matches("[+-]?\\d+")) {
          String msg;
          if (op == '+') {
            msg = (i == 0) ? "Addition requires integer on the left-hand side."
                : "Addition requires integer on the right-hand side.";
          } else {
            msg = (i == 0) ? "Subtraction requires integer on the left-hand side."
                : "Subtraction requires integer on the right-hand side.";
          }
          int caret = starts.get(i);
          return java.util.Optional.of(new Err<String, InterpretError>(new InterpretError(msg, source, caret)));
        }
        nums.add(new java.math.BigInteger(tok));
      }

      if (op == '+') {
        java.math.BigInteger sum = java.math.BigInteger.ZERO;
        for (var n : nums)
          sum = sum.add(n);
        return java.util.Optional.of(new Ok<String, InterpretError>(sum.toString()));
      } else {
        java.math.BigInteger acc = nums.get(0);
        for (int i = 1; i < nums.size(); i++)
          acc = acc.subtract(nums.get(i));
        return java.util.Optional.of(new Ok<String, InterpretError>(acc.toString()));
      }
    } catch (Exception e) {
      return java.util.Optional.empty();
    }
  }
}
