package magma.feature;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertAllValidWithPrelude;

public class ArrayTest {
  @Test
  void arrayLiteralAndIndexing() {
    assertAllValidWithPrelude("let x : [I32; 1] = [readInt()]; x[0];", "5", "5");
  }

  @Test
  void boolArrayLiteralAndIndexing() {
    // explicit typed array
    assertAllValidWithPrelude("let b : [Bool; 2] = [true, false]; b[0];", "", "true");
    // inferred typed array
    assertAllValidWithPrelude("let b = [true, false]; b[1];", "", "false");
  }
}
