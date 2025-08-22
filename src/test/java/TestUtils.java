import static org.junit.jupiter.api.Assertions.fail;

public final class TestUtils {
  public static final String PRELUDE = "intrinsic fn readInt() : I32; ";

  private TestUtils() {
  }

  public static void assertInvalid(String source) {
    org.junit.jupiter.api.Assertions.assertThrows(CompileException.class, () -> Runner.run(source, ""));
  }

  public static void assertValid(String source, String input, int expected) {
    try {
      org.junit.jupiter.api.Assertions.assertEquals(expected, Runner.run(source, input));
    } catch (CompileException | RunException e) {
      fail(e);
    }
  }

  public static void assertValidWithPrelude(String source, String input, int expected) {
    assertValid(PRELUDE + source, input, expected);
  }
}
