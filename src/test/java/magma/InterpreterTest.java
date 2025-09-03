package magma;

import org.junit.jupiter.api.Test;

public class InterpreterTest {
  @Test
  void error() {
  TestHelpers.assertInvalid("test", """
        Undefined identifier.

        File: <virtual>

        1) test
           ^^^^""");
  }

  @Test
  void errorTwoLines() {
  TestHelpers.assertInvalid("test\r\nother", """
        Undefined identifier.

        File: <virtual>

        1) test
           ^^^^
        2) other""");
  }

  @Test
  void integer() {
  TestHelpers.assertValid("5", "5");
  }

  @Test
  void add() {
  TestHelpers.assertValid("5 + 3", "8");
  }

  @Test
  void trueTest() {
  TestHelpers.assertValid("true", "true");
  }

  @Test
  void falseTest() {
  TestHelpers.assertValid("false", "false");
  }

  @Test
  void addRequiresIntLeft() {
  TestHelpers.assertInvalid("false + 1", """
        Addition requires integer on the left-hand side.

        File: <virtual>

        1) false + 1
           ^^^^^""");
  }

  @Test
  void addRequiresIntRight() {
  TestHelpers.assertInvalid("1 + false", """
        Addition requires integer on the right-hand side.

        File: <virtual>

        1) 1 + false
               ^^^^^""");
  }

  @Test
  void addArbitraryLength() {
  TestHelpers.assertValid("1 + 2 + 3", "6");
  }

  // All helpers are in TestHelpers
}
