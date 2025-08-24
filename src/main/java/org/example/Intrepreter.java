package org.example;

/** Minimal Intrepreter class. */
public class Intrepreter {
  // intentionally minimal implementation

  private static final String[] TYPE_SUFFIXES = { "I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64" };

  /**
   * Interpret the given input string and return a result string.
   * Current implementation echoes the input.
   *
   * @param input the input string to interpret
   * @return the interpreted string (echoes input)
   */
  public String interpret(String input) {
    if (input == null)
      return null;
    // If the input is a simple binary integer expression like "5 + 7", "5 - 2", or
    // "3 * 4", evaluate it.
    String trimmed = input.trim();
    // Manual parse: find operator (+, -, *) that is not part of a number's leading
    // minus.
    int opIndex = -1;
    char opChar = 0;
    for (int i = 0; i < trimmed.length(); i++) {
      char c = trimmed.charAt(i);
      if ((c == '+' || c == '*' || c == '-')) {
        // if '-' is at position 0 or follows a space and is part of a number's sign,
        // skip detecting it as operator
        if (c == '-') {
          // treat as operator if it's not the leading sign of the first number or the
          // leading sign of the second number
          if (i == 0) {
            continue; // leading sign of first number
          }
          // if previous non-space char is a digit, consider this '-' an operator
          int j = i - 1;
          while (j >= 0 && Character.isWhitespace(trimmed.charAt(j)))
            j--;
          if (j >= 0 && Character.isDigit(trimmed.charAt(j))) {
            opIndex = i;
            opChar = c;
            break;
          } else {
            continue;
          }
        } else {
          // + or * are always operators
          opIndex = i;
          opChar = c;
          break;
        }
      }
    }

    if (opIndex != -1) {
      try {
        String left = trimmed.substring(0, opIndex).trim();
        String right = trimmed.substring(opIndex + 1).trim();
        // Support arithmetic when both operands have the same type suffix (e.g. 10I32 +
        // 8I32).
        // Mixed typed/plain or mismatched typed operands remain invalid.
        String leftSuffix = getTypeSuffix(left);
        String rightSuffix = getTypeSuffix(right);
        int a;
        int b;
        if (leftSuffix == null && rightSuffix == null) {
          a = Integer.parseInt(left);
          b = Integer.parseInt(right);
        } else if (leftSuffix != null && rightSuffix != null) {
          if (!leftSuffix.equals(rightSuffix)) {
            throw new InterpretingException(
                "Typed operands must have matching types: '" + input + "'");
          }
          String leftNum = stripTypeSuffix(left);
          String rightNum = stripTypeSuffix(right);
          a = Integer.parseInt(leftNum);
          b = Integer.parseInt(rightNum);
        } else {
          // one operand typed and the other not -> invalid
          throw new InterpretingException(
              "Typed operands are not allowed in arithmetic expressions: '" + input + "'");
        }
        int result;
        switch (opChar) {
          case '+':
            result = a + b;
            break;
          case '-':
            result = a - b;
            break;
          case '*':
            result = a * b;
            break;
          default:
            return input;
        }
        return Integer.toString(result);
      } catch (NumberFormatException e) {
        // fall through to suffix stripping / echoing
      }
    }

    // If the input ends with a type-suffix like I8/I16/I32/I64 or U8/U16/U32/U64,
    // strip it.
    return stripTypeSuffix(input);
  }

  /**
   * Return the type suffix if the string ends with a known type suffix, otherwise
   * null.
   */
  private String getTypeSuffix(String str) {
    if (str == null)
      return null;
    for (String suffix : TYPE_SUFFIXES) {
      if (str.endsWith(suffix)) {
        return suffix;
      }
    }
    return null;
  }

  /**
   * Strip type suffix from input if present.
   * 
   * @param input the input string
   * @return the input with type suffix removed, or the original input if no
   *         suffix found
   */
  private String stripTypeSuffix(String input) {
    for (String suffix : TYPE_SUFFIXES) {
      if (input.endsWith(suffix)) {
        return input.substring(0, input.length() - suffix.length());
      }
    }
    return input;
  }
}
