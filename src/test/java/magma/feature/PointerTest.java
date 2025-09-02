package magma.feature;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertAllValidWithPrelude;

public class PointerTest {
  @Test
  void basicPointerDereference() {
    // let x : I32 = 0; let y : *I32 = &x; let z : I32 = *y; z
    assertAllValidWithPrelude("let x : I32 = 0; let y : *I32 = &x; let z : I32 = *y; z", "", "0");
  }
}
