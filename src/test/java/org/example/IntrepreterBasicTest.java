package org.example;

import static org.example.TestUtils.assertInvalid;
import static org.example.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

public class IntrepreterBasicTest {
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
}