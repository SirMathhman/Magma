package magma;

import org.junit.jupiter.api.Test;

public class ConditionalTest {
  @Test
  void conditionTrue() {
    TestUtil.assertValidWithPrelude("if (readInt() == readInt()) 3 else 5", "8\r\n8", "3");
  }

  @Test
  void conditionFalse() {
    TestUtil.assertValidWithPrelude("if (readInt() == readInt()) 3 else 5", "8\r\n9", "5");
  }
}
