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
}
