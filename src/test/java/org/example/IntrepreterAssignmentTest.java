package org.example;

import static org.example.TestUtils.assertValid;
import static org.example.TestUtils.assertInvalid;

import org.junit.jupiter.api.Test;

public class IntrepreterAssignmentTest {
  @Test
  void mutableAssignmentShouldWork() {
    assertValid("let mut x = 0; x = 10; x", "10");
  }

  @Test
  void immutableAssignmentShouldFail() {
    assertInvalid("let x = 0; x = 10; x");
  }
}
