package magma.struct;

import static magma.infra.TestUtils.*;
import org.junit.jupiter.api.Test;

public class ArrayTest {
  @Test
  void simpleArrayLiteralAndIndex() {
    // Expect array literal syntax and indexing to return the correct element
    assertValidWithPrelude("let array : [I32; 3] = [1, 2, 3]; array[1]", "", 2);
  }
}
