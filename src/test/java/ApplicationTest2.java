import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class ApplicationTest2 {
  private static final String PRELUDE = "intrinsic fn readInt() : I32; ";

  private void assertValid(String source, String input, int expected) {
    try {
      org.junit.jupiter.api.Assertions.assertEquals(expected, Runner.run(source, input));
    } catch (CompileException | RunException e) {
      fail(e);
    }
  }

  private void assertValidWithPrelude(String source, String input, int expected) {
    assertValid(PRELUDE + source, input, expected);
  }

  @Test
  void letWithExplicitType() {
    assertValidWithPrelude("let x: I32 = readInt(); x", "100", 100);
  }

  @Test
  void letMultiple() {
    assertValidWithPrelude("let x = readInt(); let y = readInt(); x + y", "100\r\n200", 300);
  }

  @Test
  void letWithMut() {
    assertValidWithPrelude("let mut x = 0; x = readInt(); x", "100", 100);
  }

  @Test
  void letWithoutMut() {
    org.junit.jupiter.api.Assertions.assertThrows(CompileException.class, () -> Runner.run("let x = 0; x = 1;", ""));
  }
}
