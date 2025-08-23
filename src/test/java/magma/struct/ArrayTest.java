package magma.struct;

import static magma.infra.TestUtils.*;
import org.junit.jupiter.api.Test;

public class ArrayTest {
  @Test
  void simpleArrayLiteralAndIndex() {
    // Expect array literal syntax and indexing to return the correct element
    assertValidWithPrelude("let array : [I32; 3] = [1, 2, 3]; array[1]", "", 2);
  }

  @Test
  void simpleBooleanArrayLiteralAndIndex() {
    // boolean array literals should be accepted and evaluate to 1/0
    assertValidWithPrelude("let b : [Bool; 3] = [true, false, true]; b[0]", "", 1);
  }

  @Test
  void twoDimensionalArrayLiteralAndIndex() {
    // nested array literal and indexing
    assertValidWithPrelude("let m : [[I32; 2]; 2] = [[1, 2], [3, 4]]; m[1][0]", "", 3);
  }
}
