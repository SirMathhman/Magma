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

  @Test
  void letAndFunctionCannotHaveSameName() {
    assertInvalid("let add = 1; fn add(x: I32, y: I32) => x + y;");
  }

  @Test
  void assignFunctionToLet() {
    assertValidWithPrelude("let func = readInt(); func()", "100", 100);
  }

  @Test
  void intrinsicFunctionCannotHaveBody() {
    assertInvalid("intrinsic fn readInt() : I32 => { return 0; }");
  }

  @Test
  void ternaryTrue() {
    assertValidWithPrelude("let result = if (readInt() == 1) ? 5 : 3; result", "1", 5);
  }

  @Test
  void ternaryFalse() {
    assertValidWithPrelude("let result = if (readInt() == 1) ? 5 : 3; result", "0", 3);
  }

  @Test
  void functionTemplateWithTypeParameter() {
    assertValidWithPrelude("fn identity<T>(x: T) => x; identity(readInt())", "5", 5);
  }

  @Test
  void functionTemplateWithDifferentTypeArguments() {
    assertValidWithPrelude("fn identity<T>(x: T) => x; identity(5)  + (if identity(true) readInt() else 3)", "2", 7);
  }

  @Test
  void structWithOneField() {
    assertValidWithPrelude("struct Point { x: I32 } let p = Point { readInt() }; p.x", "5", 5);
  }

  @Test
  void structWithMultipleFields() {
    assertValidWithPrelude("struct Point { x: I32, y: I32 } let p = Point { readInt(), readInt() }; p.x + p.y",
        "5\r\n10", 15);
  }

  @Test
  void structWithNoFields() {
    assertInvalid("struct Empty {}");
  }

  @Test
  void structHasStructAsFieldType() {
    assertValidWithPrelude(
        "struct Point { x: I32 } struct Circle { center: Point, radius: I32 } let c = Circle { Point { readInt() }, readInt() }; c.center.x + c.radius",
        "5\r\n10", 15);
  }

  @Test
  void letWithExplicitFunctionType() {
    assertValidWithPrelude("let f: () => I32 = readInt; f()", "100", 100);
  }

  @Test
  void letWithExplicitFunctionTypeReassigned() {
    assertValidWithPrelude("let mut f: () => I32 = readInt; f = () => 42; f()", "100", 42);
  }

  @Test
  void equalsTest() {
    assertValidWithPrelude("let a = 5; let b = readInt(); a == b", "5", 1);
  }

  @Test
  void notEquals() {
    assertValidWithPrelude("let a = 5; let b = readInt(); a != b", "4", 1);
  }

  @Test
  void lessThan() {
    assertValidWithPrelude("let a = 5; let b = readInt(); a < b", "6", 1);
  }

  @Test
  void greaterThan() {
    assertValidWithPrelude("let a = 5; let b = readInt(); a > b", "4", 1);
  }

  @Test
  void lessThanEquals() {
    assertValidWithPrelude("let a = 5; let b = readInt(); a <= b", "5", 1);
  }

  @Test
  void greaterThanEquals() {
    assertValidWithPrelude("let a = 5; let b = readInt(); a >= b", "5", 1);
  }

  @Test
  void ifTest() {
    assertValidWithPrelude("if (readInt() == 1) 5 else 3", "1", 5);
  }

  @Test
  void block() {
    assertValid("{}", "", 0);
  }

  @Test
  void blockWithExpression() {
    assertValidWithPrelude("{readInt()}", "", 5);
  }

  @Test
  void letBeforeBraces() {
    assertValidWithPrelude("let x = readInt(); { x }", "5", 5);
  }

  @Test
  void letAfterBraces() {
    assertValidWithPrelude("{} let x = readInt(); x", "5", 5);
  }

  @Test
  void letWithinBraces() {
    assertValidWithPrelude("{let x = readInt(); x}", "10", 10);
  }

  @Test
  void letNameAfterBraces() {
    assertValidWithPrelude("{let x = 10;} let x = readInt(); x", "5", 5);
  }

  @Test
  void blockDefinedInside() {
    assertValidWithPrelude("let x = readInt(); { x }", "5", 5);
  }

  @Test
  void blockNotDefinedOutside() {
    assertInvalid("{let x = 10;} x");
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
