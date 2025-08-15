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
}
