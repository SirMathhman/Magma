package org.example;

/** Minimal Intrepreter class. */
public class Intrepreter {
  public String interpret(String input) throws InterpretingException {
    if (input == null || input.isEmpty()) {
      throw new InterpretingException("Undefined value", String.valueOf(input));
    }

    // 1) Fast path: plain decimal integer => echo back exactly.
    if (isAllDigits(input)) {
      return input;
    }

    // 1a) Fast path: boolean literals
    if (isBooleanLiteral(input)) {
      return input;
    }

    // 1b) Block: "{ ... }" => evaluate inner content as a program
    int start = skipSpaces(input, 0);
    int end = input.length() - 1;
    while (end >= start && isSpace(input.charAt(end)))
      end--;
    if (start < input.length() && end >= start && input.charAt(start) == '{' && input.charAt(end) == '}') {
      String inner = input.substring(start + 1, end);
      return interpret(inner);
    }

    // 2) Minimal language:
    // - let [mut] <id> = <int>; <expr>
    // - let [mut] <id> = <int>; <id> = <int>; <expr>
    // Where <expr> is either <id> or <int>. If reassignment occurs, it must be
    // the same identifier and only allowed when declared with 'mut'.
    int i = 0;
    final int n = input.length();

    i = skipSpaces(input, i);

    if (startsWithWord(input, i, "let")) {
      i = consumeKeywordWithSpace(input, i, "let");

      boolean isMutable = false;
      // optional 'mut' followed by at least one space
      if (startsWithWord(input, i, "mut")) {
        i = consumeKeywordWithSpace(input, i, "mut");
        isMutable = true;
      }

      // identifier
      int idStart = i;
      String ident = parseIdentifier(input, i);
      if (ident == null) {
        throw new InterpretingException("Undefined value", input);
      }
      i = idStart + ident.length();

      // spaces and '='
      i = skipSpaces(input, i);
      i = expectCharOrThrow(input, i, '=');
      i = skipSpaces(input, i);

      // initializer: int | bool | block
      ValueParseResult init = parseValue(input, i);
      if (init == null) {
        throw new InterpretingException("Undefined value", input);
      }
      String intLit = init.value;
      i = init.nextIndex;

      // spaces
      i = skipSpaces(input, i);

      // ';' and spaces
      i = consumeSemicolonAndSpaces(input, i);

      // Next can be either an expression or a reassignment, then an expression.
      String currentVal = intLit;

      if (i < n && isIdentStart(input.charAt(i))) {
        String ref = parseIdentifier(input, i);
        if (ref == null) {
          throw new InterpretingException("Undefined value", input);
        }
        i += ref.length();
        int afterRef = skipSpaces(input, i);
        if (afterRef < n && input.charAt(afterRef) == '=') {
          // It's a reassignment statement: <id> = <int>;
          if (!ref.equals(ident)) {
            throw new InterpretingException("Undefined value", ref);
          }
          if (!isMutable) {
            throw new InterpretingException("Undefined value", ref);
          }
          i = afterRef + 1; // consume '='
          i = skipSpaces(input, i);
          ValueParseResult re = parseValue(input, i);
          if (re == null) {
            throw new InterpretingException("Undefined value", input);
          }
          String reassigned = re.value;
          i = re.nextIndex;
          i = skipSpaces(input, i);
          i = consumeSemicolonAndSpaces(input, i);
          currentVal = reassigned;

          // After reassignment, we expect the final expression
          if (i >= n) {
            throw new InterpretingException("Undefined value", input);
          }
        } else {
          // Not an assignment; treat the identifier we already parsed as the expression
          if (!ref.equals(ident)) {
            throw new InterpretingException("Undefined value", ref);
          }
          // trailing spaces already in 'afterRef'
          i = afterRef;
          // trailing spaces
          i = skipSpaces(input, i);
          ensureNoTrailing(input, i);
          return currentVal;
        }
      }

      // If we get here, parse the final expression: either identifier or integer
      String result;
      if (i < n && isIdentStart(input.charAt(i))) {
        String ref2 = parseIdentifier(input, i);
        if (ref2 == null) {
          throw new InterpretingException("Undefined value", input);
        }
        i += ref2.length();
        if (!ref2.equals(ident)) {
          throw new InterpretingException("Undefined value", ref2);
        }
        result = currentVal;
      } else {
        ValueParseResult v = parseValue(input, i);
        if (v == null) {
          throw new InterpretingException("Undefined value", input);
        }
        i = v.nextIndex;
        result = v.value;
      }

      // trailing spaces
      i = skipSpaces(input, i);
      ensureNoTrailing(input, i);
      return result;
    }

    // Anything else is currently undefined.
    throw new InterpretingException("Undefined value", input);
  }

  private static boolean isAllDigits(String s) {
    if (s.isEmpty())
      return false;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c < '0' || c > '9')
        return false;
    }
    return true;
  }

  private static boolean startsWithWord(String s, int i, String word) {
    int n = s.length();
    int w = word.length();
    if (i + w - 1 >= n)
      return false;
    for (int k = 0; k < w; k++) {
      if (s.charAt(i + k) != word.charAt(k))
        return false;
    }
    return true;
  }

  private static boolean isSpace(char c) {
    return c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\f';
  }

  private static int skipSpaces(String s, int i) {
    final int n = s.length();
    while (i < n && isSpace(s.charAt(i)))
      i++;
    return i;
  }

  private static int consumeKeywordWithSpace(String s, int i, String word) {
    if (!startsWithWord(s, i, word)) {
      throw new InterpretingException("Undefined value", s);
    }
    i += word.length();
    if (i >= s.length() || !isSpace(s.charAt(i))) {
      throw new InterpretingException("Undefined value", s);
    }
    return skipSpaces(s, i);
  }

  private static boolean isIdentStart(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
  }

  private static boolean isIdentPart(char c) {
    return isIdentStart(c) || (c >= '0' && c <= '9');
  }

  private static boolean isBooleanLiteral(String s) {
    return "true".equals(s) || "false".equals(s);
  }

  private static final class ValueParseResult {
    final String value;
    final int nextIndex;

    ValueParseResult(String value, int nextIndex) {
      this.value = value;
      this.nextIndex = nextIndex;
    }
  }

  // Parses a value at position i: integer | boolean | block { ... }
  private static ValueParseResult parseValue(String s, int i) {
    final int n = s.length();
    if (i >= n)
      return null;
    // block
    if (s.charAt(i) == '{') {
      int close = findMatchingBrace(s, i);
      if (close < 0)
        return null;
      String inner = s.substring(i + 1, close);
      String val = new Intrepreter().interpret(inner);
      return new ValueParseResult(val, close + 1);
    }
    // boolean
    if (startsWithWord(s, i, "true")) {
      return new ValueParseResult("true", i + 4);
    }
    if (startsWithWord(s, i, "false")) {
      return new ValueParseResult("false", i + 5);
    }
    // integer
    String intLit = parseInteger(s, i);
    if (intLit != null) {
      return new ValueParseResult(intLit, i + intLit.length());
    }
    return null;
  }

  private static String parseIdentifier(String s, int i) {
    int n = s.length();
    if (i >= n || !isIdentStart(s.charAt(i)))
      return null;
    int j = i + 1;
    while (j < n && isIdentPart(s.charAt(j)))
      j++;
    return s.substring(i, j);
  }

  private static String parseInteger(String s, int i) {
    int n = s.length();
    if (i >= n)
      return null;
    int j = i;
    while (j < n) {
      char c = s.charAt(j);
      if (c < '0' || c > '9')
        break;
      j++;
    }
    if (j == i)
      return null; // no digits
    return s.substring(i, j);
  }

  private static int expectCharOrThrow(String input, int i, char expected) {
    if (i >= input.length() || input.charAt(i) != expected) {
      throw new InterpretingException("Undefined value", input);
    }
    return i + 1;
  }

  private static int consumeSemicolonAndSpaces(String s, int i) {
    i = expectCharOrThrow(s, i, ';');
    return skipSpaces(s, i);
  }

  private static void ensureNoTrailing(String s, int i) {
    if (i != s.length()) {
      throw new InterpretingException("Undefined value", s.substring(i));
    }
  }

  private static int findMatchingBrace(String s, int openIndex) {
    if (openIndex < 0 || openIndex >= s.length() || s.charAt(openIndex) != '{')
      return -1;
    int depth = 0;
    for (int j = openIndex; j < s.length(); j++) {
      char c = s.charAt(j);
      if (c == '{')
        depth++;
      else if (c == '}') {
        depth--;
        if (depth == 0)
          return j;
      }
    }
    return -1; // no match
  }
}
