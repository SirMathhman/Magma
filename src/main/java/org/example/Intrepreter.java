package org.example;

/** Minimal Intrepreter class. */
public class Intrepreter {
  public String interpret(String input) throws InterpretingException {
    throw new InterpretingException("Undefined value", input);
  }
}
