package magma.block;

import static magma.infra.TestUtils.*;
import org.junit.jupiter.api.Test;

public class BlockTest {
  @Test
  public void blockWithValue() {
  assertValidWithPrelude("{5}", "", 5);
  }

  @Test
  public void letBeforeBlockIsVisibleInsideBlock() {
  assertValidWithPrelude("let x = readInt(); {x}", "7", 7);
  }

  @Test
  public void letInsideBlockDoesNotLeakOutside() {
  assertInvalid(PRELUDE + "{let x = 10;} x");
  }

  @Test
  public void functionInsideBlockDoesNotLeakOutside() {
  assertInvalid(PRELUDE + "{fn get() => readInt();} get()");
  }

  @Test
  public void functionBeforeBlockIsVisibleInsideBlock() {
  assertValidWithPrelude("fn get() => readInt(); { get() }", "33", 33);
  }

  @Test
  public void structInsideBlockDoesNotLeakOutside() {
  assertInvalid(PRELUDE + "{struct S { f : I32 } } S");
  }

  @Test
  public void structBeforeBlockIsVisibleInsideBlock() {
  assertValidWithPrelude("struct S { f : I32 } { let s = S { readInt() }; s.f }", "12", 12);
  }

  @Test
  public void assignmentInsideBlockCanMutateOuter() {
  assertValidWithPrelude("let mut x = 0; { x = readInt(); } x", "77", 77);
  }

  @Test
  public void nestedBlocksValue() {
  assertValidWithPrelude("{{{5}}}", "", 5);
  }

  @Test
  public void nestedBlocksWithLetsScoping() {
  assertInvalid(PRELUDE + "{ let x = 3; { let y = 4; } x + y }");
  }
}
