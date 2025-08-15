package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {
  @Test
  void empty() {
    assertEquals("", Application.compile(""));
  }
  
  @Test
  void compileSimpleLetProducesC() {
    String src = "let x : I32 = 0;";
    String expected = "#include <stdint.h>\r\nint32_t x = 0;";
    assertEquals(expected, Application.compile(src));
  }
}
