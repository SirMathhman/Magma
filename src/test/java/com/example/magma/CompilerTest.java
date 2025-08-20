package com.example.magma;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

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

  @Test
  void let() {
    assertValidWithPrelude("let x : I32 = readInt(); x", "100", 100);
  }

  @Test
  void letWithImplicitType() {
    assertValidWithPrelude("let x = readInt(); x", "100", 100);
  }

  @Test
  void letMultiple() {
    assertValidWithPrelude("let x : I32 = readInt(); let y : I32 = readInt(); x + y", "100\r\n200", 300);
  }

  @Test
  void function() {
    assertValidWithPrelude("fn get() : I32 => readInt(); get()", "100", 100);
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
