package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TestUtils {
  private TestUtils() {}

  public static void assertValid(String input, String expected) {
    Interpreter i = new Interpreter();
    assertEquals(expected, i.interpret(input));
  }

  public static void assertInvalid(String input) {
    Interpreter i = new Interpreter();
    assertThrows(InterpretingException.class, () -> i.interpret(input));
  }
}
