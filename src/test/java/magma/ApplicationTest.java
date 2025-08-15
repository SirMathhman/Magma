package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {
  @Test
  void empty() {
    assertEquals("", Application.compile(""));
  }

  @Test
  void let() {
    String src = "let x : I32 = 0;";
    String expected = "#include <stdint.h>\r\nint32_t x = 0;";
    assertEquals(expected, Application.compile(src));
  }

  @Test
  void letDifferentName() {
    String src = "let count : I32 = 10;";
    String expected = "#include <stdint.h>\r\nint32_t count = 10;";
    assertEquals(expected, Application.compile(src));
  }

  @Test
  void letNegativeValue() {
    String src = "let y : I32 = -42;";
    String expected = "#include <stdint.h>\r\nint32_t y = -42;";
    assertEquals(expected, Application.compile(src));
  }

  @Test
  void letOmittedTypeDefaultsToI32() {
    String src = "let x = 100;";
    String expected = "#include <stdint.h>\r\nint32_t x = 100;";
    assertEquals(expected, Application.compile(src));
  }

  @Test
  void letI8() {
    String src = "let a : I8 = -1;";
    String expected = "#include <stdint.h>\r\nint8_t a = -1;";
    assertEquals(expected, Application.compile(src));
  }

  @Test
  void letI64() {
    String src = "let big : I64 = 1234567890123;";
    String expected = "#include <stdint.h>\r\nint64_t big = 1234567890123;";
    assertEquals(expected, Application.compile(src));
  }

  @Test
  void letU8() {
    String src = "let b : U8 = 255;";
    String expected = "#include <stdint.h>\r\nuint8_t b = 255;";
    assertEquals(expected, Application.compile(src));
  }

  @Test
  void letU64() {
    String src = "let ub : U64 = 18446744073709551615;";
    String expected = "#include <stdint.h>\r\nuint64_t ub = 18446744073709551615;";
    assertEquals(expected, Application.compile(src));
  }
}
