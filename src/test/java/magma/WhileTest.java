package magma;

import org.junit.jupiter.api.Test;

public class WhileTest {
  @Test
  void sumWithWhile() {
    TestHelpers.assertValid(
        "let mut sum = 0; let mut i = 0; while (i < readInt()) { sum += i; i ++; } sum",
        "5",
        "10");
  }
}
