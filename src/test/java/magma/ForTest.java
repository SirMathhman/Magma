package magma;

import org.junit.jupiter.api.Test;

public class ForTest {
  @Test
  void sumWithForAndMutableLoopVar() {
    TestHelpers.assertValid(
        "let mut sum = 0; for (let mut i = 0; i < readInt(); i ++) { sum += i; } sum",
        "5",
        "10");
  }
}
