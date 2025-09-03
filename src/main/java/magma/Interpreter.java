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
    // Try folding a sequence containing +, -, or * (handles mixed ops
    // left-to-right)
    if (trimmed.contains("+") || trimmed.contains("-") || trimmed.contains("*")) {
      var res = tryEvaluateAddSub(source);
      if (res.isPresent())
        return res.get();
    }
    return new Err<String, InterpretError>(new InterpretError(source));
  }

  private static java.util.Optional<Result<String, InterpretError>> tryEvaluateAddSub(String source) {
    String firstLine = source.split("\\r?\\n|\\r")[0];
    java.util.List<String> parts = new java.util.ArrayList<>();
    java.util.List<Integer> starts = new java.util.ArrayList<>();
    java.util.List<Character> ops = new java.util.ArrayList<>();
    int pos = 0;
    int len = firstLine.length();
    while (pos < len) {
      while (pos < len && Character.isWhitespace(firstLine.charAt(pos)))
        pos++;
      if (pos >= len) {
        // nothing left to parse; avoid using 'break' which is banned by Checkstyle
        pos = len;
      }
      int pPlus = firstLine.indexOf('+', pos);
      int pMinus = firstLine.indexOf('-', pos);
      int pMul = firstLine.indexOf('*', pos);
      int sep;
      char sepChar = 0;
      if (pPlus == -1 && pMinus == -1 && pMul == -1) {
        sep = -1;
      } else if (pPlus == -1) {
        if (pMinus == -1) {
          sep = pMul;
          sepChar = '*';
        } else if (pMul == -1) {
          sep = pMinus;
          sepChar = '-';
        } else if (pMinus < pMul) {
          sep = pMinus;
          sepChar = '-';
        } else {
          sep = pMul;
          sepChar = '*';
        }
      } else if (pMinus == -1) {
        if (pMul == -1) {
          sep = pPlus;
          sepChar = '+';
        } else if (pPlus < pMul) {
          sep = pPlus;
          sepChar = '+';
        } else {
          sep = pMul;
          sepChar = '*';
        }
      } else {
        if (pPlus < pMinus) {
          if (pMul != -1 && pMul < pPlus) {
            sep = pMul;
            sepChar = '*';
          } else {
            sep = pPlus;
            sepChar = '+';
          }
        } else {
          if (pMul != -1 && pMul < pMinus) {
            sep = pMul;
            sepChar = '*';
          } else {
            sep = pMinus;
            sepChar = '-';
          }
        }
      }

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
      if (sep != -1) {
        ops.add(sepChar);
        pos = sep + 1;
      } else {
        pos = len;
      }
    }

    if (parts.size() < 2)
      return java.util.Optional.empty();

    try {
      java.util.List<java.math.BigInteger> nums = new java.util.ArrayList<>();
      for (int i = 0; i < parts.size(); i++) {
        String tok = parts.get(i);
        if (!tok.matches("[+-]?\\d+")) {
          char contextOp = (i == 0) ? ops.get(0) : ops.get(i - 1);
          String msg;
          if (contextOp == '+') {
            msg = (i == 0) ? "Addition requires integer on the left-hand side."
                : "Addition requires integer on the right-hand side.";
          } else if (contextOp == '-') {
            msg = (i == 0) ? "Subtraction requires integer on the left-hand side."
                : "Subtraction requires integer on the right-hand side.";
          } else {
            msg = (i == 0) ? "Multiplication requires integer on the left-hand side."
                : "Multiplication requires integer on the right-hand side.";
          }
          int caret = starts.get(i);
          return java.util.Optional.of(new Err<String, InterpretError>(new InterpretError(msg, source, caret)));
        }
        nums.add(new java.math.BigInteger(tok));
      }

      java.math.BigInteger acc = nums.get(0);
      for (int i = 1; i < nums.size(); i++) {
        char op = ops.get(i - 1);
        if (op == '+')
          acc = acc.add(nums.get(i));
        else if (op == '-')
          acc = acc.subtract(nums.get(i));
        else
          acc = acc.multiply(nums.get(i));
      }
      return java.util.Optional.of(new Ok<String, InterpretError>(acc.toString()));
    } catch (Exception e) {
      return java.util.Optional.empty();
    }
  }
}
