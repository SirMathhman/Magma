package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class IntrepreterExpressionTest {
  @Test
  void interpretShouldThrowOnTypedOperands() {
    Intrepreter i = new Intrepreter();
    assertThrows(InterpretingException.class, () -> i.interpret("5I64 + 0U8"));
  }

  @Test
  void interpretShouldRejectPlainPlusTyped() {
    Intrepreter it = new Intrepreter();
    assertThrows(InterpretingException.class, () -> it.interpret("8 + 10U32"));
  }

  @Test
  void multiplicationHasPrecedenceOverAdditionAndSubtraction() {
    Intrepreter it = new Intrepreter();
    assertEquals("31", it.interpret("3 + 4 * 7"));
  }

  @Test
  void parenthesesHavePrecedenceOverMultiplication_leftGrouped() {
    Intrepreter it = new Intrepreter();
    assertEquals("14", it.interpret("(3 + 4) * 2"));
  }

  @Test
  void parenthesesHavePrecedenceOverMultiplication_rightGrouped() {
    Intrepreter it = new Intrepreter();
    assertEquals("18", it.interpret("3 * (4 + 2)"));
  }
}
