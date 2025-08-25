package org.example;

import static org.example.TestUtils.assertInvalid;

import org.junit.jupiter.api.Test;

public class IntrepreterBasicTest {
  @Test
  void undefined() {
    assertInvalid("test");
  }
}