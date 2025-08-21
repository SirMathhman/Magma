package com.example.magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
  @Test
  void invalid() {
    assertInvalid("test");
  }

  @Test
  void pass() {
    assertValidWithPrelude("readInt()", "10", 10);
  }

  @Test
  void add() {
    assertValidWithPrelude("readInt() + readInt()", "10\n20", 30);
  }

  @Test
  void subtract() {
    assertValidWithPrelude("readInt() - readInt()", "20\n10", 10);
  }

  @Test
  void multiply() {
    assertValidWithPrelude("readInt() * readInt()", "10\n20", 200);
  }

  @Test
  void let() {
    assertValidWithPrelude("let x = readInt(); x", "100", 100);
  }

  @Test
  void letWithExplicitType() {
    assertValidWithPrelude("let x: I32 = readInt(); x", "100", 100);
  }

  @Test
  void letTrue() {
    assertValidWithPrelude("let x = true; x", "", 1);
  }

  @Test
  void letFalse() {
    assertValidWithPrelude("let x = false; x", "", 0);
  }

  @Test
  void letWithExplicitBooleanType() {
    assertValidWithPrelude("let x: Bool = true; x", "", 1);
  }

  @Test
  void letMismatchedTypes() {
    assertInvalid("let x: Bool = 1;");
  }

  @Test
  void letMultiple() {
    assertValidWithPrelude("let x = readInt(); let y = readInt(); x + y", "1\r\n2", 3);
  }

  @Test
  void letWithSameName() {
    assertInvalid("let x = 1; let x = 2;");
  }

  @Test
  void and() {
    assertValidWithPrelude("true && false", "", 0);
  }

  @Test
  void or() {
    assertValidWithPrelude("true || false", "", 1);
  }

  @Test
  void equalsTo() {
    assertValidWithPrelude("readInt() == readInt()", "10\n10", 1);
  }

  @Test
  void notEqualsTo() {
    assertValidWithPrelude("readInt() != readInt()", "10\n20", 1);
  }

  @Test
  void greaterThan() {
    assertValidWithPrelude("readInt() > readInt()", "20\n10", 1);
  }

  @Test
  void lessThan() {
    assertValidWithPrelude("readInt() < readInt()", "10\n20", 1);
  }

  @Test
  void lessThanOrEqualTo() {
    assertValidWithPrelude("readInt() <= readInt()", "10\n20", 1);
  }

  @Test
  void greaterThanOrEqualTo() {
    assertValidWithPrelude("readInt() >= readInt()", "20\n20", 1);
  }

  @Test
  void ternaryTrue() {
    assertValidWithPrelude("if (readInt() == 1) ? 4 : 5", "1", 4);
  }

  @Test
  void ternaryFalse() {
    assertValidWithPrelude("if (readInt() == 1) ? 4 : 5", "2", 5);
  }

  @Test
  void assignWithMut() {
    assertValidWithPrelude("let mut x = 1; x = readInt(); x", "2", 2);
  }

  @Test
  void assignWithoutMut() {
    assertInvalid("let x = 1; x = readInt();");
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
      fail("Magma compilation failed:\r\n", e);
      return;
    }

    try {
      assertEquals(exitCode, Runner.writeAndRun(stdin, compiled));
    } catch (Exception e) {
      fail("C compilation failed:\r\nIN: " + input + "\r\nOUT: " + compiled, e);
    }
  }
}
