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
  void compileLetUntypedWithSuffix() throws CompileException {
    Compiler compiler = new Compiler();
    assertEquals("uint8_t x = 0;", compiler.compile("let x = 0U8;"));
    assertEquals("int8_t y = -1;", compiler.compile("let y = -1I8;"));
    assertEquals("uint64_t z = 42;", compiler.compile("let z = 42U64;"));
  }

  @Test
  void compileLetTypedWithMatchingSuffix() throws CompileException {
    Compiler compiler = new Compiler();
    assertEquals("int x = 0;", compiler.compile("let x : I32 = 0I32;"));
    assertEquals("uint8_t y = 1;", compiler.compile("let y : U8 = 1U8;"));
  }

  @Test
  void compileLetTypedWithMismatchedSuffixThrows() {
    Compiler compiler = new Compiler();
    CompileException exception = assertThrows(
        CompileException.class,
        () -> compiler.compile("let x : I64 = 0U8;"));
    assertTrue(exception.getMessage().contains("Type mismatch"));
  }
}
