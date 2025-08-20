package com.example.magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
  @Test
  void read() {
    assertValidWithPrelude("readInt()", "10", 10);
  }

  @Test
  void add() {
    assertValidWithPrelude("readInt() + readInt()", "5\r\n10", 15);
  }

  @Test
  void subtract() {
    assertValidWithPrelude("readInt() - readInt()", "10\r\n5", 5);
  }

  @Test
  void multiply() {
    assertValidWithPrelude("readInt() * readInt()", "5\r\n10", 50);
  }

  private static final String PRELUDE = """
      extern fn readInt() : I32; """;

  private void assertValidWithPrelude(String input, String stdin, int exitCode) {
    assertValid(PRELUDE + input, exitCode, stdin);
  }

  private void assertValid(String input, int exitCode, String stdin) {
    assertEquals(exitCode, Runner.run(input, stdin));
  }
}
