package magma;

import org.junit.jupiter.api.Test;

public class BlockTest {
  @Test
  public void blockWithValue() {
    TestUtils.assertValidWithPrelude("{5}", "", 5);
  }

  @Test
  public void letBeforeBlockIsVisibleInsideBlock() {
    TestUtils.assertValidWithPrelude("let x = readInt(); {x}", "7", 7);
  }

  @Test
  public void letInsideBlockDoesNotLeakOutside() {
    TestUtils.assertInvalid(TestUtils.PRELUDE + "{let x = 10;} x");
  }
}
