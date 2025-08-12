package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationTest {

  private void assertValid(String input, String expected) throws ApplicationException {
    Application app = new Application();
    String result = app.compile(input);
    assertEquals(expected, result);
  }

  @Test
  void compile_emptyString_returnsEmptyString() throws ApplicationException {
    assertValid("", "");
  }

  @Test
  void compile_letStatement_returnsInt32t() throws ApplicationException {
    assertValid("let x = 200;", "int32_t x = 200;");
  }

  @Test
  void compile_nonEmptyString_throwsException() {
    Application app = new Application();
    assertThrows(ApplicationException.class, () -> {
      app.compile("not empty");
    });
  }
}
