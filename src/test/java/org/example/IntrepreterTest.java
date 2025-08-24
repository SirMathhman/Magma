package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntrepreterTest {
  @Test
  void interpretShouldEchoInput() {
    Intrepreter i = new Intrepreter();
    String input = "hello";
    String out = i.interpret(input);
    assertEquals(input, out);
  }

  @Test
  void interpretShouldStripI32Suffix() {
    Intrepreter i = new Intrepreter();
    String input = "5I32";
    String out = i.interpret(input);
    assertEquals("5", out);
  }
}
