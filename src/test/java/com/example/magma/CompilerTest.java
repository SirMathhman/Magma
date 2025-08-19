package com.example.magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
  @Test
  void compile_wrapsInput() {
    String input = "hello";
    String result = Compiler.compile(input);
    assertEquals("<compiled>hello</compiled>", result);
  }

  @Test
  void compile_null_throws() {
    assertThrows(IllegalArgumentException.class, () -> Compiler.compile(null));
  }
}
