package org.example;

import org.junit.jupiter.api.Test;

public class IntrepreterMixedTypeTest {
  @Test
  void interpretShouldRejectPlainPlusTyped() {
    Intrepreter it = new Intrepreter();
    try {
      it.interpret("8 + 10U32");
      throw new AssertionError("Expected InterpretingException");
    } catch (InterpretingException e) {
      // expected
    }
  }
}
