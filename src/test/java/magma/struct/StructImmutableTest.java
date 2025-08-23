package magma.struct;

import static magma.infra.TestUtils.*;
import org.junit.jupiter.api.Test;

public class StructImmutableTest {
  @Test
  void cannotAssignField() {
    // Attempt to assign to a struct field should be invalid
    assertInvalid("struct S { f : I32 } let s = S { readInt() }; s.f = 10;");
  }
}
