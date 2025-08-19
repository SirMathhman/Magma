package com.example.magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
  @Test
  void empty() {
    assertValid("", 0);
  }

  @Test
  void integer() {
    assertValid("5", 5);
  }

  private void assertValid(String input, int exitCode) {
    assertEquals(exitCode, Runner.run(input));
  }
}
