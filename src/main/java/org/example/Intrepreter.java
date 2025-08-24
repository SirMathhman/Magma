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
    // If the input ends with the type-suffix "I32", strip it.
    if (input.endsWith("I32")) {
      return input.substring(0, input.length() - 3);
    }
    return input;
  }
}
