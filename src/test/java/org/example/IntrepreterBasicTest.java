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
}