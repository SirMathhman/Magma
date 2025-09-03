package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArithmeticTest {
  @Test
  void add() {
    TestHelpers.assertValid("readInt() + readInt()", "10\r\n20", "30");
  }

  @Test
  void subtract() {
    TestHelpers.assertValid("readInt() - readInt()", "10\r\n20", "-10");
  }

  @Test
  void multiply() {
    TestHelpers.assertValid("readInt() * readInt()", "10\r\n20", "200");
  }

  @Test
  void divide() {
    TestHelpers.assertValid("readInt() / readInt()", "10\r\n20", "0");
  }

  @Test
  void modulo() {
    TestHelpers.assertValid("readInt() % readInt()", "30\r\n20", "10");
  }

  @Test
  void divideByZeroLiteral() {
    String source = "5 / 0";
    String sourceWithPrelude = "intrinsic fn readInt() : I32; " + source;
    Result<String, RunError> r = Runner.run(sourceWithPrelude, "");
    // expect runtime failure (non-zero exit)
    assertTrue(r instanceof Result.Err);
  }

  @Test
  void divideByZeroLet() {
    String source = "let x = 0; let y = 5 / x;";
    String sourceWithPrelude = "intrinsic fn readInt() : I32; " + source;
    Result<String, RunError> r = Runner.run(sourceWithPrelude, "");
    assertTrue(r instanceof Result.Err);
  }

  @Test
  void mixedPrecedenceReadInt() {
    TestHelpers.assertValid("readInt() + readInt() * readInt()", "10\r\n2\r\n3", "16");
  }

  @Test
  void parenthesizedLiteral() {
    TestHelpers.assertValid("(5)", "", "5");
  }
}
