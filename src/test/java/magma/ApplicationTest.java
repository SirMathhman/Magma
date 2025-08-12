package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApplicationTest {
  @Test
  void compile_emptyString_returnsEmptyString() throws ApplicationException {
    Application app = new Application();
    String result = app.compile("");
    org.junit.jupiter.api.Assertions.assertEquals("", result);
  }

  @Test
  void compile_nonEmptyString_throwsException() {
    Application app = new Application();
    assertThrows(ApplicationException.class, () -> {
      app.compile("not empty");
    });
  }
}
