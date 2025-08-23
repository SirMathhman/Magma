package magma.block;

import static magma.infra.TestUtils.*;
import org.junit.jupiter.api.Test;

public class BlockLetInitTest {
  @Test
  public void letInitializedFromBlock() {
    assertValidWithPrelude("let x = {readInt()}; x", "9", 9);
  }

  @Test
  public void simpleIfTrue() {
    assertValidWithPrelude("let x = if (true) { 5 } else { 6 }; x", "", 5);
  }

  @Test
  public void simpleIfFalse() {
    assertValidWithPrelude("let x = if (false) { 5 } else { 6 }; x", "", 6);
  }
}
