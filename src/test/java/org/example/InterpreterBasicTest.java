package org.example;

import static org.example.TestUtils.assertInvalid;
import static org.example.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

public class InterpreterBasicTest {
  @Test
  void undefined() {
    assertInvalid("test");
  }

  @Test
  void integer() {
    assertValid("10", "10");
  }

  @Test
  void letAssignmentThenRef() {
    assertValid("let x = 10; x", "10");
  }

  @Test
  void letMutThenReassignThenRef() {
    assertValid("let mut x = 10; x = 20; x", "20");
  }

  @Test
  void letImmutableThenReassignShouldFail() {
    assertInvalid("let x = 10; x = 20; x");
  }

  @Test
  void blockWithInteger() {
    assertValid("{5}", "5");
  }

  @Test
  void letWithBlockInitializer() {
    assertValid("let x = {5}; x", "5");
  }

  @Test
  void booleanLiteralTrue() {
    assertValid("true", "true");
  }

  @Test
  void letWithBooleanInitializer() {
    assertValid("let x = true; x", "true");
  }

  @Test
  void letWithIfInitializer() {
    assertValid("let x = if (true) 5 else 3; x", "5");
  }

  @Test
  void typedLetWithIfAssignments() {
    assertValid("let x : I32; if (true) x = 5 else x = 3; x", "5");
  }
}