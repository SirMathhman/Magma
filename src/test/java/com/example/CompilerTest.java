package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
  @Test
  void compileThrowsCompileException() {
    Compiler compiler = new Compiler();
    CompileException exception = assertThrows(
        CompileException.class,
        () -> compiler.compile("test source"));
    assertTrue(exception.getMessage().contains("Compilation failed"));
  }

  @Test
  void compileReturnsEmptyStringForEmptyInput() throws CompileException {
    Compiler compiler = new Compiler();
    assertEquals("", compiler.compile(""));
    assertEquals("", compiler.compile(null));
  }

  @Test
  void compileLetI32Statement() throws CompileException {
    Compiler compiler = new Compiler();
    String magma = "let x : I32 = 100;";
    String expectedC = "int x = 100;";
    assertEquals(expectedC, compiler.compile(magma));
  }
}
