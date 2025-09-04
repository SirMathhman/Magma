package magma;

import org.junit.jupiter.api.Test;

public class InterpreterLogicTest {
  @Test
  void interpretLogicalAndReturnsTrue() {
    TestHelper.assertInterpretsTo("true && true", "true");
  }

  @Test
  void interpretIfWithLogicalAndConditionReturnsThenBranch() {
    TestHelper.assertInterpretsTo("if (true && true) 3 else 5", "3");
  }

  @Test
  void interpretComparisonReturnsFalse() {
    TestHelper.assertInterpretsTo("3 >= 5", "false");
  }

  @Test
  void interpretNotEqualsReturnsTrue() {
    TestHelper.assertInterpretsTo("5 != 3", "true");
  }

  @Test
  void interpretNotEqualsReturnsFalse() {
    TestHelper.assertInterpretsTo("3 != 3", "false");
  }
}
