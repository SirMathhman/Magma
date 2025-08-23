package magma;

import org.junit.jupiter.api.Test;

public class BlockLetInitTest {
  @Test
  public void letInitializedFromBlock() {
    TestUtils.assertValidWithPrelude("let x = {readInt()}; x", "9", 9);
  }
}
