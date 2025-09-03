package magma;

import org.junit.jupiter.api.Test;

public class AddSubTest {
  @Test
  void add() {
    TestHelpers.assertValid("5 + 3", "8");
  }

  @Test
  void addArbitraryLength() {
    TestHelpers.assertValid("1 + 2 + 3", "6");
  }

  @Test
  void subtract() {
    TestHelpers.assertValid("5 - 3", "2");
  }

  @Test
  void addSubtractArbitraryMix() {
    TestHelpers.assertValid("1 + 2 - 3", "0");
  }
}
