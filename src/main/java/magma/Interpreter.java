package magma;

/**
 * Minimal Interpreter with a single public method `interpret(String)` that
 * returns a String.
 * - No exceptions for normal control flow.
 * - Avoids banned tokens and keeps the API tiny for tests/examples.
 */
public final class Interpreter {

  /**
   * Interpret the given input and return a string result.
   * If the input is an integer literal (e.g. "123", "-5"), returns the integer as
   * a decimal string.
   * Otherwise returns an error message that starts with "error: ".
   */
  public String interpret(String input) {
    String s = java.util.Objects.toString(input, "").strip();
    if (s.isEmpty()) {
      return "error: empty input";
    }

    int idx = 0;
    int sign = 1;
    char first = s.charAt(0);
    if (first == '+' || first == '-') {
      if (first == '-') {
        sign = -1;
      }
      idx = 1;
      if (idx >= s.length()) {
        return "error: invalid integer";
      }
    }

    int value = 0;
    for (; idx < s.length(); idx++) {
      char c = s.charAt(idx);
      if (c < '0' || c > '9') {
        return "error: invalid character: " + c;
      }
      value = value * 10 + (c - '0');
    }

    return Integer.toString(sign * value);
  }

}
