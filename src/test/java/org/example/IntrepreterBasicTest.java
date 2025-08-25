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
}