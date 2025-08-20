package com.example.magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
  @Test
  void read() {
    assertValidWithPrelude("readInt()", "10", 10);
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
