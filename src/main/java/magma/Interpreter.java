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
    // Accept a parenthesized integer like `(0)` or `( -1 )` or evaluate a wrapped
    // expression like `(1 + 3)`
    if (trimmed.startsWith("(") && trimmed.endsWith(")")) {
      String innerRaw = trimmed.substring(1, trimmed.length() - 1);
      String inside = innerRaw.strip();
      boolean isNum = false;
      if (!inside.isEmpty()) {
        int si = 0;
        if ((inside.charAt(0) == '+' || inside.charAt(0) == '-') && inside.length() > 1)
          si = 1;
        isNum = si < inside.length();
        int kk = si;
        while (kk < inside.length() && isNum) {
          if (!Character.isDigit(inside.charAt(kk)))
            isNum = false;
          kk = kk + 1;
        }
      }
      if (isNum) {
        return new Ok<String, InterpretError>(inside);
      }

      // If not a bare number, ensure the outer parentheses wrap the full expression
      int depth = 0;
      int lastMatch = -1;
      int L = trimmed.length();
      int p = 0;
      boolean found = false;
      while (p < L && !found) {
        char cc = trimmed.charAt(p);
        if (cc == '(')
          depth = depth + 1;
        else if (cc == ')')
          depth = depth - 1;
        if (depth == 0) {
          lastMatch = p;
          found = true;
        } else {
          p = p + 1;
        }
      }
      if (lastMatch == L - 1) {
        var maybe = tryEvaluateAddSub(innerRaw);
        if (maybe.isPresent())
          return maybe.get();
      }
    }
    // Try folding a sequence containing +, -, *, or parentheses (handles mixed ops
    // with parentheses and precedence)
    if (trimmed.contains("+") || trimmed.contains("-") || trimmed.contains("*") || trimmed.contains("(")
        || trimmed.contains(")")) {
      var res = tryEvaluateAddSub(source);
      if (res.isPresent())
        return res.get();
    }
    return new Err<String, InterpretError>(new InterpretError(source));
  }

  private static java.util.Optional<Result<String, InterpretError>> tryEvaluateAddSub(String source) {
    String firstLine = source.split("\\r?\\n|\\r")[0];
    // Evaluate innermost parenthesized expressions first, replacing them with their
    // numeric results.
    while (firstLine.indexOf('(') != -1) {
      int open = firstLine.lastIndexOf('(');
      int close = firstLine.indexOf(')', open + 1);
      if (close == -1) {
        return java.util.Optional.empty();
      }
      String inner = firstLine.substring(open + 1, close);
      var innerOpt = tryEvaluateAddSub(inner);
      if (!innerOpt.isPresent()) {
        return java.util.Optional.empty();
      }
      Result<String, InterpretError> innerRes = innerOpt.get();
      // If inner evaluation succeeded, inline its numeric value; otherwise treat as
      // failure.
      if (innerRes instanceof Ok ok) {
        String val = String.valueOf(ok.value());
        firstLine = firstLine.substring(0, open) + val + firstLine.substring(close + 1);
      } else {
        return java.util.Optional.empty();
      }
    }
    int len = firstLine.length();

    java.util.List<String> parts = new java.util.ArrayList<>();
    java.util.List<Integer> starts = new java.util.ArrayList<>();
    java.util.List<Character> ops = new java.util.ArrayList<>();

    int pos = 0;
    while (pos < len) {
      // skip whitespace
      while (pos < len && Character.isWhitespace(firstLine.charAt(pos)))
        pos = pos + 1;
      if (pos >= len) {
        pos = len;
      } else {
        int start = pos;
        // find next operator +,-,*
        int nextPlus = firstLine.indexOf('+', pos);
        int nextMinus = firstLine.indexOf('-', pos);
        int nextMul = firstLine.indexOf('*', pos);
        int sep = -1;
        if (nextPlus >= 0)
          sep = nextPlus;
        if (nextMinus >= 0 && (sep == -1 || nextMinus < sep)) {
          sep = nextMinus;
        }
        if (nextMul >= 0 && (sep == -1 || nextMul < sep)) {
          sep = nextMul;
        }
        int tokenEnd = (sep == -1) ? len : sep;
        int tokenEndTrim = tokenEnd;
        while (tokenEndTrim > start && Character.isWhitespace(firstLine.charAt(tokenEndTrim - 1)))
          tokenEndTrim = tokenEndTrim - 1;
        String token = firstLine.substring(start, tokenEndTrim);
        if (!token.isEmpty()) {
          parts.add(token);
          starts.add(start);
        }
        if (sep != -1) {
          char c = firstLine.charAt(sep);
          ops.add(c);
          pos = sep + 1;
        } else {
          pos = len;
        }
      }
    }

    if (parts.size() < 2)
      return java.util.Optional.empty();

    try {
      java.util.List<java.math.BigInteger> nums = new java.util.ArrayList<>();
      for (int idx = 0; idx < parts.size(); idx++) {
        String tok = parts.get(idx).strip();
        boolean isNum = false;
        if (!tok.isEmpty()) {
          int si = 0;
          if ((tok.charAt(0) == '+' || tok.charAt(0) == '-') && tok.length() > 1)
            si = 1;
          isNum = si < tok.length();
          for (int k = si; isNum && k < tok.length(); k++) {
            if (!Character.isDigit(tok.charAt(k)))
              isNum = false;
          }
        }
        if (!isNum) {
          char contextOp = (idx == 0) ? ops.get(0) : ops.get(idx - 1);
          String msg;
          if (contextOp == '+')
            msg = (idx == 0) ? "Addition requires integer on the left-hand side."
                : "Addition requires integer on the right-hand side.";
          else if (contextOp == '-')
            msg = (idx == 0) ? "Subtraction requires integer on the left-hand side."
                : "Subtraction requires integer on the right-hand side.";
          else
            msg = (idx == 0) ? "Multiplication requires integer on the left-hand side."
                : "Multiplication requires integer on the right-hand side.";
          return java.util.Optional
              .of(new Err<String, InterpretError>(new InterpretError(msg, source, starts.get(idx))));
        }
        nums.add(new java.math.BigInteger(tok));
      }

      // fold multiplication
      java.util.List<java.math.BigInteger> nums2 = new java.util.ArrayList<>();
      java.util.List<Character> ops2 = new java.util.ArrayList<>();
      nums2.add(nums.get(0));
      for (int k = 0; k < ops.size(); k++) {
        char op = ops.get(k);
        java.math.BigInteger next = nums.get(k + 1);
        if (op == '*') {
          int last = nums2.size() - 1;
          java.math.BigInteger folded = nums2.get(last).multiply(next);
          nums2.set(last, folded);
        } else {
          ops2.add(op);
          nums2.add(next);
        }
      }

      java.math.BigInteger acc = nums2.get(0);
      for (int m = 1; m < nums2.size(); m++) {
        char op = ops2.get(m - 1);
        if (op == '+')
          acc = acc.add(nums2.get(m));
        else
          acc = acc.subtract(nums2.get(m));
      }
      return java.util.Optional.of(new Ok<String, InterpretError>(acc.toString()));
    } catch (Exception e) {
      return java.util.Optional.empty();
    }
  }

  // helper methods removed as logic is inlined to satisfy Checkstyle/CPD
  // constraints
}
