package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {

  @Test
  void greetReturnsExpected() {
    assertEquals("Hello, Magma!", Application.greet());
  }

  @Test
  void mainRunsWithoutException() {
    Application.main(new String[] {});
  }
}
