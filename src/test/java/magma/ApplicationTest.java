package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {
  @Test
  void compileEmptyInputProducesEmptyOutput() {
    assertEquals("", Application.compile(""));
  }
}
