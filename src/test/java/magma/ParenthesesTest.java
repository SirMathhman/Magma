package magma;

public class ParenthesesTest {
  @org.junit.jupiter.api.Test
  public void parenthesizedZero() {
    TestHelpers.assertValid("(0)", "0");
  }
}
