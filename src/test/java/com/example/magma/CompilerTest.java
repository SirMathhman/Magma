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

  @Test
  void add() {
    assertValid("3 + 4", 7);
  }

  @Test
  void subtract() {
    assertValid("5 - 3", 2);
  }

  @Test
  void multiply() {
    assertValid("2 * 3", 6);
  }

  private void assertValid(String input, int exitCode) {
    assertEquals(exitCode, Runner.run(input));
  }
}
