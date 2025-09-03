package magma.feature;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertAllValidWithPrelude;

public class BoolArrayTest {
  @Test
  void boolArrayLiteralAndIndexing() {
    // explicit typed array
    assertAllValidWithPrelude("let b : [Bool; 2] = [true, false]; b[0];", "", "true");
    // inferred typed array
    assertAllValidWithPrelude("let b = [true, false]; b[1];", "", "false");
  }
}
