package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
  static Object[][] typeCases() {
    return new Object[][] {
        { "U8", "uint8_t" },
        { "U16", "uint16_t" },
        { "U32", "uint32_t" },
        { "U64", "uint64_t" },
        { "I8", "int8_t" },
        { "I16", "int16_t" },
        { "I32", "int" },
        { "I64", "int64_t" }
    };
  }

  @ParameterizedTest
  @MethodSource("typeCases")
  void compileLetTypedStatement(String magmaType, String cType) throws CompileException {
    Compiler compiler = new Compiler();
    String magma = "let x : " + magmaType + " = 100;";
    String expectedC = cType + " x = 100;";
    assertEquals(expectedC, compiler.compile(magma));
  }

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

  // Removed compileLetI32Statement; covered by parameterized test
  // compileLetTypedStatement
}
