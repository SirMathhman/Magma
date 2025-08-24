package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TestUtils {
  private TestUtils() {}

  public static void assertValid(String input, String expected) {
    Intrepreter i = new Intrepreter();
    assertEquals(expected, i.interpret(input));
  }

  public static void assertInvalid(String input) {
    Intrepreter i = new Intrepreter();
    assertThrows(InterpretingException.class, () -> i.interpret(input));
  }
}
