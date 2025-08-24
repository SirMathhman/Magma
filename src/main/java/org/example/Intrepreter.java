package org.example;

/** Minimal Intrepreter class. */
public class Intrepreter {
  // intentionally minimal implementation

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
    // If the input ends with a type-suffix like I8/I16/I32/I64 or U8/U16/U32/U64,
    // strip it.
    String[] suffixes = { "I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64" };
    for (String s : suffixes) {
      if (input.endsWith(s)) {
        return input.substring(0, input.length() - s.length());
      }
    }
    return input;
  }
}
