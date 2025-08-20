package com.example.magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CompilerTest {
  @Test
  void read() {
    assertValidWithPrelude("readInt()", "10", 10);
  }

  @Test
  void add() {
    assertValidWithPrelude("readInt() + readInt()", "10\r\n20", 30);
  }

  @Test
  void subtract() {
    assertValidWithPrelude("readInt() - readInt()", "30\r\n10", 20);
  }

  @Test
  void multiply() {
    assertValidWithPrelude("readInt() * readInt()", "5\r\n6", 30);
  }

  @Test
  void let() {
    assertValidWithPrelude("let x = readInt(); x", "100", 100);
  }

  @Test
  void letMultiple() {
    assertValidWithPrelude("let x = readInt(); let y = readInt(); x + y", "100\r\n200", 300);
  }

  @Test
  void trueTest() {
    assertValidWithPrelude("true", "", 1);
  }

  @Test
  void falseTest() {
    assertValidWithPrelude("false", "", 0);
  }

  @Test
  void letWithExplicitType() {
    assertValidWithPrelude("let x: I32 = readInt(); x", "100", 100);
  }

  @Test
  void letWithBoolType() {
    assertValidWithPrelude("let x: Bool = true; x", "", 1);
  }

  @Test
  void letWithMismatchedType() {
    assertInvalid("let x: I32 = true;");
  }

  @Test
  void letWithSameName() {
    assertInvalid("let x = 1; let x = 2;");
  }

  @Test
  void assignWithMut() {
    assertValidWithPrelude("let mut x = 0; x = readInt(); x", "100", 100);
  }

  @Test
  void assignWithoutMut() {
    assertInvalid("let x = 1; x = 2;");
  }

  @Test
  void assignWithMismatchedType() {
    assertInvalid("let mut x = 1; x = true;");
  }

  @Test
  void assignUndefined() {
    assertInvalid("let x = 1; x = y;");
  }

  @Test
  void undefined() {
    assertInvalid("readInt");
  }

  @Test
  void notAFunction() {
    assertInvalid("let readInt = 100; readInt()");
  }

  @Test
  void function() {
    assertValidWithPrelude("fn get() => readInt(); get()", "100", 100);
  }

  @Test
  void functionAlreadyDefined() {
    assertInvalid(PRELUDE + "fn readInt() => {}");
  }

  @Test
  void functionWithOneParameter() {
    assertValidWithPrelude("fn get(x: I32) => x; get(readInt())", "100", 100);
  }

  @Test
  void functionWithTwoParameters() {
    assertValidWithPrelude("fn add(x: I32, y: I32) => x + y; add(readInt(), readInt())", "100\r\n200", 300);
  }

  @Test
  void functionCallWithInsufficientArguments() {
    assertInvalid("fn add(x: I32, y: I32) => x + y; add(readInt())");
  }

  @Test
  void functionWithInvalidArgumentType() {
    assertInvalid("fn add(x: I32, y: I32) => x + y; add(readInt(), true)");
  }

  private void assertInvalid(String input) {
    assertThrows(CompileException.class, () -> Runner.run(input, ""));
  }

  private static final String PRELUDE = """
      extern fn readInt() : I32;
      """;

  private void assertValidWithPrelude(String input, String stdin, int exitCode) {
    assertValid(PRELUDE + input, exitCode, stdin);
  }

  private void assertValid(String input, int exitCode, String stdin) {
    assertEquals(exitCode, Runner.run(input, stdin));
  }
}
