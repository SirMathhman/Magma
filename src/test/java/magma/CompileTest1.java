package magma;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CompileTest1 {
  @Test
  void undefined() {
    assertTrue(Runner.run("readInt", "") instanceof Result.Err);
  }

  @Test
  void pass() {
    TestHelpers.assertValid("readInt()", "10", "10");
  }

  @Test
  void empty() {
    TestHelpers.assertValid("", "", "");
  }

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
  void trueTest() {
    TestHelpers.assertValid("intrinsic fn readInt() : I32; true", "", "true");
  }

  @Test
  void letInitWithBoolImplicit() {
    TestHelpers.assertValid("let x = true; x", "", "true");
  }
}
