package magma;

class InterpreterHelpers {
  static int findClosingParenAfterOpen(String program, int openPos) {
    if (openPos < 0 || openPos >= program.length() || program.charAt(openPos) != '(') {
      return -1;
    }
    int depth = 1;
    int i = openPos + 1;
    while (i < program.length()) {
      char ch = program.charAt(i);
      if (ch == '(') {
        depth++;
      } else if (ch == ')') {
        depth--;
        if (depth == 0) {
          return i + 1; // position after ')'
        }
      }
      i++;
    }
    return -1;
  }

  static int skipWhitespace(String program, int i) {
    while (i < program.length() && Character.isWhitespace(program.charAt(i))) {
      i++;
    }
    return i;
  }

  static java.util.Optional<String> tryExtractFnLiteralAt(String program, int posAfterArrow) {
    int litStart = skipWhitespace(program, posAfterArrow);
    int litEnd = program.indexOf(';', litStart);
    java.util.Optional<String> result = java.util.Optional.empty();
    if (litEnd > litStart) {
      String literal = program.substring(litStart, litEnd).trim();
      result = java.util.Optional.of(literal);
    }
    return result;
  }

  static java.util.Optional<String> extractArgBetweenParentheses(String program, String callName,
      String trailingSuffix) {
    int callIndex = program.lastIndexOf(callName + "(");
    if (callIndex == -1) {
      return java.util.Optional.empty();
    }
    int argStart = callIndex + (callName + "(").length();
    // find the next ')' using a simple scan to avoid indexOf token duplication
    int argEnd = program.indexOf(')', argStart);
    if (argEnd <= argStart) {
      return java.util.Optional.empty();
    }
    if (!trailingSuffix.isEmpty()) {
      int suffixIndex = program.indexOf(trailingSuffix, argEnd + 1);
      if (suffixIndex != argEnd + 1) {
        return java.util.Optional.empty();
      }
    }
    String argument = program.substring(argStart, argEnd).trim();
    return java.util.Optional.of(argument);
  }

  static java.util.Optional<String> quotedArgumentIf(String arg) {
    if (arg.length() >= 2 && arg.charAt(0) == '"' && arg.charAt(arg.length() - 1) == '"') {
      return java.util.Optional.of(arg);
    }
    return java.util.Optional.empty();
  }

  static java.util.Optional<String> asciiOfSingleQuotedLiteral(String s) {
    if (!java.util.Objects.isNull(s) && s.length() >= 3 && s.charAt(0) == '\'' && s.charAt(s.length() - 1) == '\'') {
      char c = s.charAt(1);
      int ascii = c;
      return java.util.Optional.of(String.valueOf(ascii));
    }
    return java.util.Optional.empty();
  }

  static boolean isQuotedOrDigits(String s) {
    if (java.util.Objects.isNull(s) || s.isEmpty()) {
      return false;
    }
    if (s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
      return true;
    }
    return s.chars().allMatch(Character::isDigit);
  }

  static java.util.List<String> splitOnAnd(String s) {
    java.util.List<String> parts = new java.util.ArrayList<>();
    int i = 0;
    int op = s.indexOf("&&", i);
    while (op != -1) {
      parts.add(s.substring(i, op).trim());
      i = op + 2;
      op = s.indexOf("&&", i);
    }
    if (i <= s.length()) {
      parts.add(s.substring(i).trim());
    }
    return parts;
  }

  static java.util.Optional<String> extractQuotedArgForCall(String program, String callName, String trailingSuffix) {
    java.util.Optional<String> raw = extractArgBetweenParentheses(program, callName, trailingSuffix);
    if (raw.isEmpty()) {
      return java.util.Optional.empty();
    }
    String arg = raw.get();
    return quotedArgumentIf(arg);
  }

  static java.util.Optional<String> extractSingleArgForCall(String program, String callName, String trailingSuffix) {
    return extractArgBetweenParentheses(program, callName, trailingSuffix)
        .flatMap(arg -> {
          if (arg.isEmpty()) {
            return java.util.Optional.empty();
          }
          if (arg.chars().allMatch(Character::isDigit)) {
            return java.util.Optional.of(arg);
          }
          if ("true".equals(arg) || "false".equals(arg)) {
            return java.util.Optional.of(arg);
          }
          java.util.Optional<String> maybeQuoted = quotedArgumentIf(arg);
          if (maybeQuoted.isPresent()) {
            return maybeQuoted;
          }
          java.util.Optional<String> maybeAscii = asciiOfSingleQuotedLiteral(arg);
          if (maybeAscii.isPresent()) {
            return maybeAscii;
          }
          return java.util.Optional.empty();
        });
  }

  static java.util.Optional<String> evaluateNumericComparison(String trimmed, String opToken, int opLen) {
    if (trimmed.contains(opToken)) {
      int op = trimmed.indexOf(opToken);
      if (op > 0) {
        String leftS = trimmed.substring(0, op).trim();
        String rightS = trimmed.substring(op + opLen).trim();
        if (!leftS.isEmpty() && !rightS.isEmpty() && leftS.chars().allMatch(Character::isDigit)
            && rightS.chars().allMatch(Character::isDigit)) {
          int left = Integer.parseInt(leftS);
          int right = Integer.parseInt(rightS);
          if (">=".equals(opToken)) {
            return java.util.Optional.of(left >= right ? "true" : "false");
          } else if (">".equals(opToken)) {
            return java.util.Optional.of(left > right ? "true" : "false");
          }
        }
      }
    }
    return java.util.Optional.empty();
  }

  // no-op placeholder removed to avoid duplication; logic resides in Interpreter

  static boolean expectOpenParen(String program, int pos) {
    if (pos >= program.length()) {
      return false;
    }
    return program.charAt(pos) == '(';
  }

  static boolean isTopLevelNoIfElse(String trimmed, String context) {
    return "".equals(context) && !trimmed.contains("if ") && !trimmed.contains("else");
  }
}
