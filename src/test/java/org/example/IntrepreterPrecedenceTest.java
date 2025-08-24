package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class IntrepreterPrecedenceTest {
  @Test
  void multiplicationHasPrecedenceOverAdditionAndSubtraction() {
    Intrepreter it = new Intrepreter();
    assertEquals("31", it.interpret("3 + 4 * 7"));
  }
}
