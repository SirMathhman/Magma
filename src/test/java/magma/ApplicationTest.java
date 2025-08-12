package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationTest {
  private void assertValid(String input, String expected) {
    Application app = new Application();
    try {
      String result = app.compile(input);
      assertEquals(expected, result);
    } catch (Exception e) {
      org.junit.jupiter.api.Assertions.fail("Unexpected exception: " + e.getMessage());
    }
  }

  @Test
  void valid() {
    assertValid("", "");
  }

  @Test
  void let() {
    assertValid("let x = 100;", "int32_t x = 100;");
  }

  @Test
  void letName() {
    assertValid("let y = 100;", "int32_t y = 100;");
  }

  @Test
  void letValue() {
    assertValid("let z = 200;", "int32_t z = 200;");
  }

  @Test
  void letType() {
    assertValid("let z : I32 = 200;", "int32_t z = 200;");
  }

  @Test
  void invalid() {
    Application app = new Application();
    assertThrows(ApplicationException.class, () -> {
      app.compile("not empty");
    });
  }
}
