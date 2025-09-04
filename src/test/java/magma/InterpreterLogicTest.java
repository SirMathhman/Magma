package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
