package com.example.magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
  @Test
  void invalid() {
    assertInvalid("test");
  }

  private void assertInvalid(String input) {
    assertThrows(CompileException.class, () -> {
      Runner.writeAndRun("", Compiler.compile(input));
    });
  }

  private static final String PRELUDE = """
      intrinsic fn readInt() : I32;
      """;

  private void assertValidWithPrelude(String input, String stdin, int exitCode) {
    assertValid(PRELUDE + input, stdin, exitCode);
  }

  private void assertValid(String input, String stdin, int exitCode) {
    String compiled;
    try {
      compiled = Compiler.compile(input);
    } catch (CompileException e) {
      fail("Magma compilation failed: ", e);
      return;
    }

    try {
      assertEquals(exitCode, Runner.writeAndRun(stdin, compiled));
    } catch (Exception e) {
      fail("C compilation failed. IN: " + input + "\r\nOUT: " + compiled, e);
    }
  }
}
