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

  @Test
  void functionWithOneParam() {
    assertValidWithPrelude("fn get(x : I32) : I32 => x; get(readInt())", "100", 100);
  }

  @Test
  void functionWithTwoParams() {
    assertValidWithPrelude("fn add(x : I32, y : I32) : I32 => x + y; add(readInt(), readInt())", "100\r\n200", 300);
  }

  @Test
  void undefined() {
    assertThrows(CompileException.class, () -> Runner.run("test", ""));
  }

  @Test
  void notAFunction() {
    assertThrows(CompileException.class, () -> Runner.run("let test = 100; test()", ""));
  }

  @Test
  void structure() {
    assertValidWithPrelude("struct Wrapper {field : I32} let instance = Wrapper {readInt()}; instance.field", "100",
        100);
  }

  @Test
  void structureWithTwoFields() {
    assertValidWithPrelude(
        "struct Wrapper {field1 : I32, field2 : I32} let instance = Wrapper {readInt(), readInt()}; instance.field1 + instance.field2",
        "100\r\n200", 300);
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
