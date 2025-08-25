package org.example;

/** Minimal Intrepreter class. */
public class Intrepreter {
  public String interpret(String input) throws InterpretingException {
    if (input == null || input.isEmpty()) {
      throw new InterpretingException("Undefined value", String.valueOf(input));
    }

    // Accept plain decimal integers only; otherwise, it's undefined for now.
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      if (c < '0' || c > '9') {
        throw new InterpretingException("Undefined value", input);
      }
    }

    // Echo valid integer inputs exactly as provided.
    return input;
  }
}
